package com.bradesco.dto.response;

public record CriterioScoreDTO(
        String nome,
        int pontos,
        boolean dadosSuficientes
) {}
