package com.bradesco.gestao_financeira.controller;

import com.bradesco.controller.TransacaoController;
import com.bradesco.dto.request.TransacaoRequestDTO;
import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.service.TransacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bradesco.entity.Categoria;
import com.bradesco.entity.Usuario;
import org.mockito.Mockito;

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

                Usuario usuario = Mockito.mock(Usuario.class);
when(usuario.getId()).thenReturn(1L);

Categoria categoria = Mockito.mock(Categoria.class);
when(categoria.getId()).thenReturn(1L);

Transacao transacao = new Transacao();

transacao.setDescricao("Salário");
transacao.setValor(new BigDecimal("1000.00"));
transacao.setDataTransacao(LocalDate.now());
transacao.setTipo(TipoTransacao.RECEITA);
transacao.setUsuario(usuario);
transacao.setCategoria(categoria);

when(transacaoService.cadastrar(any())).thenReturn(transacao);

        when(transacaoService.cadastrar(any())).thenReturn(transacao);

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

    @TestConfiguration 
static class TestConfig{

    @Bean 
    ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json().build();
    }
}
}

