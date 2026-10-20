package com.bradesco.service;

import com.bradesco.dto.response.ResumoFinanceiroDTO;
import com.bradesco.dto.response.TransacaoResponseDTO;
import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class ConsultaFinanceiraService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public ConsultaFinanceiraService(TransacaoRepository transacaoRepository,
                                     UsuarioRepository usuarioRepository) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private void validarUsuario(Long usuarioId) {
        usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    // Feature 01 — resumo consolidado (totais, saldo e indicadores)
    public ResumoFinanceiroDTO consultarResumo(Long usuarioId) {
        validarUsuario(usuarioId);
        List<Transacao> transacoes = transacaoRepository.findByUsuarioId(usuarioId);

        BigDecimal totalReceitas = somarPorTipo(transacoes, TipoTransacao.RECEITA);
        BigDecimal totalDespesas = somarPorTipo(transacoes, TipoTransacao.DESPESA);
        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        // Versão inicial até o Epic 07 (quando houver renda formal):
        // economia acumulada = o próprio saldo.
        BigDecimal economiaAcumulada = saldo;

        // Versão inicial até o Epic 07: comprometimento = despesas / receitas * 100.
        BigDecimal percentualComprometimentoRenda;
        if (totalReceitas.signum() == 0) {
            percentualComprometimentoRenda = BigDecimal.ZERO;
        } else {
            percentualComprometimentoRenda = totalDespesas
                .multiply(BigDecimal.valueOf(100))
                .divide(totalReceitas, 2, RoundingMode.HALF_UP);
        }

        return new ResumoFinanceiroDTO(
            totalReceitas, totalDespesas, saldo,
            economiaAcumulada, percentualComprometimentoRenda);
    }

    // Feature 02 — filtrar movimentações (todos os filtros opcionais)
    public List<TransacaoResponseDTO> filtrar(Long usuarioId, LocalDate dataInicio, LocalDate dataFim,
                                              Long categoriaId, TipoTransacao tipo,
                                              String estabelecimento, String recorrencia) {
        validarUsuario(usuarioId);
        return transacaoRepository.findByUsuarioId(usuarioId).stream()
            .filter(t -> dataInicio == null || !t.getDataTransacao().isBefore(dataInicio))
            .filter(t -> dataFim == null || !t.getDataTransacao().isAfter(dataFim))
            .filter(t -> categoriaId == null
                || (t.getCategoria() != null && categoriaId.equals(t.getCategoria().getId())))
            .filter(t -> tipo == null || t.getTipo() == tipo)
            .filter(t -> estabelecimento == null
                || (t.getEstabelecimento() != null
                    && t.getEstabelecimento().toLowerCase().contains(estabelecimento.toLowerCase())))
            .filter(t -> recorrencia == null
                || recorrencia.equalsIgnoreCase(t.getRecorrencia()))
            .map(this::toResponse)
            .toList();
    }

    private BigDecimal somarPorTipo(List<Transacao> transacoes, TipoTransacao tipo) {
        return transacoes.stream()
            .filter(t -> t.getTipo() == tipo)
            .map(Transacao::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private TransacaoResponseDTO toResponse(Transacao t) {
        return new TransacaoResponseDTO(
            t.getId(),
            t.getDescricao(),
            t.getValor(),
            t.getDataTransacao(),
            t.getRecorrencia(),
            t.getTipo(),
            t.getCategoria() != null ? t.getCategoria().getId() : null,
            t.getUsuario() != null ? t.getUsuario().getId() : null
        );
    }
}
