package com.bradesco.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaDTO(

        @NotBlank(message = "O código de redefinição é obrigatório.")
        String token,

        @NotBlank(message = "A nova senha é obrigatória.")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
        String novaSenha
) {}