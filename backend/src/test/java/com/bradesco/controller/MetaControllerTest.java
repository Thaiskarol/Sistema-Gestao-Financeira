package com.bradesco.controller;

import com.bradesco.dto.response.MetaResponseDTO;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.service.MetaService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MetaController.class)
class MetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MetaService metaService;

    private MetaResponseDTO metaViagem(BigDecimal acumulado) {
        return new MetaResponseDTO(
                1L, "Viagem", new BigDecimal("12000.00"), acumulado,
                LocalDate.now().plusMonths(12), LocalDate.now(), 1L);
    }

    private String jsonMeta(String valorObjetivo, String dataDesejada) {
        return """
                {
                  "nome": "Viagem",
                  "valorObjetivo": %s,
                  "dataDesejada": "%s",
                  "valorAcumulado": 3000.00,
                  "usuarioId": 1
                }
                """.formatted(valorObjetivo, dataDesejada);
    }

    @Test
    void deveRetornar201QuandoCriacaoValida() throws Exception {
        when(metaService.criar(any())).thenReturn(metaViagem(new BigDecimal("3000.00")));

        mockMvc.perform(post("/metas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMeta("12000.00", LocalDate.now().plusMonths(12).toString())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Meta criada com sucesso."))
                .andExpect(jsonPath("$.data.nome").value("Viagem"));
    }

    @Test
    void deveRetornar400QuandoValorObjetivoZero() throws Exception {
        mockMvc.perform(post("/metas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMeta("0", LocalDate.now().plusMonths(12).toString())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deveRetornar400QuandoDataDesejadaNoPassado() throws Exception {
        mockMvc.perform(post("/metas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMeta("12000.00", LocalDate.now().minusDays(1).toString())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deveRetornar200AoListarMetas() throws Exception {
        when(metaService.listarPorUsuario(1L)).thenReturn(List.of(metaViagem(new BigDecimal("3000.00"))));

        mockMvc.perform(get("/metas").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Metas recuperadas com sucesso."))
                .andExpect(jsonPath("$.data[0].nome").value("Viagem"));
    }

    @Test
    void deveRetornar200AoBuscarMeta() throws Exception {
        when(metaService.buscar(1L, 1L)).thenReturn(metaViagem(new BigDecimal("3000.00")));

        mockMvc.perform(get("/metas/1").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Meta recuperada com sucesso."));
    }

    @Test
    void deveRetornar404QuandoMetaNaoExiste() throws Exception {
        when(metaService.buscar(9L, 1L)).thenThrow(new ResourceNotFoundException("Meta não encontrada."));

        mockMvc.perform(get("/metas/9").param("usuarioId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Meta não encontrada."));
    }

    @Test
    void deveRetornar200AoAtualizarValorAcumulado() throws Exception {
        when(metaService.atualizarValorAcumulado(any(), any()))
                .thenReturn(metaViagem(new BigDecimal("6000.00")));

        String json = """
                {
                  "usuarioId": 1,
                  "valorAcumulado": 6000.00
                }
                """;

        mockMvc.perform(patch("/metas/1/valor-acumulado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Valor acumulado atualizado com sucesso."))
                .andExpect(jsonPath("$.data.valorAcumulado").value(6000.0));
    }

    @Test
    void deveRetornar400QuandoValorAcumuladoNegativo() throws Exception {
        String json = """
                {
                  "usuarioId": 1,
                  "valorAcumulado": -10.00
                }
                """;

        mockMvc.perform(patch("/metas/1/valor-acumulado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
