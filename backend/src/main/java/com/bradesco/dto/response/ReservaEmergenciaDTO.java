package com.bradesco.dto.response;

import java.math.BigDecimal;

public record ReservaEmergenciaDTO(
    BigDecimal despesaMediaMensal,
    int mesesCobertura,
    BigDecimal valorIdealReserva
) {
}
