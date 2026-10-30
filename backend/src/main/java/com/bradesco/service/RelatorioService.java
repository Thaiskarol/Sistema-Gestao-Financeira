package com.bradesco.service;

import java.time.YearMonth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bradesco.dto.response.EvolucaoRelatorioDTO;
import com.bradesco.dto.response.RelatorioMensalDTO;
import com.bradesco.dto.response.ResumoFinanceiroDTO;

@Service
public class RelatorioService {

    private final ConsultaFinanceiraService consultaFinanceiraService;
    private final GastosService gastosService;
    private final EvolucaoRelatorioService evolucaoRelatorioService;

    public RelatorioService(
            ConsultaFinanceiraService consultaFinanceiraService,
            GastosService gastosService,
            EvolucaoRelatorioService evolucaoRelatorioService) {

        this.consultaFinanceiraService = consultaFinanceiraService;
        this.gastosService = gastosService;
        this.evolucaoRelatorioService = evolucaoRelatorioService;
    }

    @Transactional(readOnly = true)
    public RelatorioMensalDTO gerarRelatorioMensal(Long usuarioId) {

        // Cada service abaixo já valida o usuário internamente
        // (lança ResourceNotFoundException se não existir).
        ResumoFinanceiroDTO resumo =
                consultaFinanceiraService.consultarResumo(usuarioId);

        var gastosPorCategoria =
                gastosService.gastosPorCategoria(usuarioId);

        EvolucaoRelatorioDTO evolucao =
                evolucaoRelatorioService.evolucaoMensal(usuarioId);

        String mesReferencia = YearMonth.now().toString();

        return new RelatorioMensalDTO(
                mesReferencia,
                resumo,
                gastosPorCategoria,
                evolucao
        );
    }
}
