package com.bradesco.gestao_financeira.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TransacaoRequestDTO(
  
    @NotBlank(message = "A descrição é obrigatória.")
        @Size(max = 150, message = "A descrição deve ter no máximo 150 caracteres.")
        String descricao,

        @NotNull(message = "O valor é obrigatório.")
        @Positive(message = "O valor deve ser maior que zero.")
        BigDecimal valor,

        @NotNull(message = "A data é obrigatória.")
        LocalDate data,

        @NotNull(message = "O tipo da transação é obrigatório.")
        //TipoTransacao tipo,

        Long categoriaId,

        @Size(max = 150, message = "O estabelecimento deve ter no máximo 150 caracteres.")
        String estabelecimento,

        Boolean recorrente
) {

}
