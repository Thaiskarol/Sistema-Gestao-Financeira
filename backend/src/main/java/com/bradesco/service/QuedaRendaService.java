package com.bradesco.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bradesco.dto.response.InsightDTO;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.entity.Transacao;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;

@Service
public class QuedaRendaService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public QuedaRendaService(
            TransacaoRepository transacaoRepository,
            UsuarioRepository usuarioRepository) {

        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<InsightDTO> analisarQueda(Long usuarioId) {

        validarUsuario(usuarioId);

        List<Transacao> transacoes =
                transacaoRepository.findByUsuarioId(usuarioId);

        Map<YearMonth, BigDecimal> receitasPorMes =
                transacoes.stream()
                        .filter(transacao -> transacao.getTipo() == TipoTransacao.RECEITA)
                        .collect(Collectors.groupingBy(
                                transacao -> YearMonth.from(transacao.getDataTransacao()),
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        Transacao::getValor,
                                        BigDecimal::add
                                )
                        ));

        YearMonth mesAtual = YearMonth.now();
        YearMonth mesAnterior = mesAtual.minusMonths(1);

        BigDecimal receitaAtual =
        receitasPorMes.getOrDefault(mesAtual, BigDecimal.ZERO);

BigDecimal receitaAnterior =
        receitasPorMes.getOrDefault(mesAnterior, BigDecimal.ZERO);

if (receitaAnterior.compareTo(BigDecimal.ZERO) == 0) {
    return Optional.empty();
}

BigDecimal percentual = receitaAnterior
        .subtract(receitaAtual)
        .divide(receitaAnterior, 4, java.math.RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100));

if (percentual.compareTo(BigDecimal.valueOf(15)) > 0) {

    InsightDTO insight = new InsightDTO(
            "QUEDA_RENDA",
            null,
            percentual,
            "Sua receita mensal apresentou redução de "
                    + percentual
                    + "% em comparação ao período anterior."
    );

    return Optional.of(insight);
}

return Optional.empty();
    }

    private void validarUsuario(Long usuarioId) {

    if (!usuarioRepository.existsById(usuarioId)) {
        throw new ResourceNotFoundException("Usuário não encontrado.");
    }
}
}