package com.bradesco.controller;

import com.bradesco.dto.response.InsightDTO;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.service.AumentoGastosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AumentoGastosController.class)
public class AumentoGastosControllerTest {
      @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AumentoGastosService aumentoGastosService;

    @Test
    void deveRetornar200ComInsightQuandoHaAumento() throws Exception {
        when(aumentoGastosService.analisarAumento(1L)).thenReturn(Optional.of(
                new InsightDTO("AUMENTO_GASTOS", "Geral", new BigDecimal("50.00"),
                        "Seus gastos aumentaram 50.00% em relação ao mês anterior.")));

        mockMvc.perform(get("/insights/aumento-gastos").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Análise de aumento de gastos realizada com sucesso."))
                .andExpect(jsonPath("$.data.tipo").value("AUMENTO_GASTOS"))
                .andExpect(jsonPath("$.data.percentual").value(50.0));
    }

    @Test
    void deveRetornar200SemDadosQuandoNaoHaAumento() throws Exception {
        when(aumentoGastosService.analisarAumento(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/insights/aumento-gastos").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
        when(aumentoGastosService.analisarAumento(99L))
                .thenThrow(new ResourceNotFoundException("Usuário não encontrado."));

        mockMvc.perform(get("/insights/aumento-gastos").param("usuarioId", "99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado."));
    }
}
