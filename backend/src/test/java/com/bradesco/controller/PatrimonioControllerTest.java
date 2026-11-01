package com.bradesco.controller;

import com.bradesco.dto.response.EvolucaoPatrimonialDTO;
import com.bradesco.dto.response.MetaSugeridaDTO;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.service.PatrimonioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatrimonioController.class)
public class PatrimonioControllerTest {
  
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatrimonioService patrimonioService;

    @Test
    void deveRetornar200ParaEvolucaoPatrimonial() throws Exception {
        when(patrimonioService.evolucaoPatrimonial(1L)).thenReturn(List.of(
                new EvolucaoPatrimonialDTO("2026-08", new BigDecimal("2000.00")),
                new EvolucaoPatrimonialDTO("2026-09", new BigDecimal("2500.00"))));

        mockMvc.perform(get("/patrimonio/evolucao").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Evolução patrimonial recuperada com sucesso."))
                .andExpect(jsonPath("$.data[0].mes").value("2026-08"))
                .andExpect(jsonPath("$.data[1].patrimonioAcumulado").value(2500.0));
    }

    @Test
    void deveRetornar200ParaMetasSugeridas() throws Exception {
        when(patrimonioService.metasSugeridas(1L)).thenReturn(List.of(
                new MetaSugeridaDTO("Reserva de emergência (6 meses)", new BigDecimal("30000.00")),
                new MetaSugeridaDTO("Investimento anual", new BigDecimal("12000.00"))));

        mockMvc.perform(get("/patrimonio/metas-sugeridas").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Metas sugeridas geradas com sucesso."))
                .andExpect(jsonPath("$.data[0].descricao").value("Reserva de emergência (6 meses)"))
                .andExpect(jsonPath("$.data[0].valorObjetivo").value(30000.0));
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
        when(patrimonioService.evolucaoPatrimonial(99L))
                .thenThrow(new ResourceNotFoundException("Usuário não encontrado."));

        mockMvc.perform(get("/patrimonio/evolucao").param("usuarioId", "99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado."));
    }
}
