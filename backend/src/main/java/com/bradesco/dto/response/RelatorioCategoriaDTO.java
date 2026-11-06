package com.bradesco.dto.response;

import java.math.BigDecimal;

public record RelatorioCategoriaDTO(
    String categoria,
    BigDecimal valor,
    BigDecimal percentual
) {} 
