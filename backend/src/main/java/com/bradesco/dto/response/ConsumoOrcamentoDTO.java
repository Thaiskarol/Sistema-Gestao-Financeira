package com.bradesco.dto.response;

import java.math.BigDecimal;

public record ConsumoOrcamentoDTO (

    Long orcamentoId,
    Long categoriaId,
    String categoriaNome,
    BigDecimal limite,
    BigDecimal valorConsumido,
    BigDecimal percentualUtilizado,
    BigDecimal saldoRestante

) {}
