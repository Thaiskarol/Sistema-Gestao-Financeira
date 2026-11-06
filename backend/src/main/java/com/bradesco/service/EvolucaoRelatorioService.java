package com.bradesco.service;

import org.springframework.stereotype.Service;

import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bradesco.dto.response.EvolucaoRelatorioDTO;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.entity.Transacao;

@Service
public class EvolucaoRelatorioService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public EvolucaoRelatorioService(
            TransacaoRepository transacaoRepository,
            UsuarioRepository usuarioRepository) {

        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private void validarUsuario(Long usuarioId) {

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }
    }

    public EvolucaoRelatorioDTO evolucaoMensal(Long usuarioId) {

    validarUsuario(usuarioId);

    List<Transacao> transacoes =
            transacaoRepository.findByUsuarioId(usuarioId);

    Map<YearMonth, BigDecimal> despesasPorMes =
            transacoes.stream()
                    .filter(transacao -> transacao.getTipo() == TipoTransacao.DESPESA)
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

    BigDecimal totalMesAtual =
            despesasPorMes.getOrDefault(mesAtual, BigDecimal.ZERO);

 BigDecimal totalMesAnterior =
        despesasPorMes.getOrDefault(mesAnterior, BigDecimal.ZERO);

if (totalMesAnterior.compareTo(BigDecimal.ZERO) == 0) {

    return new EvolucaoRelatorioDTO(
            totalMesAtual,
            totalMesAnterior,
            BigDecimal.ZERO,
            "Sem base de comparação do mês anterior."
    );
}

BigDecimal variacao = totalMesAtual
        .subtract(totalMesAnterior)
        .multiply(BigDecimal.valueOf(100))
        .divide(totalMesAnterior, 2, java.math.RoundingMode.HALF_UP);

BigDecimal percentualDescricao = variacao.abs();

String descricao;

if (variacao.compareTo(BigDecimal.ZERO) > 0) {

    descricao = "Gastos "
            + percentualDescricao
            + "% maiores que o mês anterior.";

} else if (variacao.compareTo(BigDecimal.ZERO) < 0) {

    descricao = "Gastos "
            + percentualDescricao
            + "% menores que o mês anterior.";

} else {

    descricao = "Gastos iguais ao mês anterior.";
}

return new EvolucaoRelatorioDTO(
        totalMesAtual,
        totalMesAnterior,
        variacao,
        descricao
);
  }  
}
