package com.bradesco.controller;

import static org.mockito.ArgumentMatchers.eq;
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

import com.bradesco.dto.response.ComparativoMensalDTO;
import com.bradesco.dto.response.ValorMensalDTO;
import com.bradesco.service.EvolucaoService;

@WebMvcTest(EvolucaoController.class)
class EvolucaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EvolucaoService evolucaoService;

    @Test
    void deveRetornarEvolucaoDeDespesas() throws Exception {

        List<ValorMensalDTO> dados = List.of(
                new ValorMensalDTO(
                        "2026-09",
                        new BigDecimal("300.00")),
                new ValorMensalDTO(
                        "2026-10",
                        new BigDecimal("150.00"))
        );

        when(evolucaoService.evolucaoMensalDespesas(1L))
                .thenReturn(dados);

        mockMvc.perform(get("/evolucao/despesas")
                .param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].mes").value("2026-09"))
                .andExpect(jsonPath("$.data[0].valor").value(300.00))
                .andExpect(jsonPath("$.data[1].mes").value("2026-10"))
                .andExpect(jsonPath("$.data[1].valor").value(150.00));
    }

    @Test
    void deveRetornarEvolucaoDeReceitas() throws Exception {

        List<ValorMensalDTO> dados = List.of(
                new ValorMensalDTO(
                        "2026-09",
                        new BigDecimal("2500.00")),
                new ValorMensalDTO(
                        "2026-10",
                        new BigDecimal("2500.00"))
        );

        when(evolucaoService.evolucaoMensalReceitas(1L))
                .thenReturn(dados);

        mockMvc.perform(get("/evolucao/receitas")
                .param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].mes").value("2026-09"))
                .andExpect(jsonPath("$.data[0].valor").value(2500.00))
                .andExpect(jsonPath("$.data[1].mes").value("2026-10"))
                .andExpect(jsonPath("$.data[1].valor").value(2500.00));
    }

    @Test
    void deveRetornarComparativoMensal() throws Exception {

        ComparativoMensalDTO dados = new ComparativoMensalDTO(
                "2026-09",
                new BigDecimal("1000.00"),
                "2026-10",
                new BigDecimal("1300.00"),
                new BigDecimal("300.00"));

        when(evolucaoService.compararMeses(
                eq(1L),
                eq("2026-09"),
                eq("2026-10")))
                .thenReturn(dados);

        mockMvc.perform(get("/evolucao/comparativo")
                .param("usuarioId", "1")
                .param("mesA", "2026-09")
                .param("mesB", "2026-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.mesA").value("2026-09"))
                .andExpect(jsonPath("$.data.valorA").value(1000.00))
                .andExpect(jsonPath("$.data.mesB").value("2026-10"))
                .andExpect(jsonPath("$.data.valorB").value(1300.00))
                .andExpect(jsonPath("$.data.variacao").value(300.00));
    }
}