package com.bradesco.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bradesco.dto.response.ConsumoOrcamentoDTO;
import com.bradesco.dto.response.InsightDTO;

@ExtendWith(MockitoExtension.class)
class InsightOrcamentoServiceTest {

    @Mock
    private ConsumoOrcamentoService consumoOrcamentoService;

    @InjectMocks
    private InsightOrcamentoService insightOrcamentoService;

    private ConsumoOrcamentoDTO consumoComPercentual(BigDecimal percentual,
            BigDecimal limite, BigDecimal valorConsumido) {
        return new ConsumoOrcamentoDTO(
                1L, 1L, "Alimentação",
                limite, valorConsumido, percentual,
                limite.subtract(valorConsumido));
    }

    @Test
    void deveGerarAlertaQuandoProximoDoLimite() {
        // 92% está na faixa 90-100 -> gera "ORCAMENTO_PROXIMO"
        when(consumoOrcamentoService.calcularConsumo(1L)).thenReturn(
                consumoComPercentual(new BigDecimal("92.00"),
                        new BigDecimal("500.00"), new BigDecimal("460.00")));

        Optional<InsightDTO> resultado = insightOrcamentoService.orcamentoProximoDoLimite(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().tipo()).isEqualTo("ORCAMENTO_PROXIMO");
    }

    @Test
    void naoDeveGerarAlertaProximoQuandoLongeDoLimite() {
        // 50% não está na faixa -> vazio
        when(consumoOrcamentoService.calcularConsumo(1L)).thenReturn(
                consumoComPercentual(new BigDecimal("50.00"),
                        new BigDecimal("500.00"), new BigDecimal("250.00")));

        Optional<InsightDTO> resultado = insightOrcamentoService.orcamentoProximoDoLimite(1L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveGerarAlertaQuandoExcedido() {
        // 120% -> "ORCAMENTO_EXCEDIDO", excedente = 600 - 500 = 100
        when(consumoOrcamentoService.calcularConsumo(1L)).thenReturn(
                consumoComPercentual(new BigDecimal("120.00"),
                        new BigDecimal("500.00"), new BigDecimal("600.00")));

        Optional<InsightDTO> resultado = insightOrcamentoService.orcamentoExcedido(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().tipo()).isEqualTo("ORCAMENTO_EXCEDIDO");
        assertThat(resultado.get().mensagem()).contains("100");
    }

    @Test
    void naoDeveGerarAlertaExcedidoQuandoDentroDoLimite() {
        // 80% -> não estourou -> vazio
        when(consumoOrcamentoService.calcularConsumo(1L)).thenReturn(
                consumoComPercentual(new BigDecimal("80.00"),
                        new BigDecimal("500.00"), new BigDecimal("400.00")));

        Optional<InsightDTO> resultado = insightOrcamentoService.orcamentoExcedido(1L);

        assertThat(resultado).isEmpty();
    }
}
