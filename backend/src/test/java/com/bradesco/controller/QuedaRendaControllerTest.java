package com.bradesco.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bradesco.dto.response.InsightDTO;
import com.bradesco.service.QuedaRendaService;

@WebMvcTest(QuedaRendaController.class)
class QuedaRendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuedaRendaService quedaRendaService;

    @Test
    void deveRetornar200AoAnalisarQuedaDeRenda() throws Exception {

        Long usuarioId = 1L;

        InsightDTO insight = new InsightDTO(
                "QUEDA_RENDA",
                null,
                BigDecimal.valueOf(20),
                "Sua receita mensal apresentou redução de 20% em comparação ao período anterior."
        );

        when(quedaRendaService.analisarQueda(usuarioId))
                .thenReturn(Optional.of(insight));

        mockMvc.perform(
                get("/insights/queda-renda")
                        .param("usuarioId", "1")
        ).andExpect(status().isOk());
    }
}