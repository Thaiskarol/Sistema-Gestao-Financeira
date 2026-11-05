package com.bradesco.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MetaRequestDTO(

        @NotBlank(message = "O nome da meta é obrigatório.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
        String nome,

        @NotNull(message = "O valor objetivo é obrigatório.")
        @Positive(message = "O valor objetivo deve ser maior que zero.")
        BigDecimal valorObjetivo,

        @NotNull(message = "A data desejada é obrigatória.")
        @Future(message = "A data desejada deve ser uma data futura.")
        LocalDate dataDesejada,

        @NotNull(message = "O valor acumulado é obrigatório.")
        @PositiveOrZero(message = "O valor acumulado não pode ser negativo.")
        BigDecimal valorAcumulado,

        @NotNull(message = "O usuário é obrigatório.")
        Long usuarioId
) {
}
