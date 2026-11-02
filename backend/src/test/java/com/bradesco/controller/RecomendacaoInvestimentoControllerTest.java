package com.bradesco.controller;

import com.bradesco.dto.response.RecomendacaoInvestimentoDTO;
import com.bradesco.dto.response.ReservaEmergenciaDTO;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.service.RecomendacaoInvestimentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecomendacaoInvestimentoController.class)
public class RecomendacaoInvestimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecomendacaoInvestimentoService recomendacaoInvestimentoService;

    @Test
    void deveRetornar200ParaRecomendacaoDeInvestimento() throws Exception {
        when(recomendacaoInvestimentoService.recomendarPercentualMinimo(1L))
                .thenReturn(new RecomendacaoInvestimentoDTO(
                        new BigDecimal("5000.00"), new BigDecimal("20"), new BigDecimal("1000.00")));

        mockMvc.perform(get("/recomendacoes/investimento").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Recomendação de investimento gerada com sucesso."))
                .andExpect(jsonPath("$.data.rendaIdentificada").value(5000.0))
                .andExpect(jsonPath("$.data.percentualRecomendado").value(20))
                .andExpect(jsonPath("$.data.valorRecomendado").value(1000.0));
    }

    @Test
    void deveRetornar200ParaReservaDeEmergencia() throws Exception {
        when(recomendacaoInvestimentoService.calcularReservaEmergencia(1L))
                .thenReturn(new ReservaEmergenciaDTO(
                        new BigDecimal("1000.00"), 6, new BigDecimal("6000.00")));

        mockMvc.perform(get("/recomendacoes/reserva-emergencia").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Valor ideal de reserva de emergência calculado com sucesso."))
                .andExpect(jsonPath("$.data.mesesCobertura").value(6))
                .andExpect(jsonPath("$.data.valorIdealReserva").value(6000.0));
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
        when(recomendacaoInvestimentoService.recomendarPercentualMinimo(99L))
                .thenThrow(new ResourceNotFoundException("Usuário não encontrado."));

        mockMvc.perform(get("/recomendacoes/investimento").param("usuarioId", "99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado."));
    }
}
