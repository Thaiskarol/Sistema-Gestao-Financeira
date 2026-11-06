package com.bradesco.dto.response;

import java.math.BigDecimal;

public record DistribuicaoFinanceiraDTO(
    BigDecimal rendaTotal,
    BigDecimal necessidades,
    BigDecimal desejos,
    BigDecimal investimentos
) {
}