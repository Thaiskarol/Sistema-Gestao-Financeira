package com.bradesco.controller;

import com.bradesco.dto.response.RendaResponseDTO;
import com.bradesco.enums.TipoRenda;
import com.bradesco.service.RendaService;
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

@WebMvcTest(RendaController.class)
class RendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RendaService rendaService;

    @Test
    void deveRetornar201QuandoCadastroValido() throws Exception {
        RendaResponseDTO resposta = new RendaResponseDTO(
                1L, "Salário mensal", new BigDecimal("3000.00"), TipoRenda.SALARIO_LIQUIDO, 1L);
        when(rendaService.cadastrar(any())).thenReturn(resposta);

        String json = """
                {
                  "descricao": "Salário mensal",
                  "valor": 3000.00,
                  "tipo": "SALARIO_LIQUIDO",
                  "usuarioId": 1
                }
                """;

        mockMvc.perform(post("/rendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tipo").value("SALARIO_LIQUIDO"));
    }

    @Test
    void deveRetornar400QuandoValorInvalido() throws Exception {
        String json = """
                {
                  "descricao": "Salário mensal",
                  "valor": -100.00,
                  "tipo": "SALARIO_LIQUIDO",
                  "usuarioId": 1
                }
                """;

        mockMvc.perform(post("/rendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deveRetornar201AoCadastrarReceitaRecorrente() throws Exception {
        RendaResponseDTO resposta = new RendaResponseDTO(
                2L, "Aluguel recebido", new BigDecimal("1200.00"), TipoRenda.RECORRENTE, 1L);
        when(rendaService.cadastrarReceitaRecorrente(any())).thenReturn(resposta);

        String json = """
                {
                  "descricao": "Aluguel recebido",
                  "valor": 1200.00,
                  "tipo": "RECORRENTE",
                  "usuarioId": 1
                }
                """;

        mockMvc.perform(post("/rendas/recorrente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Receita recorrente cadastrada com sucesso."))
                .andExpect(jsonPath("$.data.tipo").value("RECORRENTE"));
    }

    @Test
    void deveRetornar201AoCadastrarReceitaVariavel() throws Exception {
        RendaResponseDTO resposta = new RendaResponseDTO(
                3L, "Freelance", new BigDecimal("800.00"), TipoRenda.VARIAVEL, 1L);
        when(rendaService.cadastrarReceitaVariavel(any())).thenReturn(resposta);

        String json = """
                {
                  "descricao": "Freelance",
                  "valor": 800.00,
                  "tipo": "VARIAVEL",
                  "usuarioId": 1
                }
                """;

        mockMvc.perform(post("/rendas/variavel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Receita variável cadastrada com sucesso."))
                .andExpect(jsonPath("$.data.tipo").value("VARIAVEL"));
    }
}
