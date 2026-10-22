package com.bradesco.controller;

import com.bradesco.dto.response.OrcamentoResponseDTO;
import com.bradesco.service.OrcamentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrcamentoController.class)
class OrcamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrcamentoService orcamentoService;

    @Test
    void deveRetornar201AoCriarOrcamento() throws Exception {

        OrcamentoResponseDTO resposta = new OrcamentoResponseDTO(
                1L,
                new BigDecimal("500.00"),
                1L,
                "Alimentação",
                1L
        );

        when(orcamentoService.criar(any())).thenReturn(resposta);

        String json = """
                {
                    "limite": 500.00,
                    "categoriaId": 1,
                    "usuarioId": 1
                }
                """;

        mockMvc.perform(post("/orcamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Orçamento criado com sucesso."))
                .andExpect(jsonPath("$.data.limite").value(500.00))
                .andExpect(jsonPath("$.data.categoriaNome").value("Alimentação"));
    }

    @Test
    void deveRetornar400QuandoLimiteForNulo() throws Exception {

        String json = """
                {
                    "limite": null,
                    "categoriaId": 1,
                    "usuarioId": 1
                }
                """;

        mockMvc.perform(post("/orcamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
