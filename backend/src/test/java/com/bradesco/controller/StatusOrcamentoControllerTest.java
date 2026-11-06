package com.bradesco.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bradesco.dto.response.AlertaOrcamentoDTO;
import com.bradesco.dto.response.StatusOrcamentoDTO;
import com.bradesco.service.StatusOrcamentoService;

@WebMvcTest(StatusOrcamentoController.class)
class StatusOrcamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatusOrcamentoService statusOrcamentoService;

    @Test
    void deveRetornarStatusDoOrcamento() throws Exception {

        StatusOrcamentoDTO status = new StatusOrcamentoDTO(
                1L,
                2L,
                "Alimentação",
                new BigDecimal("80.00"),
                "ATENCAO"
        );

        when(statusOrcamentoService.obterStatus(1L))
                .thenReturn(status);

        mockMvc.perform(get("/orcamentos/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Status do orçamento recuperado com sucesso."))
                .andExpect(jsonPath("$.data.orcamentoId").value(1))
                .andExpect(jsonPath("$.data.categoriaId").value(2))
                .andExpect(jsonPath("$.data.categoriaNome")
                        .value("Alimentação"))
                .andExpect(jsonPath("$.data.percentualUtilizado")
                        .value(80.00))
                .andExpect(jsonPath("$.data.status")
                        .value("ATENCAO"));
    }

    @Test
    void deveRetornarAlertaDoOrcamento() throws Exception {

        AlertaOrcamentoDTO alerta = new AlertaOrcamentoDTO(
                2L,
                "Alimentação",
                "RISCO",
                new BigDecimal("95.00"),
                "Você já utilizou 95.00% do orçamento de Alimentação."
        );

        when(statusOrcamentoService.gerarAlerta(1L))
                .thenReturn(alerta);

        mockMvc.perform(get("/orcamentos/1/alerta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Alerta do orçamento gerado com sucesso."))
                .andExpect(jsonPath("$.data.categoriaId").value(2))
                .andExpect(jsonPath("$.data.categoriaNome")
                        .value("Alimentação"))
                .andExpect(jsonPath("$.data.status")
                        .value("RISCO"))
                .andExpect(jsonPath("$.data.percentualUtilizado")
                        .value(95.00))
                .andExpect(jsonPath("$.data.mensagem")
                        .value("Você já utilizou 95.00% do orçamento de Alimentação."));
    }
}