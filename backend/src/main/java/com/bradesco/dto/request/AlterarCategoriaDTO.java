package com.bradesco.dto.request;

import jakarta.validation.constraints.NotNull;

public record AlterarCategoriaDTO(

    @NotNull(message = "A nova categoria é obrigatória.")
    Long novaCategoriaId

) {}
