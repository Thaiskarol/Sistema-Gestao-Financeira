package com.bradesco.dto.response;

import com.bradesco.enums.NivelSaudeFinanceira;

public record ClassificacaoScoreDTO(
    Long usuarioId,
    Integer score,
    NivelSaudeFinanceira nivel,
    String descricao
) {}