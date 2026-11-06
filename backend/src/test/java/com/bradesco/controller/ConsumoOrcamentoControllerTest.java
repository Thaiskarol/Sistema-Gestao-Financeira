package com.bradesco.controller;

import com.bradesco.dto.response.ConsumoOrcamentoDTO;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.service.ConsumoOrcamentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConsumoOrcamentoController.class)
public class ConsumoOrcamentoControllerTest {
  
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ConsumoOrcamentoService consumoOrcamentoService;

  @Test
  void deveRetornar200ComOConsumoDoOrcamento() throws Exception {
      when(consumoOrcamentoService.calcularConsumo(5L)).thenReturn(
              new ConsumoOrcamentoDTO(5L, 10L, "Alimentação",
                      new BigDecimal("1000.00"), new BigDecimal("300.00"),
                      new BigDecimal("30.00"), new BigDecimal("700.00")));

      mockMvc.perform(get("/orcamentos/5/consumo"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.success").value(true))
              .andExpect(jsonPath("$.message").value("Consumo do orçamento calculado com sucesso."))
              .andExpect(jsonPath("$.data.categoriaNome").value("Alimentação"))
              .andExpect(jsonPath("$.data.percentualUtilizado").value(30.0))
              .andExpect(jsonPath("$.data.saldoRestante").value(700.0));
  }

  @Test
  void deveRetornar404QuandoOrcamentoNaoExiste() throws Exception {
      when(consumoOrcamentoService.calcularConsumo(99L))
              .thenThrow(new ResourceNotFoundException("Orçamento não encontrado."));

      mockMvc.perform(get("/orcamentos/99/consumo"))
              .andExpect(status().isNotFound())
              .andExpect(jsonPath("$.message").value("Orçamento não encontrado."));
  }
}
