package com.bradesco.dto.response;

import java.math.BigDecimal;

public record ResumoFinanceiroDTO(
    BigDecimal totalReceitas,
    BigDecimal totalDespesas,
    BigDecimal saldo,
    BigDecimal economiaAcumulada,
    BigDecimal percentualComprometimentoRenda
) {
}
