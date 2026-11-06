package com.bradesco.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bradesco.dto.response.InsightDTO;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.entity.Transacao;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;

class QuedaRendaServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private QuedaRendaService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        service = new QuedaRendaService(
                transacaoRepository,
                usuarioRepository
        );
    }

    @Test
    void deveGerarInsightQuandoQuedaForMaiorQue15Porcento() {

        Long usuarioId = 1L;

        when(usuarioRepository.existsById(usuarioId))
                .thenReturn(true);

        Transacao receitaAnterior =
                criarTransacao(
                        BigDecimal.valueOf(1000),
                        YearMonth.now().minusMonths(1)
                );

        Transacao receitaAtual =
                criarTransacao(
                        BigDecimal.valueOf(800),
                        YearMonth.now()
                );

        when(transacaoRepository.findByUsuarioId(usuarioId))
                .thenReturn(List.of(receitaAnterior, receitaAtual));

        Optional<InsightDTO> resultado =
                service.analisarQueda(usuarioId);

        assertTrue(resultado.isPresent());
        assertEquals("QUEDA_RENDA", resultado.get().tipo());
        assertEquals(
        0,
        BigDecimal.valueOf(20)
                .compareTo(resultado.get().percentual())
);
    }

    @Test
    void deveRetornarVazioQuandoMesAnteriorNaoPossuirReceita() {

        Long usuarioId = 1L;

        when(usuarioRepository.existsById(usuarioId))
                .thenReturn(true);

        Transacao receitaAtual =
                criarTransacao(
                        BigDecimal.valueOf(800),
                        YearMonth.now()
                );

        when(transacaoRepository.findByUsuarioId(usuarioId))
                .thenReturn(List.of(receitaAtual));

        Optional<InsightDTO> resultado =
                service.analisarQueda(usuarioId);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveRetornarVazioQuandoQuedaForMenorQue15Porcento() {

        Long usuarioId = 1L;

        when(usuarioRepository.existsById(usuarioId))
                .thenReturn(true);

        Transacao receitaAnterior =
                criarTransacao(
                        BigDecimal.valueOf(1000),
                        YearMonth.now().minusMonths(1)
                );

        Transacao receitaAtual =
                criarTransacao(
                        BigDecimal.valueOf(950),
                        YearMonth.now()
                );

        when(transacaoRepository.findByUsuarioId(usuarioId))
                .thenReturn(List.of(receitaAnterior, receitaAtual));

        Optional<InsightDTO> resultado =
                service.analisarQueda(usuarioId);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveRetornarVazioQuandoRendaAumentar() {

        Long usuarioId = 1L;

        when(usuarioRepository.existsById(usuarioId))
                .thenReturn(true);

        Transacao receitaAnterior =
                criarTransacao(
                        BigDecimal.valueOf(1000),
                        YearMonth.now().minusMonths(1)
                );

        Transacao receitaAtual =
                criarTransacao(
                        BigDecimal.valueOf(1200),
                        YearMonth.now()
                );

        when(transacaoRepository.findByUsuarioId(usuarioId))
                .thenReturn(List.of(receitaAnterior, receitaAtual));

        Optional<InsightDTO> resultado =
                service.analisarQueda(usuarioId);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {

        Long usuarioId = 999L;

        when(usuarioRepository.existsById(usuarioId))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.analisarQueda(usuarioId)
        );
    }

    private Transacao criarTransacao(
            BigDecimal valor,
            YearMonth mes) {

        Transacao transacao = new Transacao();

        transacao.setValor(valor);
        transacao.setDataTransacao(
                LocalDate.of(
                        mes.getYear(),
                        mes.getMonth(),
                        10
                )
        );
        transacao.setTipo(TipoTransacao.RECEITA);

        return transacao;
    }
}