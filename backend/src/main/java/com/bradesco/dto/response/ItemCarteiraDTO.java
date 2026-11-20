package com.bradesco.dto.response;

import java.math.BigDecimal;

import com.bradesco.enums.TipoInvestimento;

public record ItemCarteiraDTO(
        TipoInvestimento tipo,
        String nome,
        BigDecimal valor,
        BigDecimal percentual
) {}
