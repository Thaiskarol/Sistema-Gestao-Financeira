package com.bradesco.dto.response;

import java.math.BigDecimal;

public record EvolucaoRelatorioDTO(
        BigDecimal totalMesAtual,
        BigDecimal totalMesAnterior,
        BigDecimal variacaoPercentual,
        String descricao
) {
}