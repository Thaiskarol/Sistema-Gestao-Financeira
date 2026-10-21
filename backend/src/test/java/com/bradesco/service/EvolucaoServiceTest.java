package com.bradesco.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bradesco.dto.response.ComparativoMensalDTO;
import com.bradesco.dto.response.ValorMensalDTO;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.entity.Transacao;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class EvolucaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private EvolucaoService evolucaoService;

    private Transacao criarTransacao(
            BigDecimal valor,
            LocalDate data,
            TipoTransacao tipo) {

        Transacao transacao = new Transacao();

        transacao.setValor(valor);
        transacao.setDataTransacao(data);
        transacao.setTipo(tipo);

        return transacao;
    }

    @Test
    void deveAgruparDespesasPorMes() {

        Transacao setembro1 = criarTransacao(
                new BigDecimal("100.00"),
                LocalDate.of(2026, 9, 5),
                TipoTransacao.DESPESA);

        Transacao setembro2 = criarTransacao(
                new BigDecimal("200.00"),
                LocalDate.of(2026, 9, 20),
                TipoTransacao.DESPESA);

        Transacao outubro = criarTransacao(
                new BigDecimal("150.00"),
                LocalDate.of(2026, 10, 10),
                TipoTransacao.DESPESA);

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        
        when(transacaoRepository.findByUsuarioId(1L))
                .thenReturn(List.of(setembro1, setembro2, outubro));

        List<ValorMensalDTO> resultado =
                evolucaoService.evolucaoMensalDespesas(1L);

        assertEquals(2, resultado.size());

        assertEquals("2026-09", resultado.get(0).mes());
        assertEquals(new BigDecimal("300.00"), resultado.get(0).valor());

        assertEquals("2026-10", resultado.get(1).mes());
        assertEquals(new BigDecimal("150.00"), resultado.get(1).valor());
    }

    @Test
    void deveAgruparReceitasPorMes() {

        Transacao setembro1 = criarTransacao(
                new BigDecimal("2000.00"),
                LocalDate.of(2026, 9, 5),
                TipoTransacao.RECEITA);

        Transacao setembro2 = criarTransacao(
                new BigDecimal("500.00"),
                LocalDate.of(2026, 9, 20),
                TipoTransacao.RECEITA);

        Transacao outubro = criarTransacao(
                new BigDecimal("2500.00"),
                LocalDate.of(2026, 10, 10),
                TipoTransacao.RECEITA);
        
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        when(transacaoRepository.findByUsuarioId(1L))
                .thenReturn(List.of(setembro1, setembro2, outubro));

        List<ValorMensalDTO> resultado =
                evolucaoService.evolucaoMensalReceitas(1L);

        assertEquals(2, resultado.size());

        assertEquals("2026-09", resultado.get(0).mes());
        assertEquals(new BigDecimal("2500.00"), resultado.get(0).valor());

        assertEquals("2026-10", resultado.get(1).mes());
        assertEquals(new BigDecimal("2500.00"), resultado.get(1).valor());
    }

    @Test
    void deveCalcularComparativoEntreMeses() {

        Transacao setembro = criarTransacao(
                new BigDecimal("1000.00"),
                LocalDate.of(2026, 9, 10),
                TipoTransacao.DESPESA);

        Transacao outubro = criarTransacao(
                new BigDecimal("1300.00"),
                LocalDate.of(2026, 10, 10),
                TipoTransacao.DESPESA);

        when(usuarioRepository.existsById(1L)).thenReturn(true);

        when(transacaoRepository.findByUsuarioId(1L))
                .thenReturn(List.of(setembro, outubro));

        ComparativoMensalDTO resultado =
                evolucaoService.compararMeses(
                        1L,
                        "2026-09",
                        "2026-10");

        assertEquals("2026-09", resultado.mesA());
        assertEquals(new BigDecimal("1000.00"), resultado.valorA());

        assertEquals("2026-10", resultado.mesB());
        assertEquals(new BigDecimal("1300.00"), resultado.valorB());

        assertEquals(new BigDecimal("300.00"), resultado.variacao());
    }

    @Test
    void deveConsiderarZeroQuandoMesNaoPossuiDespesas() {

        Transacao setembro = criarTransacao(
                new BigDecimal("1000.00"),
                LocalDate.of(2026, 9, 10),
                TipoTransacao.DESPESA);

        when(usuarioRepository.existsById(1L)).thenReturn(true);

        when(transacaoRepository.findByUsuarioId(1L))
                .thenReturn(List.of(setembro));

        ComparativoMensalDTO resultado =
                evolucaoService.compararMeses(
                        1L,
                        "2026-09",
                        "2026-10");

        assertEquals(new BigDecimal("1000.00"), resultado.valorA());
        assertEquals(BigDecimal.ZERO, resultado.valorB());
        assertEquals(new BigDecimal("-1000.00"), resultado.variacao());
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoExiste() {

        when(usuarioRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> evolucaoService.evolucaoMensalDespesas(99L));

        assertEquals(
                "Usuário não encontrado.",
                exception.getMessage());
    }
}