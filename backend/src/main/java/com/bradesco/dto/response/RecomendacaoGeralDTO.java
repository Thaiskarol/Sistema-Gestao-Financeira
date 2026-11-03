package com.bradesco.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record RecomendacaoGeralDTO(
        BigDecimal rendaIdentificada,
        RecomendacaoInvestimentoDTO investimento,
        ReservaEmergenciaDTO reserva,
        List<MetaSugeridaDTO> metasSugeridas,
        String insight
) {
}