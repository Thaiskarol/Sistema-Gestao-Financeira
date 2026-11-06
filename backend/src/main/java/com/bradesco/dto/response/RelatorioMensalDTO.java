package com.bradesco.dto.response;

import java.util.List;

public record RelatorioMensalDTO(
        String mesReferencia,
        ResumoFinanceiroDTO resumo,
        List<GrupoValorDTO> gastosPorCategoria,
        EvolucaoRelatorioDTO evolucao
) {
}
