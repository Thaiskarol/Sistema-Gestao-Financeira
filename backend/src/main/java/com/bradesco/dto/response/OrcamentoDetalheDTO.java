package com.bradesco.dto.response;

import java.math.BigDecimal;

public record OrcamentoDetalheDTO(
        Long id,
        Long categoriaId,
        String categoriaNome,
        BigDecimal limite,
        BigDecimal valorConsumido,
        BigDecimal percentualUtilizado,
        String status
) {}
