package com.bradesco.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrcamentoAtualizacaoDTO(

        @NotNull(message = "O limite é obrigatório.")
        @Positive(message = "O limite deve ser maior que zero.")
        @Digits(integer = 8, fraction = 2,
                message = "O limite deve ter no máximo 8 dígitos inteiros e 2 casas decimais.")
        BigDecimal limite,

        @NotNull(message = "O usuário é obrigatório.")
        Long usuarioId
) {}
