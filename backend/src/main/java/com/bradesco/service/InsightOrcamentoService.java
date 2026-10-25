package com.bradesco.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bradesco.dto.response.ConsumoOrcamentoDTO;
import com.bradesco.dto.response.InsightDTO;

@Service
public class InsightOrcamentoService {

    private static final BigDecimal NOVENTA = BigDecimal.valueOf(90);
    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private final ConsumoOrcamentoService consumoOrcamentoService;

    public InsightOrcamentoService(ConsumoOrcamentoService consumoOrcamentoService) {
        this.consumoOrcamentoService = consumoOrcamentoService;
    }

    // Feature 01 — alerta de orçamento PRÓXIMO do limite (faixa 90% a 100%)
    public Optional<InsightDTO> orcamentoProximoDoLimite(Long orcamentoId) {

        ConsumoOrcamentoDTO consumo = consumoOrcamentoService.calcularConsumo(orcamentoId);
        BigDecimal percentual = consumo.percentualUtilizado();

        boolean dentroDaFaixa = percentual.compareTo(NOVENTA) >= 0
                && percentual.compareTo(CEM) <= 0;

        if (!dentroDaFaixa) {
            return Optional.empty();
        }

        InsightDTO insight = new InsightDTO(
                "ORCAMENTO_PROXIMO",
                consumo.categoriaNome(),
                percentual,
                "Você já utilizou " + percentual + "% do orçamento de "
                        + consumo.categoriaNome() + " neste mês."
        );
        return Optional.of(insight);
    }

    // Feature 02 — alerta de orçamento EXCEDIDO (passou de 100%)
    public Optional<InsightDTO> orcamentoExcedido(Long orcamentoId) {

        ConsumoOrcamentoDTO consumo = consumoOrcamentoService.calcularConsumo(orcamentoId);
        BigDecimal percentual = consumo.percentualUtilizado();

        if (percentual.compareTo(CEM) <= 0) {
            return Optional.empty();
        }

        BigDecimal valorExcedente = consumo.valorConsumido().subtract(consumo.limite());

        InsightDTO insight = new InsightDTO(
                "ORCAMENTO_EXCEDIDO",
                consumo.categoriaNome(),
                percentual,
                "Seu gasto com " + consumo.categoriaNome()
                        + " ultrapassou o limite definido em R$ " + valorExcedente + "."
        );
        return Optional.of(insight);
    }
}
