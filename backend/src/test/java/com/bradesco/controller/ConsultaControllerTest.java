package com.bradesco.controller;

import com.bradesco.dto.response.ResumoFinanceiroDTO;
import com.bradesco.service.ConsultaFinanceiraService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConsultaController.class)
class ConsultaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultaFinanceiraService consultaFinanceiraService;

    @Test
    void deveRetornar200AoConsultarResumo() throws Exception {
        when(consultaFinanceiraService.consultarResumo(1L)).thenReturn(
            new ResumoFinanceiroDTO(
                new BigDecimal("1000.00"), new BigDecimal("300.00"),
                new BigDecimal("700.00"), new BigDecimal("700.00"),
                new BigDecimal("30.00")));

        mockMvc.perform(get("/consulta/resumo").param("usuarioId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Resumo financeiro recuperado com sucesso."))
            .andExpect(jsonPath("$.data.saldo").value(700.00));
    }

    @Test
    void deveRetornar200AoFiltrarMovimentacoes() throws Exception {
        when(consultaFinanceiraService.filtrar(
                eq(1L), any(), any(), any(), any(), any(), any()))
            .thenReturn(List.of());

        mockMvc.perform(get("/consulta/filtro").param("usuarioId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Movimentações filtradas com sucesso."));
    }
}
