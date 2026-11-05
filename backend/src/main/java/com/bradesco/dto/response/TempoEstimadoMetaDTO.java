package com.bradesco.dto.response;

import java.math.BigDecimal;

public record TempoEstimadoMetaDTO(
        Long metaId,
        String nome,
        BigDecimal valorRestante,
        BigDecimal valorMensalConsiderado,
        int mesesEstimados,
        String previsaoConclusao
) {
}