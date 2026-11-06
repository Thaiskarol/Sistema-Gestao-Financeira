package com.bradesco.dto.response;

import java.math.BigDecimal;

public record AlertaOrcamentoDTO(
        Long categoriaId,
        String categoriaNome,
        String status,
        BigDecimal percentualUtilizado,
        String mensagem
) {
}