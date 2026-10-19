package com.bradesco.dto.request;

import com.bradesco.enums.TipoTransacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoRequestDTO(

        @NotBlank(message = "A descrição é obrigatória.")
        @Size(max = 350, message = "A descrição deve ter no máximo 350 caracteres.")
        String descricao,

        String estabelecimento,

        @NotNull(message = "O valor é obrigatório.")
        @Positive(message = "O valor deve ser maior que zero.")
        BigDecimal valor,

        @NotNull(message = "A data da transação é obrigatória.")
        LocalDate dataTransacao,

        String recorrencia,

        @NotNull(message = "O tipo da transação é obrigatório.")
        TipoTransacao tipo,

        @NotNull(message = "A categoria é obrigatória.")
        Long categoriaId,

        @NotNull(message = "O usuário é obrigatório.")
        Long usuarioId

) {}