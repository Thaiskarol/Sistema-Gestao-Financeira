package com.bradesco.dto.response;

public record CategoriaResponseDTO(
        Long id,
        String nome,
        boolean padrao
) {

    // Compatibilidade com o código antigo: sem informar, considera categoria padrão
    public CategoriaResponseDTO(Long id, String nome) {
        this(id, nome, true);
    }
}