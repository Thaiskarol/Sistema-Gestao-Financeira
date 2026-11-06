package com.bradesco.dto.request;

import com.bradesco.enums.TipoRenda;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record RendaRequestDTO(

        @Size(max = 150, message = "A descrição deve ter no máximo 150 caracteres.")
        String descricao,

        @NotNull(message = "O valor é obrigatório.")
        @Positive(message = "O valor deve ser maior que zero.")
        BigDecimal valor,

        @NotNull(message = "O tipo da renda é obrigatório.")
        TipoRenda tipo,

        @NotNull(message = "O usuário é obrigatório.")
        Long usuarioId

) {
}
