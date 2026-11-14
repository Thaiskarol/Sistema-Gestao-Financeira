package com.bradesco.controller;

import com.bradesco.dto.response.InsightDTO;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.service.InsightGeralService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InsightGeralController.class)
class InsightGeralControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InsightGeralService insightGeralService;

    @Test
    void deveRetornar200ComOsInsights() throws Exception {
        when(insightGeralService.listarInsights(1L)).thenReturn(List.of(
                new InsightDTO("AUMENTO_GASTOS", "Geral", new BigDecimal("50.00"),
                        "Seus gastos aumentaram 50.00% em relação ao mês anterior."),
                new InsightDTO("ORCAMENTO_PROXIMO", "Alimentação", new BigDecimal("95.00"),
                        "Você já utilizou 95.00% do orçamento de Alimentação neste mês.")));

        mockMvc.perform(get("/insights").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Insights gerados com sucesso."))
                .andExpect(jsonPath("$.data[0].tipo").value("AUMENTO_GASTOS"))
                .andExpect(jsonPath("$.data[1].categoria").value("Alimentação"));
    }

    @Test
    void deveRetornar200ComListaVaziaQuandoNaoHaInsights() throws Exception {
        when(insightGeralService.listarInsights(1L)).thenReturn(List.of());

        mockMvc.perform(get("/insights").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
        when(insightGeralService.listarInsights(99L))
                .thenThrow(new ResourceNotFoundException("Usuário não encontrado."));

        mockMvc.perform(get("/insights").param("usuarioId", "99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado."));
    }

    @Test
    void deveRetornar400QuandoFaltaUsuarioId() throws Exception {
        mockMvc.perform(get("/insights"))
                .andExpect(status().isBadRequest());
    }
}
