package com.bradesco.dto.response;

import java.math.BigDecimal;

public record StatusOrcamentoDTO(
        Long orcamentoId,
        Long categoriaId,
        String categoriaNome,
        BigDecimal percentualUtilizado,
        String status
) {
}