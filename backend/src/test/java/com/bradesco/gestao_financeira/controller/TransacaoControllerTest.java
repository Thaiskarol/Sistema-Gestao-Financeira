package com.bradesco.gestao_financeira.controller;

import com.bradesco.controller.TransacaoController;
import com.bradesco.dto.request.TransacaoRequestDTO;
import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.service.TransacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransacaoController.class)
class TransacaoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private TransacaoService transacaoService;

        @Test
        void deveRetornar201QuandoCadastroValido() throws Exception {
                TransacaoRequestDTO dados = new TransacaoRequestDTO(
                                "Salário", new BigDecimal("1000.00"), LocalDate.now(),
                                null, TipoTransacao.RECEITA, 1L, 1L);

                when(transacaoService.cadastrar(any())).thenReturn(new Transacao());

                mockMvc.perform(post("/transacoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dados)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void deveRetornar400QuandoDescricaoVazia() throws Exception {
                TransacaoRequestDTO dados = new TransacaoRequestDTO(
                                "", new BigDecimal("1000.00"), LocalDate.now(),
                                null, TipoTransacao.RECEITA, 1L, 1L);

                mockMvc.perform(post("/transacoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dados)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false));
        }
}
