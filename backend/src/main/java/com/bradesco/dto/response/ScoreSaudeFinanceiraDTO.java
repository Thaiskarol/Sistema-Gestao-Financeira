package com.bradesco.dto.response;

import java.util.List;

public record ScoreSaudeFinanceiraDTO(
        int score,
        boolean dadosSuficientes,
        String mensagem,
        List<CriterioScoreDTO> criterios
) {}
