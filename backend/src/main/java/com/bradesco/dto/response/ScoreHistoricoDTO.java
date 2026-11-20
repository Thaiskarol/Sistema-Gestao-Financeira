package com.bradesco.dto.response;

import com.bradesco.enums.NivelSaudeFinanceira;

public record ScoreHistoricoDTO(
        String mes,
        int score,
        NivelSaudeFinanceira nivel,
        String nivelDescricao
) {}