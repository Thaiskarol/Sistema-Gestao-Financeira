package com.bradesco.controller;

import com.bradesco.dto.response.CriterioScoreDTO;
import com.bradesco.dto.response.ScoreSaudeFinanceiraDTO;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.service.ScoreSaudeFinanceiraService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScoreSaudeFinanceiraController.class)
class ScoreSaudeFinanceiraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScoreSaudeFinanceiraService scoreSaudeFinanceiraService;

    @Test
    void deveRetornar200ComOScore() throws Exception {
        when(scoreSaudeFinanceiraService.consultarScore(1L)).thenReturn(
                new ScoreSaudeFinanceiraDTO(68, true, "Score financeiro calculado com sucesso.",
                        List.of(
                                new CriterioScoreDTO("Percentual de economia", 15, true),
                                new CriterioScoreDTO("Estouro de orçamento", 17, true))));

        mockMvc.perform(get("/score").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Score financeiro calculado com sucesso."))
                .andExpect(jsonPath("$.data.score").value(68))
                .andExpect(jsonPath("$.data.dadosSuficientes").value(true))
                .andExpect(jsonPath("$.data.criterios[0].nome").value("Percentual de economia"))
                .andExpect(jsonPath("$.data.criterios[0].pontos").value(15));
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
        when(scoreSaudeFinanceiraService.consultarScore(99L))
                .thenThrow(new ResourceNotFoundException("Usuário não encontrado."));

        mockMvc.perform(get("/score").param("usuarioId", "99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado."));
    }
}
