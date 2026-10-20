package com.bradesco.controller;

import com.bradesco.controller.GastosController;
import com.bradesco.dto.response.GrupoValorDTO;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.service.GastosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GastosController.class)
public class GastosControllerTest {
  
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GastosService gastosService;

    @Test
    void deveRetornar200ParaGastosPorCategoria() throws Exception {
        when(gastosService.gastosPorCategoria(1L))
                .thenReturn(List.of(new GrupoValorDTO("Alimentação", new BigDecimal("50.00"))));

        mockMvc.perform(get("/gastos/por-categoria").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].rotulo").value("Alimentação"))
                .andExpect(jsonPath("$.data[0].valor").value(50.0));
    }

    @Test
    void deveRetornar200ParaGastosPorEstabelecimento() throws Exception {
        when(gastosService.gastosPorEstabelecimento(1L))
                .thenReturn(List.of(new GrupoValorDTO("Mercado", new BigDecimal("45.00"))));

        mockMvc.perform(get("/gastos/por-estabelecimento").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].rotulo").value("Mercado"));
    }

    @Test
    void deveRetornar200ParaGastosPorPeriodo() throws Exception {
        when(gastosService.gastosPorPeriodo(
                1L, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31)))
                .thenReturn(new GrupoValorDTO("Período", new BigDecimal("80.00")));

        mockMvc.perform(get("/gastos/por-periodo")
                        .param("usuarioId", "1")
                        .param("dataInicio", "2026-10-01")
                        .param("dataFim", "2026-10-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rotulo").value("Período"))
                .andExpect(jsonPath("$.data.valor").value(80.0));
    }

    @Test
    void deveRetornar400QuandoFaltaUsuarioId() throws Exception {
        mockMvc.perform(get("/gastos/por-categoria"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deveRetornar400QuandoDataInvalida() throws Exception {
        mockMvc.perform(get("/gastos/por-periodo")
                        .param("usuarioId", "1")
                        .param("dataInicio", "abc")
                        .param("dataFim", "2026-10-31"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
        when(gastosService.gastosPorCategoria(99L))
                .thenThrow(new ResourceNotFoundException("Usuário não encontrado."));

        mockMvc.perform(get("/gastos/por-categoria").param("usuarioId", "99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado."));
    }
}
