package com.bradesco.dto.response;

import com.bradesco.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoResponseDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate dataTransacao,
        String recorrencia,
        TipoTransacao tipo,
        Long categoriaId,
        Long usuarioId
) {
}