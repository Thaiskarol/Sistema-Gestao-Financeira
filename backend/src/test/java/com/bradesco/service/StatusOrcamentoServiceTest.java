package com.bradesco.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bradesco.dto.response.AlertaOrcamentoDTO;
import com.bradesco.dto.response.ConsumoOrcamentoDTO;
import com.bradesco.dto.response.StatusOrcamentoDTO;

@ExtendWith(MockitoExtension.class)
class StatusOrcamentoServiceTest {

    @Mock
    private ConsumoOrcamentoService consumoOrcamentoService;

    private StatusOrcamentoService statusOrcamentoService;

    @BeforeEach
    void setUp() {
        statusOrcamentoService =
                new StatusOrcamentoService(consumoOrcamentoService);
    }

    private ConsumoOrcamentoDTO criarConsumo(String percentual) {
        return new ConsumoOrcamentoDTO(
                1L,
                2L,
                "Alimentação",
                new BigDecimal("1000.00"),
                new BigDecimal("500.00"),
                new BigDecimal(percentual),
                new BigDecimal("500.00")
        );
    }

    @Test
    void deveClassificarComoNormalAte70Porcento() {

        when(consumoOrcamentoService.calcularConsumo(1L))
                .thenReturn(criarConsumo("70.00"));

        StatusOrcamentoDTO resultado =
                statusOrcamentoService.obterStatus(1L);

        assertEquals("NORMAL", resultado.status());
    }

    @Test
    void deveClassificarComoAtencaoAcimaDe70EAbaixoDe90() {

        when(consumoOrcamentoService.calcularConsumo(1L))
                .thenReturn(criarConsumo("80.00"));

        StatusOrcamentoDTO resultado =
                statusOrcamentoService.obterStatus(1L);

        assertEquals("ATENCAO", resultado.status());
    }

    @Test
    void deveClassificarComoRiscoDe90A100Porcento() {

        when(consumoOrcamentoService.calcularConsumo(1L))
                .thenReturn(criarConsumo("95.00"));

        StatusOrcamentoDTO resultado =
                statusOrcamentoService.obterStatus(1L);

        assertEquals("RISCO", resultado.status());
    }

    @Test
    void deveClassificarComoEstouradoAcimaDe100Porcento() {

        when(consumoOrcamentoService.calcularConsumo(1L))
                .thenReturn(criarConsumo("120.00"));

        StatusOrcamentoDTO resultado =
                statusOrcamentoService.obterStatus(1L);

        assertEquals("ESTOURADO", resultado.status());
    }

    @Test
    void deveGerarAlertaParaRisco() {

        when(consumoOrcamentoService.calcularConsumo(1L))
                .thenReturn(criarConsumo("95.00"));

        AlertaOrcamentoDTO resultado =
                statusOrcamentoService.gerarAlerta(1L);

        assertNotNull(resultado);
        assertEquals("RISCO", resultado.status());
        assertEquals(new BigDecimal("95.00"),
                resultado.percentualUtilizado());
    }

    @Test
    void deveGerarAlertaParaEstourado() {

        when(consumoOrcamentoService.calcularConsumo(1L))
                .thenReturn(criarConsumo("120.00"));

        AlertaOrcamentoDTO resultado =
                statusOrcamentoService.gerarAlerta(1L);

        assertNotNull(resultado);
        assertEquals("ESTOURADO", resultado.status());
        assertEquals(new BigDecimal("120.00"),
                resultado.percentualUtilizado());
    }
}