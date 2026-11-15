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
    return gerarRelatorioMensal(usuarioId, YearMonth.now());
}

@Transactional(readOnly = true)
public RelatorioMensalDTO gerarRelatorioMensal(Long usuarioId, YearMonth mes) {

    ResumoFinanceiroDTO resumo =
            consultaFinanceiraService.consultarResumo(usuarioId, mes);

    var gastosPorCategoria =
            gastosService.gastosPorCategoria(usuarioId, mes);

    EvolucaoRelatorioDTO evolucao =
            evolucaoRelatorioService.evolucaoMensal(usuarioId, mes);

    return new RelatorioMensalDTO(
            mes.toString(),
            resumo,
            gastosPorCategoria,
            evolucao
    );
}
}
