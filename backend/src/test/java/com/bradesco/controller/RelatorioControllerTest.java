package com.bradesco.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bradesco.dto.response.EvolucaoRelatorioDTO;
import com.bradesco.dto.response.GrupoValorDTO;
import com.bradesco.dto.response.RelatorioMensalDTO;
import com.bradesco.dto.response.ResumoFinanceiroDTO;
import com.bradesco.service.RelatorioService;

@WebMvcTest(RelatorioController.class)
class RelatorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RelatorioService relatorioService;

    @Test
    void deveRetornar200AoGerarRelatorioMensal() throws Exception {
        RelatorioMensalDTO dados = new RelatorioMensalDTO(
                "2026-10",
                new ResumoFinanceiroDTO(
                        new BigDecimal("1000.00"),
                        new BigDecimal("400.00"),
                        new BigDecimal("600.00"),
                        new BigDecimal("600.00"),
                        new BigDecimal("40.00")),
                List.of(new GrupoValorDTO("Alimentação", new BigDecimal("300.00"))),
                new EvolucaoRelatorioDTO(
                        new BigDecimal("400.00"),
                        new BigDecimal("500.00"),
                        new BigDecimal("-20.00"),
                        "Gastos 20.00% menores que o mês anterior."));

        when(relatorioService.gerarRelatorioMensal(1L)).thenReturn(dados);

        mockMvc.perform(get("/relatorios/mensal").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Relatório mensal gerado com sucesso."))
                .andExpect(jsonPath("$.data.mesReferencia").value("2026-10"))
                .andExpect(jsonPath("$.data.resumo.saldo").value(600.00))
                .andExpect(jsonPath("$.data.gastosPorCategoria[0].rotulo").value("Alimentação"));
    }
}
