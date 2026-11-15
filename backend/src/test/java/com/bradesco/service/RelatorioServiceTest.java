package com.bradesco.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bradesco.dto.response.EvolucaoRelatorioDTO;
import com.bradesco.dto.response.GrupoValorDTO;
import com.bradesco.dto.response.RelatorioMensalDTO;
import com.bradesco.dto.response.ResumoFinanceiroDTO;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    private ConsultaFinanceiraService consultaFinanceiraService;

    @Mock
    private GastosService gastosService;

    @Mock
    private EvolucaoRelatorioService evolucaoRelatorioService;

    @InjectMocks
    private RelatorioService relatorioService;

    @Test
    void deveMontarRelatorioMensalComOsTresBlocos() {
        Long usuarioId = 1L;
        YearMonth mes = YearMonth.now();

        ResumoFinanceiroDTO resumo = new ResumoFinanceiroDTO(
                new BigDecimal("1000.00"),
                new BigDecimal("400.00"),
                new BigDecimal("600.00"),
                new BigDecimal("600.00"),
                new BigDecimal("40.00"));

        List<GrupoValorDTO> categorias = List.of(
                new GrupoValorDTO("Alimentação", new BigDecimal("300.00")),
                new GrupoValorDTO("Transporte", new BigDecimal("100.00")));

        EvolucaoRelatorioDTO evolucao = new EvolucaoRelatorioDTO(
                new BigDecimal("400.00"),
                new BigDecimal("500.00"),
                new BigDecimal("-20.00"),
                "Gastos 20.00% menores que o mês anterior.");

        when(consultaFinanceiraService.consultarResumo(usuarioId, mes)).thenReturn(resumo);

when(gastosService.gastosPorCategoria(usuarioId, mes)).thenReturn(categorias);

when(evolucaoRelatorioService.evolucaoMensal(usuarioId, mes)).thenReturn(evolucao);

        RelatorioMensalDTO resultado = relatorioService.gerarRelatorioMensal(usuarioId);

        assertThat(resultado.resumo()).isEqualTo(resumo);
        assertThat(resultado.gastosPorCategoria()).hasSize(2);
        assertThat(resultado.evolucao()).isEqualTo(evolucao);
        assertThat(resultado.mesReferencia()).isNotBlank();
    }

    @Test
void deveGerarRelatorioParaMesEspecifico() {
    Long usuarioId = 1L;
    YearMonth mes = YearMonth.of(2026, 10);

    ResumoFinanceiroDTO resumo = new ResumoFinanceiroDTO(
            new BigDecimal("1000.00"),
            new BigDecimal("400.00"),
            new BigDecimal("600.00"),
            new BigDecimal("600.00"),
            new BigDecimal("40.00"));

    List<GrupoValorDTO> categorias = List.of(
            new GrupoValorDTO("Alimentação", new BigDecimal("300.00")));

    EvolucaoRelatorioDTO evolucao = new EvolucaoRelatorioDTO(
            new BigDecimal("400.00"),
            new BigDecimal("500.00"),
            new BigDecimal("-20.00"),
            "Gastos 20.00% menores que o mês anterior.");

    when(consultaFinanceiraService.consultarResumo(usuarioId, mes))
            .thenReturn(resumo);

    when(gastosService.gastosPorCategoria(usuarioId, mes))
            .thenReturn(categorias);

    when(evolucaoRelatorioService.evolucaoMensal(usuarioId, mes))
            .thenReturn(evolucao);

    RelatorioMensalDTO resultado =
            relatorioService.gerarRelatorioMensal(usuarioId, mes);

    assertThat(resultado.mesReferencia()).isEqualTo("2026-10");
    assertThat(resultado.resumo()).isEqualTo(resumo);
    assertThat(resultado.gastosPorCategoria()).isEqualTo(categorias);
    assertThat(resultado.evolucao()).isEqualTo(evolucao);
}
}
