package com.bradesco.dto.response;

import java.math.BigDecimal;

public record PercentualMetaDTO(
        Long metaId,
        String nome,
        BigDecimal valorObjetivo,
        BigDecimal valorAcumulado,
        BigDecimal percentualAtingido,
        boolean metaAtingida
) {
}