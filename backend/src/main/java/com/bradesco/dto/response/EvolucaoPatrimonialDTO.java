package com.bradesco.dto.response;

import java.math.BigDecimal;

public record EvolucaoPatrimonialDTO(
    String mes,
    BigDecimal patrimonioAcumulado
) {
}