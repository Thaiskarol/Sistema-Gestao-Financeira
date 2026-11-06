package com.bradesco.service;

import com.bradesco.dto.response.GrupoValorDTO;
import com.bradesco.dto.response.RelatorioCategoriaDTO;
import com.bradesco.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DistribuicaoCategoriaServiceTest {

    @Mock
    private GastosService gastosService;

    @InjectMocks
    private DistribuicaoCategoriaService distribuicaoCategoriaService;

    private GrupoValorDTO grupo(String rotulo, String valor) {
        return new GrupoValorDTO(rotulo, new BigDecimal(valor));
    }

    private List<GrupoValorDTO> gastosDoMes() {
        return List.of(
                grupo("Moradia", "1800.00"),
                grupo("Alimentação", "1000.00"),
                grupo("Transporte", "600.00"),
                grupo("Lazer", "500.00"));   // total: 3900.00
    }

    @Test
    void deveCalcularValorEPercentualDeCadaCategoria() {
        when(gastosService.gastosPorCategoria(1L)).thenReturn(gastosDoMes());

        List<RelatorioCategoriaDTO> resultado =
                distribuicaoCategoriaService.distribuicaoPorCategoria(1L);

        assertEquals(4, resultado.size());

        assertEquals("Moradia", resultado.get(0).categoria());
        assertEquals(new BigDecimal("1800.00"), resultado.get(0).valor());
        assertEquals(new BigDecimal("46.15"), resultado.get(0).percentual());

        assertEquals(new BigDecimal("25.64"), resultado.get(1).percentual());
        assertEquals(new BigDecimal("15.38"), resultado.get(2).percentual());
        assertEquals(new BigDecimal("12.82"), resultado.get(3).percentual());
    }

    @Test
    void deveManterOrdemDoMaiorParaOMenor() {
        when(gastosService.gastosPorCategoria(1L)).thenReturn(gastosDoMes());

        List<RelatorioCategoriaDTO> resultado =
                distribuicaoCategoriaService.distribuicaoPorCategoria(1L);

        assertEquals(List.of("Moradia", "Alimentação", "Transporte", "Lazer"),
                resultado.stream().map(RelatorioCategoriaDTO::categoria).toList());

        for (int i = 0; i < resultado.size() - 1; i++) {
            assertTrue(resultado.get(i).percentual()
                    .compareTo(resultado.get(i + 1).percentual()) >= 0);
        }
    }

    @Test
    void deveRetornar100PorCentoQuandoHaUmaUnicaCategoria() {
        when(gastosService.gastosPorCategoria(1L))
                .thenReturn(List.of(grupo("Moradia", "1800.00")));

        List<RelatorioCategoriaDTO> resultado =
                distribuicaoCategoriaService.distribuicaoPorCategoria(1L);

        assertEquals(1, resultado.size());
        assertEquals(new BigDecimal("100.00"), resultado.get(0).percentual());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaGastos() {
        when(gastosService.gastosPorCategoria(1L)).thenReturn(List.of());

        assertTrue(distribuicaoCategoriaService.distribuicaoPorCategoria(1L).isEmpty());
    }

    @Test
    void deveRetornarListaVaziaQuandoTotalForZero() {
        when(gastosService.gastosPorCategoria(1L))
                .thenReturn(List.of(grupo("Lazer", "0.00")));

        assertTrue(distribuicaoCategoriaService.distribuicaoPorCategoria(1L).isEmpty());
    }

    @Test
    void devePropagarExcecaoQuandoUsuarioNaoExiste() {
        when(gastosService.gastosPorCategoria(99L))
                .thenThrow(new ResourceNotFoundException("Usuário não encontrado."));

        assertThrows(ResourceNotFoundException.class,
                () -> distribuicaoCategoriaService.distribuicaoPorCategoria(99L));
    }
}