
package com.bradesco.controller;

import com.bradesco.dto.response.PercentualMetaDTO;
import com.bradesco.dto.response.TempoEstimadoMetaDTO;
import com.bradesco.service.ProgressoMetaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProgressoMetaController.class)
class ProgressoMetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProgressoMetaService progressoMetaService;

    @Test
    void deveRetornar200ComPercentualAtingido() throws Exception {
        when(progressoMetaService.calcularPercentualAtingido(1L, 1L))
                .thenReturn(new PercentualMetaDTO(
                        1L,
                        "Viagem",
                        new BigDecimal("12000.00"),
                        new BigDecimal("3000.00"),
                        new BigDecimal("25.00"),
                        false
                ));

        mockMvc.perform(get("/metas/1/percentual")
                        .param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Percentual atingido calculado com sucesso."))
                .andExpect(jsonPath("$.data.nome").value("Viagem"))
                .andExpect(jsonPath("$.data.percentualAtingido").value(25.0))
                .andExpect(jsonPath("$.data.metaAtingida").value(false));
    }

    @Test
    void deveRetornar200ComTempoEstimado() throws Exception {
        when(progressoMetaService.calcularTempoEstimado(
                1L, 1L, new BigDecimal("1000")))
                .thenReturn(new TempoEstimadoMetaDTO(
                        1L,
                        "Viagem",
                        new BigDecimal("9000.00"),
                        new BigDecimal("1000.00"),
                        9,
                        "2027-08"
                ));

        mockMvc.perform(get("/metas/1/tempo-estimado")
                        .param("usuarioId", "1")
                        .param("aporteMensal", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Tempo estimado calculado com sucesso."))
                .andExpect(jsonPath("$.data.nome").value("Viagem"))
                .andExpect(jsonPath("$.data.mesesEstimados").value(9))
                .andExpect(jsonPath("$.data.valorMensalConsiderado").value(1000.0))
                .andExpect(jsonPath("$.data.previsaoConclusao").value("2027-08"));
    }
}
