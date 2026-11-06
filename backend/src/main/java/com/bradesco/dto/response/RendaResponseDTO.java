package com.bradesco.dto.response;

import com.bradesco.enums.TipoRenda;
import java.math.BigDecimal;

public record RendaResponseDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        TipoRenda tipo,
        Long usuarioId
) {
}
