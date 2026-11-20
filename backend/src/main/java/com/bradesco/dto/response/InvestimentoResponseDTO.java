package com.bradesco.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.bradesco.enums.TipoInvestimento;

public record InvestimentoResponseDTO(
        Long id,
        String nome,
        TipoInvestimento tipo,
        String tipoDescricao,
        BigDecimal valor,
        LocalDate dataRegistro
) {}
