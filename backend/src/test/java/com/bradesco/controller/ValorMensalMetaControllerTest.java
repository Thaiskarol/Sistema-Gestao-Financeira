package com.bradesco.controller;

import com.bradesco.dto.response.ValorMensalMetaDTO;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.service.ValorMensalMetaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ValorMensalMetaController.class)
class ValorMensalMetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ValorMensalMetaService valorMensalMetaService;

    @Test
    void deveRetornar200ComOValorMensalNecessario() throws Exception {
        when(valorMensalMetaService.consultarValorMensal(7L, 1L)).thenReturn(
                new ValorMensalMetaDTO(7L, "Viagem",
                        new BigDecimal("10000.00"), new BigDecimal("1000.00"),
                        new BigDecimal("9000.00"), LocalDate.of(2027, 10, 30),
                        12, new BigDecimal("750.00")));

        mockMvc.perform(get("/metas/7/valor-mensal").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Valor mensal necessário calculado com sucesso."))
                .andExpect(jsonPath("$.data.nome").value("Viagem"))
                .andExpect(jsonPath("$.data.mesesRestantes").value(12))
                .andExpect(jsonPath("$.data.valorMensalNecessario").value(750.0));
    }

    @Test
    void deveRetornar404QuandoMetaNaoExiste() throws Exception {
        when(valorMensalMetaService.consultarValorMensal(99L, 1L))
                .thenThrow(new ResourceNotFoundException("Meta não encontrada."));

        mockMvc.perform(get("/metas/99/valor-mensal").param("usuarioId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Meta não encontrada."));
    }
}