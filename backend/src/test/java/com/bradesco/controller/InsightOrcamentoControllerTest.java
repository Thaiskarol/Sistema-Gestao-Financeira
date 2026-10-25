package com.bradesco.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bradesco.dto.response.InsightDTO;
import com.bradesco.service.InsightOrcamentoService;

@WebMvcTest(InsightOrcamentoController.class)
class InsightOrcamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InsightOrcamentoService insightOrcamentoService;

    @Test
    void deveRetornar200AoAnalisarProximoDoLimite() throws Exception {
        when(insightOrcamentoService.orcamentoProximoDoLimite(1L)).thenReturn(
                Optional.of(new InsightDTO("ORCAMENTO_PROXIMO", "Alimentação",
                        new BigDecimal("92.00"), "mensagem de teste")));

        mockMvc.perform(get("/insights/orcamento/1/proximo-limite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deveRetornar200AoAnalisarExcedido() throws Exception {
        when(insightOrcamentoService.orcamentoExcedido(1L)).thenReturn(
                Optional.of(new InsightDTO("ORCAMENTO_EXCEDIDO", "Alimentação",
                        new BigDecimal("120.00"), "mensagem de teste")));

        mockMvc.perform(get("/insights/orcamento/1/excedido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
