package com.bradesco.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TransacaoUpdateDTO(
     @NotBlank(message = "A descrição é obrigatória.")
      @Size(max = 350, message = "A descrição deve ter no máximo 350 caracteres.")
      String descricao,

      String recorrencia,

      @NotNull(message = "A categoria é obrigatória.")
      Long categoriaId,

      @NotNull(message = "O usuário é obrigatório.")
      Long usuarioId
) {}
