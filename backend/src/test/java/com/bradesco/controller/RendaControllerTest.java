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
}
