package com.bradesco.service;

import com.bradesco.dto.response.GrupoValorDTO;
import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GastosService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public GastosService(TransacaoRepository transacaoRepository,
            UsuarioRepository usuarioRepository) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<GrupoValorDTO> gastosPorCategoria(Long usuarioId) {
        validarUsuario(usuarioId);
        return agrupar(despesasDoUsuario(usuarioId), t -> t.getCategoria().getNome());
    }

    @Transactional(readOnly = true)
    public List<GrupoValorDTO> gastosPorEstabelecimento(Long usuarioId) {
        validarUsuario(usuarioId);

        List<Transacao> comEstabelecimento = despesasDoUsuario(usuarioId).stream()
            .filter(t -> t.getEstabelecimento() != null
                    && !t.getEstabelecimento().isBlank())
            .toList();

        return agrupar(comEstabelecimento, Transacao::getEstabelecimento);
    }

    @Transactional(readOnly = true)
    public GrupoValorDTO gastosPorPeriodo(Long usuarioId, LocalDate dataInicio, LocalDate dataFim) {
        validarUsuario(usuarioId);

        BigDecimal total = despesasDoUsuario(usuarioId).stream()
            .filter(t -> !t.getDataTransacao().isBefore(dataInicio)
                    && !t.getDataTransacao().isAfter(dataFim))
            .map(Transacao::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new GrupoValorDTO("Período", total);
    }

    private List<Transacao> despesasDoUsuario(Long usuarioId) {
        return transacaoRepository.findByUsuarioIdAndTipo(usuarioId, TipoTransacao.DESPESA);
    }

    private List<GrupoValorDTO> agrupar(List<Transacao> despesas,
            Function<Transacao, String> chave) {
        return despesas.stream()
            .collect(Collectors.groupingBy(
                chave,
                Collectors.reducing(BigDecimal.ZERO, Transacao::getValor, BigDecimal::add)))
            .entrySet().stream()
            .map(e -> new GrupoValorDTO(e.getKey(), e.getValue()))
            .sorted(Comparator.comparing(GrupoValorDTO::valor, Comparator.reverseOrder())
                .thenComparing(GrupoValorDTO::rotulo))
            .toList();
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }
    }
}
