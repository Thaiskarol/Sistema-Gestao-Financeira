package com.bradesco.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.bradesco.dto.response.AlertaOrcamentoDTO;
import com.bradesco.dto.response.ConsumoOrcamentoDTO;
import com.bradesco.dto.response.StatusOrcamentoDTO;

@Service
public class StatusOrcamentoService {

    private final ConsumoOrcamentoService consumoOrcamentoService;

    public AlertaOrcamentoDTO gerarAlerta(Long orcamentoId) {

    ConsumoOrcamentoDTO consumo =
            consumoOrcamentoService.calcularConsumo(orcamentoId);

    String status = classificar(consumo.percentualUtilizado());

    String mensagem;

    if ("NORMAL".equals(status)) {
        mensagem = "Orçamento sob controle.";
    } else {
        mensagem = "Você já utilizou "
                + consumo.percentualUtilizado()
                + "% do orçamento de "
                + consumo.categoriaNome()
                + ".";
    }

    return new AlertaOrcamentoDTO(
            consumo.categoriaId(),
            consumo.categoriaNome(),
            status,
            consumo.percentualUtilizado(),
            mensagem
    );
}

    public StatusOrcamentoService(ConsumoOrcamentoService consumoOrcamentoService) {
        this.consumoOrcamentoService = consumoOrcamentoService;
    }

    private String classificar(BigDecimal percentual) {

        if (percentual.compareTo(BigDecimal.valueOf(70)) <= 0) {
            return "NORMAL";
        }

        if (percentual.compareTo(BigDecimal.valueOf(90)) < 0) {
            return "ATENCAO";
        }

        if (percentual.compareTo(BigDecimal.valueOf(100)) <= 0) {
            return "RISCO";
        }

        return "ESTOURADO";
    }

    public StatusOrcamentoDTO obterStatus(Long orcamentoId) {

        ConsumoOrcamentoDTO consumo =
                consumoOrcamentoService.calcularConsumo(orcamentoId);

        String status = classificar(consumo.percentualUtilizado());

        return new StatusOrcamentoDTO(
                consumo.orcamentoId(),
                consumo.categoriaId(),
                consumo.categoriaNome(),
                consumo.percentualUtilizado(),
                status
        );
    }
}