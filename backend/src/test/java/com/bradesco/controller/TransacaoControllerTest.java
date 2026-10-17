package com.bradesco.controller;

import com.bradesco.dto.request.TransacaoRequestDTO;
import com.bradesco.dto.response.TransacaoResponseDTO;
import com.bradesco.entity.Categoria;
import com.bradesco.entity.Transacao;
import com.bradesco.entity.Usuario;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.service.TransacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransacaoController.class)
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private TransacaoService transacaoService;

    @Test
    void deveRetornar201QuandoCadastroValido() throws Exception {
        TransacaoRequestDTO dados = new TransacaoRequestDTO(
                "Salário", new BigDecimal("1000.00"), LocalDate.now(),
                null, TipoTransacao.RECEITA, 1L, 1L);

        Categoria categoria = mock(Categoria.class);
        when(categoria.getId()).thenReturn(1L);

        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(1L);

        Transacao transacao = new Transacao();
        transacao.setDescricao("Salário");
        transacao.setValor(new BigDecimal("1000.00"));
        transacao.setDataTransacao(LocalDate.now());
        transacao.setTipo(TipoTransacao.RECEITA);
        transacao.setCategoria(categoria);
        transacao.setUsuario(usuario);

        when(transacaoService.cadastrar(any())).thenReturn(transacao);
        when(transacaoService.toResponse(any())).thenReturn(
                new TransacaoResponseDTO(1L, "Salário", new BigDecimal("1000.00"),
                        LocalDate.now(), null, TipoTransacao.RECEITA, 1L, 1L));

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

    @Test
    void deveRetornar200AoBuscarHistorico() throws Exception {
        TransacaoResponseDTO item = new TransacaoResponseDTO(
                1L, "Salário", new BigDecimal("1000.00"),
                LocalDate.now(), null, TipoTransacao.RECEITA, 1L, 1L);

        when(transacaoService.listarPorUsuario(eq(1L))).thenReturn(List.of(item));

        mockMvc.perform(get("/transacoes").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].descricao").value("Salário"));
    }
}
