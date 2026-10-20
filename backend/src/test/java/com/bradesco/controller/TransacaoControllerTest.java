package com.bradesco.controller;

import com.bradesco.dto.request.TransacaoRequestDTO;
import com.bradesco.dto.response.TransacaoResponseDTO;
import com.bradesco.entity.Categoria;
import com.bradesco.entity.Transacao;
import com.bradesco.entity.Usuario;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.service.SugestaoCategoriaService;
import com.bradesco.service.TransacaoService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransacaoController.class)
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransacaoService transacaoService;

    @MockitoBean
    private SugestaoCategoriaService sugestaoCategoriaService;

    @Test
    void deveRetornar200AoSugerirCategoria() throws Exception {

        when(sugestaoCategoriaService.sugerirCategoria("Supermercado"))
            .thenReturn("Alimentação");

        mockMvc.perform(get("/transacoes/sugestao-categoria")
                    .param("estabelecimento", "Supermercado"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message")
                    .value("Sugestão de categoria gerada com sucesso."))
            .andExpect(jsonPath("$.data")
                    .value("Alimentação"));
    }

    @Test
    void deveRetornar201AoCadastrarTransacao() throws Exception {

        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(1L);

        Categoria categoria = mock(Categoria.class);
        when(categoria.getId()).thenReturn(1L);

        Transacao transacao = new Transacao();

        transacao.setDescricao("Salário");
        transacao.setValor(new BigDecimal("1000.00"));
        transacao.setDataTransacao(LocalDate.now());
        transacao.setTipo(TipoTransacao.RECEITA);
        transacao.setUsuario(usuario);
        transacao.setCategoria(categoria);

        when(transacaoService.cadastrar(any())).thenReturn(transacao);

        String json = """
                {
                    "descricao": "Salário",
                    "valor": 1000.00,
                    "dataTransacao": "2026-10-17",
                    "recorrencia": null,
                    "tipo": "RECEITA",
                    "categoriaId": 1,
                    "usuarioId": 1
                }
                """;

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Transação cadastrada com sucesso."))
                .andExpect(jsonPath("$.data.descricao").value("Salário"))
                .andExpect(jsonPath("$.data.valor").value(1000.00));
    }

    @Test
    void deveRetornar400QuandoDadosForemInvalidos() throws Exception {

        String json = """
                {
                    "descricao": "",
                    "valor": -100,
                    "dataTransacao": null,
                    "recorrencia": null,
                    "tipo": null,
                    "categoriaId": null,
                    "usuarioId": null
                }
                """;

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deveRetornar200AoBuscarHistorico() throws Exception {

        TransacaoResponseDTO item = new TransacaoResponseDTO(
                1L,
                "Salário",
                new BigDecimal("1000.00"),
                LocalDate.now(),
                null,
                TipoTransacao.RECEITA,
                1L,
                1L
        );

        when(transacaoService.listarPorUsuario(eq(1L)))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/transacoes")
                        .param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].descricao")
                        .value("Salário"));
    }

    @Test
    void deveRetornar200AoClassificarDespesa() throws Exception {

        TransacaoResponseDTO item = new TransacaoResponseDTO(
                10L,
                "Mercado",
                new BigDecimal("200.00"),
                LocalDate.now(),
                null,
                TipoTransacao.DESPESA,
                1L,
                1L
        );

        when(transacaoService.classificar(eq(10L), eq(1L)))
                .thenReturn(item);

        mockMvc.perform(patch("/transacoes/10/categoria")
                    .param("categoriaId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Despesa classificada com sucesso."))
                .andExpect(jsonPath("$.data.categoriaId").value(1));
    }

    @Test
    void deveRetornar200AoAlterarCategoria() throws Exception {
        TransacaoResponseDTO retorno = new TransacaoResponseDTO(
            10L, "Mercado", new BigDecimal("50.00"), LocalDate.of(2026, 10, 17),
            null, TipoTransacao.DESPESA, 2L, 1L);

        when(transacaoService.alterarCategoria(10L, 2L)).thenReturn(retorno);

        mockMvc.perform(put("/transacoes/10/categoria")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"novaCategoriaId\": 2}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Categoria da despesa alterada com sucesso."))
            .andExpect(jsonPath("$.data.categoriaId").value(2));
    }

    @Test
    void deveRetornar400QuandoNovaCategoriaNaoInformada() throws Exception {
        mockMvc.perform(put("/transacoes/10/categoria")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].field").value("novaCategoriaId"));
    }
}
