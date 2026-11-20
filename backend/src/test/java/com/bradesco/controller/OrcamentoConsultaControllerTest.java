package com.bradesco.controller;

import com.bradesco.dto.response.OrcamentoDetalheDTO;
import com.bradesco.dto.response.OrcamentoResponseDTO;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.service.OrcamentoConsultaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrcamentoConsultaController.class)
class OrcamentoConsultaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrcamentoConsultaService orcamentoConsultaService;

    @Test
    void deveRetornar200AoListarOrcamentos() throws Exception {
        when(orcamentoConsultaService.listar(1L)).thenReturn(List.of(
                new OrcamentoDetalheDTO(1L, 10L, "Alimentação", new BigDecimal("2000.00"),
                        new BigDecimal("500.00"), new BigDecimal("25.00"), "NORMAL")));

        mockMvc.perform(get("/orcamentos").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Orçamentos listados com sucesso."))
                .andExpect(jsonPath("$.data[0].categoriaNome").value("Alimentação"))
                .andExpect(jsonPath("$.data[0].percentualUtilizado").value(25.0))
                .andExpect(jsonPath("$.data[0].status").value("NORMAL"));
    }

    @Test
    void deveRetornar400AoListarSemUsuarioId() throws Exception {
        mockMvc.perform(get("/orcamentos"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deveRetornar200AoAtualizarLimite() throws Exception {
        when(orcamentoConsultaService.atualizar(eq(1L), any()))
                .thenReturn(new OrcamentoResponseDTO(
                        1L, new BigDecimal("3000.00"), 10L, "Alimentação", 1L));

        mockMvc.perform(put("/orcamentos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"limite\": 3000.00, \"usuarioId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Orçamento atualizado com sucesso."))
                .andExpect(jsonPath("$.data.limite").value(3000.0))
                .andExpect(jsonPath("$.data.categoriaNome").value("Alimentação"));
    }

    @Test
    void deveRetornar400AoAtualizarComLimiteNegativo() throws Exception {
        mockMvc.perform(put("/orcamentos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"limite\": -5, \"usuarioId\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors[0].field").value("limite"));
    }

    @Test
    void deveRetornar400AoAtualizarSemLimite() throws Exception {
        mockMvc.perform(put("/orcamentos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("limite"));
    }

    @Test
    void deveRetornar404AoAtualizarOrcamentoInexistente() throws Exception {
        when(orcamentoConsultaService.atualizar(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Orçamento não encontrado."));

        mockMvc.perform(put("/orcamentos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"limite\": 3000.00, \"usuarioId\": 1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Orçamento não encontrado."));
    }

    @Test
    void deveRetornar200AoExcluirOrcamento() throws Exception {
        mockMvc.perform(delete("/orcamentos/1").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Orçamento excluído com sucesso."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void deveRetornar404AoExcluirOrcamentoDeOutroUsuarioOuInexistente() throws Exception {
        doThrow(new ResourceNotFoundException("Orçamento não encontrado."))
                .when(orcamentoConsultaService).excluir(99L, 1L);

        mockMvc.perform(delete("/orcamentos/99").param("usuarioId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Orçamento não encontrado."));
    }
    @Test
    void deveRetornar200ChamandoVersaoComMesQuandoMesInformado() throws Exception {
        YearMonth mes = YearMonth.of(2026, 10);
        when(orcamentoConsultaService.listar(1L, mes)).thenReturn(List.of(
                new OrcamentoDetalheDTO(1L, 10L, "Alimentação", new BigDecimal("2000.00"),
                        new BigDecimal("1900.00"), new BigDecimal("95.00"), "RISCO")));

        mockMvc.perform(get("/orcamentos")
                        .param("usuarioId", "1")
                        .param("mes", "2026-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].status").value("RISCO"));

        verify(orcamentoConsultaService).listar(1L, mes);
        verify(orcamentoConsultaService, never()).listar(1L);
    }

    @Test
    void deveChamarVersaoAntigaQuandoMesNaoInformado() throws Exception {
        mockMvc.perform(get("/orcamentos").param("usuarioId", "1"))
                .andExpect(status().isOk());

        verify(orcamentoConsultaService).listar(1L);
        verify(orcamentoConsultaService, never()).listar(eq(1L), any(YearMonth.class));
    }

    @Test
    void deveRetornar400QuandoMesInvalido() throws Exception {
        mockMvc.perform(get("/orcamentos")
                       .param("usuarioId", "1")
                       .param("mes", "2026-13"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("O parâmetro 'mes' tem um valor inválido."));

        verifyNoInteractions(orcamentoConsultaService);
    }
}
