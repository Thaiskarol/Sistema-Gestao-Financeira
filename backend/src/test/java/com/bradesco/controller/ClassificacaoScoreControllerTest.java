package com.bradesco.controller;

import com.bradesco.dto.response.ClassificacaoScoreDTO;
import com.bradesco.enums.NivelSaudeFinanceira;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.service.ClassificacaoScoreService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassificacaoScoreController.class)
class ClassificacaoScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClassificacaoScoreService classificacaoScoreService;

    @Test
    void deveRetornar200ComClassificacaoDoScore() throws Exception {

        when(classificacaoScoreService.consultarClassificacao(1L))
                .thenReturn(new ClassificacaoScoreDTO(
                        1L,
                        68,
                        NivelSaudeFinanceira.BOM,
                        "Bom"
                ));

        mockMvc.perform(get("/score/classificacao")
                        .param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Score classificado com sucesso."))
                .andExpect(jsonPath("$.data.usuarioId").value(1))
                .andExpect(jsonPath("$.data.score").value(68))
                .andExpect(jsonPath("$.data.nivel").value("BOM"))
                .andExpect(jsonPath("$.data.descricao").value("Bom"));
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExistir() throws Exception {

        when(classificacaoScoreService.consultarClassificacao(99L))
                .thenThrow(new ResourceNotFoundException(
                        "Usuário não encontrado."
                ));

        mockMvc.perform(get("/score/classificacao")
                        .param("usuarioId", "99"))
                .andExpect(status().isNotFound());
    }
}