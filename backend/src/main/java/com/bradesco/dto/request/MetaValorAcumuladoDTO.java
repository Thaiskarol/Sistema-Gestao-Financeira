package com.bradesco.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record MetaValorAcumuladoDTO(

        @NotNull(message = "O usuário é obrigatório.")
        Long usuarioId,

        @NotNull(message = "O valor acumulado é obrigatório.")
        @PositiveOrZero(message = "O valor acumulado não pode ser negativo.")
        BigDecimal valorAcumulado
) {
}
