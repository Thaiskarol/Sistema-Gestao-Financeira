package com.bradesco.dto.response;

import java.math.BigDecimal;

public record ValorMensalDTO(
        String mes,
        BigDecimal valor
) {
}