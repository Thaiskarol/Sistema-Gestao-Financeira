package com.bradesco.dto.response;

import java.math.BigDecimal;

public record ComparativoMensalDTO(
        String mesA,
        BigDecimal valorA,
        String mesB,
        BigDecimal valorB,
        BigDecimal variacao
) {
}