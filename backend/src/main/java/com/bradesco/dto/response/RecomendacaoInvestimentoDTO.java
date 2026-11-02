package com.bradesco.dto.response;

import java.math.BigDecimal;

public record RecomendacaoInvestimentoDTO(
    BigDecimal rendaIdentificada,
    BigDecimal percentualRecomendado,
    BigDecimal valorRecomendado
) {
}
