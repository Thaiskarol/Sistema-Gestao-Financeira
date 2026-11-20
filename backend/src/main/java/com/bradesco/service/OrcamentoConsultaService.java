package com.bradesco.service;

import com.bradesco.dto.request.OrcamentoAtualizacaoDTO;
import com.bradesco.dto.response.ConsumoOrcamentoDTO;
import com.bradesco.dto.response.OrcamentoDetalheDTO;
import com.bradesco.dto.response.OrcamentoResponseDTO;
import com.bradesco.dto.response.StatusOrcamentoDTO;
import com.bradesco.entity.Orcamento;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.OrcamentoRepository;
import com.bradesco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@Service
public class OrcamentoConsultaService {

    private final OrcamentoRepository orcamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConsumoOrcamentoService consumoOrcamentoService;
    private final StatusOrcamentoService statusOrcamentoService;

    public OrcamentoConsultaService(OrcamentoRepository orcamentoRepository,
                                    UsuarioRepository usuarioRepository,
                                    ConsumoOrcamentoService consumoOrcamentoService,
                                    StatusOrcamentoService statusOrcamentoService) {
        this.orcamentoRepository = orcamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.consumoOrcamentoService = consumoOrcamentoService;
        this.statusOrcamentoService = statusOrcamentoService;
    }

    // Lista os orçamentos do usuário com consumo (mês atual) e status
    @Transactional(readOnly = true)
    public List<OrcamentoDetalheDTO> listar(Long usuarioId) {

        validarUsuario(usuarioId);

        return orcamentoRepository.findByUsuarioId(usuarioId).stream()
                .sorted(Comparator.comparing(Orcamento::getId,
                        Comparator.nullsLast(Comparator.<Long>naturalOrder())))
                .map(this::toDetalhe)
                .toList();
    }

    // Altera só o limite; a categoria não muda
    @Transactional
    public OrcamentoResponseDTO atualizar(Long id, OrcamentoAtualizacaoDTO dados) {

        Orcamento orcamento = buscarDoUsuario(id, dados.usuarioId());
        orcamento.setLimite(dados.limite());

        Orcamento salvo = orcamentoRepository.save(orcamento);

        return new OrcamentoResponseDTO(
                salvo.getId(),
                salvo.getLimite(),
                salvo.getCategoria().getId(),
                salvo.getCategoria().getNome(),
                salvo.getUsuario().getId());
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {

        Orcamento orcamento = buscarDoUsuario(id, usuarioId);
        orcamentoRepository.delete(orcamento);
    }

    private OrcamentoDetalheDTO toDetalhe(Orcamento orcamento) {
        ConsumoOrcamentoDTO consumo = consumoOrcamentoService.calcularConsumo(orcamento.getId());
        StatusOrcamentoDTO status = statusOrcamentoService.obterStatus(orcamento.getId());

        return new OrcamentoDetalheDTO(
                orcamento.getId(),
                consumo.categoriaId(),
                consumo.categoriaNome(),
                consumo.limite(),
                consumo.valorConsumido(),
                consumo.percentualUtilizado(),
                status.status());
    }

    // Orçamento de outro usuário responde igual a inexistente (404), sem revelar que existe
    private Orcamento buscarDoUsuario(Long id, Long usuarioId) {
        Orcamento orcamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado."));

        if (!orcamento.getUsuario().getId().equals(usuarioId)) {
            throw new ResourceNotFoundException("Orçamento não encontrado.");
        }

        return orcamento;
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }
    }

    @Transactional(readOnly = true)
    public List<OrcamentoDetalheDTO> listar(Long usuarioId, YearMonth mes) {
        validarUsuario(usuarioId);
        return orcamentoRepository.findByUsuarioId(usuarioId).stream()
                .sorted(Comparator.comparing(Orcamento::getId,
                        Comparator.nullsLast(Comparator.<Long>naturalOrder())))
                .map(orcamento -> toDetalhe(orcamento, mes))
                .toList();
    }
    private OrcamentoDetalheDTO toDetalhe(Orcamento orcamento, YearMonth mes) {
        ConsumoOrcamentoDTO consumo = consumoOrcamentoService.calcularConsumo(orcamento.getId(), mes);
        StatusOrcamentoDTO status = statusOrcamentoService.obterStatus(orcamento.getId(), mes);

        return new OrcamentoDetalheDTO(
                orcamento.getId(),
                consumo.categoriaId(),
                consumo.categoriaNome(),
                consumo.limite(),
                consumo.valorConsumido(),
                consumo.percentualUtilizado(),
                status.status());
    }
}
