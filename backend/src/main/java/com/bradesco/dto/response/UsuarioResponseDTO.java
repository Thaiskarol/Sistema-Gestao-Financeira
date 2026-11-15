package com.bradesco.dto.response;

import java.time.LocalDate;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        LocalDate dataNascimento,
        String telefone
) {}