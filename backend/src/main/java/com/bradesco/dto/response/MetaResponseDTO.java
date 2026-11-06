package com.bradesco.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MetaResponseDTO(
        Long id,
        String nome,
        BigDecimal valorObjetivo,
        BigDecimal valorAcumulado,
        LocalDate dataDesejada,
        LocalDate dataCriacao,
        Long usuarioId
) {
}
