package com.bradesco.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ValorMensalMetaDTO(
        Long metaId,
        String nome,
        BigDecimal valorObjetivo,
        BigDecimal valorAcumulado,
        BigDecimal valorRestante,
        LocalDate dataDesejada,
        int mesesRestantes,
        BigDecimal valorMensalNecessario
) {
}
