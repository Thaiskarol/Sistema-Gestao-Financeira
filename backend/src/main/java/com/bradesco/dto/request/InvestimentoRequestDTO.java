package com.bradesco.dto.request;

import java.math.BigDecimal;

import com.bradesco.enums.TipoInvestimento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record InvestimentoRequestDTO(

        @NotBlank(message = "O nome do investimento é obrigatório.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
        String nome,

        @NotNull(message = "O tipo do investimento é obrigatório.")
        TipoInvestimento tipo,

        @NotNull(message = "O valor é obrigatório.")
        @Positive(message = "O valor deve ser maior que zero.")
        BigDecimal valor,

        @NotNull(message = "O usuário é obrigatório.")
        Long usuarioId
) {}
