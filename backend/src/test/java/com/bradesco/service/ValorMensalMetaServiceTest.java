package com.bradesco.service;

import com.bradesco.dto.response.ValorMensalMetaDTO;
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
class ValorMensalMetaServiceTest {

    @Mock
    private MetaRepository metaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ValorMensalMetaService valorMensalMetaService;

    private Meta meta(String objetivo, String acumulado, LocalDate dataDesejada) {
        Meta m = new Meta();
        m.setNome("Viagem");
        m.setValorObjetivo(new BigDecimal(objetivo));
        m.setValorAcumulado(new BigDecimal(acumulado));
        m.setDataDesejada(dataDesejada);
        return m;
    }

    @Test
    void deveCalcularExemploDeReferencia() {
        Meta meta = meta("10000.00", "1000.00", LocalDate.now().plusMonths(12));

        assertEquals(new BigDecimal("9000.00"), valorMensalMetaService.calcularValorRestante(meta));
        assertEquals(12, valorMensalMetaService.calcularMesesRestantes(meta));
        assertEquals(new BigDecimal("750.00"), valorMensalMetaService.calcularValorMensal(meta));
    }

    @Test
    void deveRetornarZeroQuandoAcumuladoJaAtingiuObjetivo() {
        Meta meta = meta("5000.00", "6000.00", LocalDate.now().plusMonths(6));

        assertEquals(0, BigDecimal.ZERO.compareTo(valorMensalMetaService.calcularValorRestante(meta)));
        assertEquals(0, BigDecimal.ZERO.compareTo(valorMensalMetaService.calcularValorMensal(meta)));
    }

    @Test
    void deveUsarUmMesQuandoDataDesejadaEhNoMesAtual() {
        Meta meta = meta("1000.00", "400.00", LocalDate.now());

        assertEquals(1, valorMensalMetaService.calcularMesesRestantes(meta));
        assertEquals(new BigDecimal("600.00"), valorMensalMetaService.calcularValorMensal(meta));
    }

    @Test
    void deveUsarUmMesQuandoDataDesejadaJaPassou() {
        Meta meta = meta("1000.00", "400.00", LocalDate.now().minusMonths(2));

        assertEquals(1, valorMensalMetaService.calcularMesesRestantes(meta));
        assertEquals(new BigDecimal("600.00"), valorMensalMetaService.calcularValorMensal(meta));
    }

    @Test
    void deveArredondarParaCimaParaNaoFaltarCentavos() {
        Meta meta = meta("1000.00", "0.00", LocalDate.now().plusMonths(3));

        BigDecimal mensal = valorMensalMetaService.calcularValorMensal(meta);

        assertEquals(new BigDecimal("333.34"), mensal);
        assertTrue(mensal.multiply(BigDecimal.valueOf(3)).compareTo(new BigDecimal("1000.00")) >= 0);
    }

    @Test
    void deveMontarDtoCompletoNaConsulta() {
        LocalDate dataDesejada = LocalDate.now().plusMonths(12);
        Meta meta = meta("10000.00", "1000.00", dataDesejada);
        ReflectionTestUtils.setField(meta, "id", 7L);

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(metaRepository.findByIdAndUsuarioId(7L, 1L)).thenReturn(Optional.of(meta));

        ValorMensalMetaDTO resultado = valorMensalMetaService.consultarValorMensal(7L, 1L);

        assertEquals(7L, resultado.metaId());
        assertEquals("Viagem", resultado.nome());
        assertEquals(new BigDecimal("10000.00"), resultado.valorObjetivo());
        assertEquals(new BigDecimal("1000.00"), resultado.valorAcumulado());
        assertEquals(new BigDecimal("9000.00"), resultado.valorRestante());
        assertEquals(dataDesejada, resultado.dataDesejada());
        assertEquals(12, resultado.mesesRestantes());
        assertEquals(new BigDecimal("750.00"), resultado.valorMensalNecessario());
    }

    @Test
    void deveLancarExcecaoQuandoMetaNaoExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(metaRepository.findByIdAndUsuarioId(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> valorMensalMetaService.consultarValorMensal(99L, 1L));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> valorMensalMetaService.consultarValorMensal(7L, 99L));

        verifyNoInteractions(metaRepository);
    }
}
