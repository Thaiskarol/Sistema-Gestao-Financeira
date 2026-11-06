
package com.bradesco.service;

import com.bradesco.entity.Meta;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.MetaRepository;
import com.bradesco.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressoMetaServiceTest {

    @Mock
    private MetaRepository metaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ValorMensalMetaService valorMensalMetaService;

    @InjectMocks
    private ProgressoMetaService progressoMetaService;

    private Meta criarMeta(String objetivo, String acumulado) {
        Meta meta = new Meta();
        meta.setNome("Viagem");
        meta.setValorObjetivo(new BigDecimal(objetivo));
        meta.setValorAcumulado(new BigDecimal(acumulado));
        meta.setDataDesejada(LocalDate.now().plusMonths(12));

        ReflectionTestUtils.setField(meta, "id", 1L);

        return meta;
    }

    @Test
    void deveCalcularPercentualAtingidoDe25PorCento() {
        Meta meta = criarMeta("12000.00", "3000.00");

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(metaRepository.findByIdAndUsuarioId(1L, 1L))
                .thenReturn(Optional.of(meta));

        var resultado = progressoMetaService.calcularPercentualAtingido(1L, 1L);

        assertEquals(new BigDecimal("25.00"), resultado.percentualAtingido());
        assertEquals("Viagem", resultado.nome());
        assertFalse(resultado.metaAtingida());
    }

    @Test
    void deveLimitarPercentualEm100QuandoMetaForAtingida() {
        Meta meta = criarMeta("5000.00", "6000.00");

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(metaRepository.findByIdAndUsuarioId(1L, 1L))
                .thenReturn(Optional.of(meta));

        var resultado = progressoMetaService.calcularPercentualAtingido(1L, 1L);

        assertEquals(new BigDecimal("100.00"), resultado.percentualAtingido());
        assertTrue(resultado.metaAtingida());
    }

    @Test
    void deveCalcularTempoEstimadoSemAporteInformado() {
        Meta meta = criarMeta("12000.00", "3000.00");

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(metaRepository.findByIdAndUsuarioId(1L, 1L))
                .thenReturn(Optional.of(meta));

        when(valorMensalMetaService.calcularValorRestante(meta))
                .thenReturn(new BigDecimal("9000.00"));

        when(valorMensalMetaService.calcularValorMensal(meta))
                .thenReturn(new BigDecimal("750.00"));

        var resultado = progressoMetaService.calcularTempoEstimado(
                1L, 1L, null);

        assertEquals(new BigDecimal("9000.00"), resultado.valorRestante());
        assertEquals(new BigDecimal("750.00"), resultado.valorMensalConsiderado());
        assertEquals(12, resultado.mesesEstimados());
    }
  
    @Test
    void deveCalcularTempoEstimadoComAporteInformado() {
        Meta meta = criarMeta("12000.00", "3000.00");

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(metaRepository.findByIdAndUsuarioId(1L, 1L))
                .thenReturn(Optional.of(meta));

        when(valorMensalMetaService.calcularValorRestante(meta))
                .thenReturn(new BigDecimal("9000.00"));

        var resultado = progressoMetaService.calcularTempoEstimado(
                1L, 1L, new BigDecimal("1000.00"));

        assertEquals(new BigDecimal("9000.00"), resultado.valorRestante());
        assertEquals(new BigDecimal("1000.00"), resultado.valorMensalConsiderado());
        assertEquals(9, resultado.mesesEstimados());
    }

    @Test
    void deveLancarExcecaoQuandoMetaNaoExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(metaRepository.findByIdAndUsuarioId(99L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> progressoMetaService.calcularPercentualAtingido(99L, 1L));
    }

}
