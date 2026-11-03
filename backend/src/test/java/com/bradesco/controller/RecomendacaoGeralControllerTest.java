package com.bradesco.controller;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.MetaSugeridaDTO;
import com.bradesco.dto.response.RecomendacaoGeralDTO;
import com.bradesco.dto.response.RecomendacaoInvestimentoDTO;
import com.bradesco.dto.response.ReservaEmergenciaDTO;
import com.bradesco.service.RecomendacaoGeralService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest (RecomendacaoGeralController.class)
class RecomendacaoGeralControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean 
    private RecomendacaoGeralService recomendacaoGeralService;

    @Test
    void deveRetornarRecomendacoesComSucesso() throws Exception {

        Long usuarioId = 1L;

        RecomendacaoInvestimentoDTO investimento =
                new RecomendacaoInvestimentoDTO(
                        new BigDecimal("4000.00"),
                        new BigDecimal("20"),
                        new BigDecimal("800.00")
                );

        ReservaEmergenciaDTO reserva =
                new ReservaEmergenciaDTO(
                        new BigDecimal("2000.00"),
                        6,
                        new BigDecimal("12000.00")
                );

        List<MetaSugeridaDTO> metas = List.of(
                new MetaSugeridaDTO(
                        "Reserva de emergência (6 meses)",
                        new BigDecimal("24000.00")
                )
        );

        RecomendacaoGeralDTO dto =
                new RecomendacaoGeralDTO(
                        new BigDecimal("4000.00"),
                        investimento,
                        reserva,
                        metas,
                        "Com sua renda atual, recomenda-se investir pelo menos R$ 800.00 por mês para construir sua reserva de emergência."
                );

        when(recomendacaoGeralService.gerarRecomendacoes(usuarioId))
                .thenReturn(dto);

        mockMvc.perform(
                get("/recomendacoes/geral")
                        .param("usuarioId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Recomendações financeiras geradas com sucesso."))
                .andExpect(jsonPath("$.data.rendaIdentificada")
                        .value(4000.00))
                .andExpect(jsonPath("$.data.investimento.valorRecomendado")
                        .value(800.00))
                .andExpect(jsonPath("$.data.reserva.mesesCobertura")
                        .value(6))
                .andExpect(jsonPath("$.data.metasSugeridas[0].descricao")
                        .value("Reserva de emergência (6 meses)"))
                .andExpect(jsonPath("$.data.insight")
                        .value("Com sua renda atual, recomenda-se investir pelo menos R$ 800.00 por mês para construir sua reserva de emergência."));
    }
}