package com.bradesco.dto.response;

import java.math.BigDecimal;

public record OrcamentoResponseDTO(
        Long id,
        BigDecimal limite,
        Long categoriaId,
        String categoriaNome,
        Long usuarioId
) {
}
