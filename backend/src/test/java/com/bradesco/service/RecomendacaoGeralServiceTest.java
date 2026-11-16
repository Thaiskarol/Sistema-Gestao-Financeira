package com.bradesco.service;

import com.bradesco.dto.response.MetaSugeridaDTO;
import com.bradesco.dto.response.RecomendacaoGeralDTO;
import com.bradesco.dto.response.RecomendacaoInvestimentoDTO;
import com.bradesco.dto.response.ReservaEmergenciaDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecomendacaoGeralServiceTest {

    @Mock
    private RecomendacaoInvestimentoService recomendacaoInvestimentoService;

    @Mock
    private PatrimonioService patrimonioService;

    @InjectMocks
    private RecomendacaoGeralService recomendacaoGeralService;

    @Test
    void deveGerarRecomendacoesCompletas() {
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
                ),
                new MetaSugeridaDTO(
                        "Investimento anual",
                        new BigDecimal("9600.00")
                )
        );

        when(recomendacaoInvestimentoService
                .recomendarPercentualMinimo(usuarioId))
                .thenReturn(investimento);

        when(recomendacaoInvestimentoService
                .calcularReservaEmergencia(usuarioId))
                .thenReturn(reserva);

        when(patrimonioService
                .metasSugeridas(usuarioId))
                .thenReturn(metas);

        RecomendacaoGeralDTO resultado =
                recomendacaoGeralService.gerarRecomendacoes(usuarioId);

        assertEquals(
                new BigDecimal("4000.00"),
                resultado.rendaIdentificada()
        );

        assertEquals(investimento, resultado.investimento());
        assertEquals(reserva, resultado.reserva());
        assertEquals(metas, resultado.metasSugeridas());

        assertTrue(
                resultado.insight().contains("800.00")
        );

        verify(recomendacaoInvestimentoService)
                .recomendarPercentualMinimo(usuarioId);

        verify(recomendacaoInvestimentoService)
                .calcularReservaEmergencia(usuarioId);

        verify(patrimonioService)
                .metasSugeridas(usuarioId);
    }
}