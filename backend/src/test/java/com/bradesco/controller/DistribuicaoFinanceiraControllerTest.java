package com.bradesco.controller;

import com.bradesco.dto.response.DistribuicaoFinanceiraDTO;
import com.bradesco.service.DistribuicaoFinanceiraService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest (DistribuicaoFinanceiraController.class)
class DistribuicaoFinanceiraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean 
    private DistribuicaoFinanceiraService distribuicaoFinanceiraService;

    @Test
    void deveRetornarDistribuicaoFinanceiraComSucesso() throws Exception {

        DistribuicaoFinanceiraDTO dto = new DistribuicaoFinanceiraDTO(
                new BigDecimal("5000.00"),
                new BigDecimal("2500.00"),
                new BigDecimal("1500.00"),
                new BigDecimal("1000.00")
        );

        when(distribuicaoFinanceiraService.calcularDistribuicao(1L))
                .thenReturn(dto);

        mockMvc.perform(
                get("/distribuicao-financeira")
                        .param("usuarioId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.rendaTotal").value(5000.00))
        .andExpect(jsonPath("$.data.necessidades").value(2500.00))
        .andExpect(jsonPath("$.data.desejos").value(1500.00))
        .andExpect(jsonPath("$.data.investimentos").value(1000.00));
    }
}