package com.bradesco.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bradesco.dto.response.InvestimentoResponseDTO;
import com.bradesco.enums.TipoInvestimento;
import com.bradesco.service.InvestimentoService;

@WebMvcTest(InvestimentoController.class)
class InvestimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InvestimentoService investimentoService;

    private InvestimentoResponseDTO respostaPadrao() {
        return new InvestimentoResponseDTO(
                1L, "Tesouro Selic", TipoInvestimento.RENDA_FIXA, "Renda fixa",
                new BigDecimal("3000.00"), LocalDate.now());
    }

    @Test
    void deveRetornar201QuandoPostValido() throws Exception {
        when(investimentoService.registrar(any())).thenReturn(respostaPadrao());

        String json = """
                {
                  "nome": "Tesouro Selic",
                  "tipo": "RENDA_FIXA",
                  "valor": 3000.00,
                  "usuarioId": 1
                }
                """;

        mockMvc.perform(post("/investimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("Tesouro Selic"))
                .andExpect(jsonPath("$.data.tipo").value("RENDA_FIXA"));
    }

    @Test
    void deveRetornar400QuandoValorZero() throws Exception {
        String json = """
                {
                  "nome": "Tesouro Selic",
                  "tipo": "RENDA_FIXA",
                  "valor": 0,
                  "usuarioId": 1
                }
                """;

        mockMvc.perform(post("/investimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deveRetornar400QuandoNomeVazio() throws Exception {
        String json = """
                {
                  "nome": "",
                  "tipo": "RENDA_FIXA",
                  "valor": 3000.00,
                  "usuarioId": 1
                }
                """;

        mockMvc.perform(post("/investimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deveRetornar200AoListar() throws Exception {
        when(investimentoService.listar(1L)).thenReturn(List.of(respostaPadrao()));

        mockMvc.perform(get("/investimentos").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].nome").value("Tesouro Selic"));
    }

    @Test
    void deveRetornar200AoExcluir() throws Exception {
        mockMvc.perform(delete("/investimentos/1").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Investimento excluído com sucesso."));
    }
}
