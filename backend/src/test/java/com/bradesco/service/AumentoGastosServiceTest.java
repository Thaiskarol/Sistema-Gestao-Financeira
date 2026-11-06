package com.bradesco.service;

import com.bradesco.dto.response.InsightDTO;
import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AumentoGastosServiceTest {
  
    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AumentoGastosService aumentoGastosService;

    private static final LocalDate MES_ATUAL = LocalDate.now();
    private static final LocalDate MES_ANTERIOR = LocalDate.now().minusMonths(1);

    private Transacao transacao(String valor, LocalDate data, TipoTransacao tipo) {
        Transacao t = new Transacao();
        t.setValor(new BigDecimal(valor));
        t.setDataTransacao(data);
        t.setTipo(tipo);
        return t;
    }

    private void transacoesDoUsuario(Transacao... transacoes) {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(transacaoRepository.findByUsuarioId(1L)).thenReturn(List.of(transacoes));
    }

    @Test
    void deveGerarInsightQuandoGastosAumentam50Porcento() {
        transacoesDoUsuario(
                transacao("100.00", MES_ANTERIOR, TipoTransacao.DESPESA),
                transacao("150.00", MES_ATUAL, TipoTransacao.DESPESA));

        Optional<InsightDTO> resultado = aumentoGastosService.analisarAumento(1L);

        assertTrue(resultado.isPresent());
        assertEquals("AUMENTO_GASTOS", resultado.get().tipo());
        assertEquals("Geral", resultado.get().categoria());
        assertEquals(new BigDecimal("50.00"), resultado.get().percentual());
        assertEquals("Seus gastos aumentaram 50.00% em relação ao mês anterior.",
                resultado.get().mensagem());
    }

    @Test
    void deveArredondarPercentualComDuasCasas() {
        transacoesDoUsuario(
                transacao("300.00", MES_ANTERIOR, TipoTransacao.DESPESA),
                transacao("400.00", MES_ATUAL, TipoTransacao.DESPESA));

        Optional<InsightDTO> resultado = aumentoGastosService.analisarAumento(1L);

        assertTrue(resultado.isPresent());
        assertEquals(new BigDecimal("33.33"), resultado.get().percentual());
    }

    @Test
    void naoDeveGerarInsightQuandoMesAnteriorNaoTemDespesas() {
        transacoesDoUsuario(
                transacao("500.00", MES_ATUAL, TipoTransacao.DESPESA));

        assertTrue(aumentoGastosService.analisarAumento(1L).isEmpty());
    }

    @Test
    void naoDeveGerarInsightQuandoAumentoEPequeno() {
        transacoesDoUsuario(
                transacao("100.00", MES_ANTERIOR, TipoTransacao.DESPESA),
                transacao("105.00", MES_ATUAL, TipoTransacao.DESPESA));

        assertTrue(aumentoGastosService.analisarAumento(1L).isEmpty());
    }

    @Test
    void naoDeveGerarInsightQuandoAumentoEExatamenteNoLimiar() {
        transacoesDoUsuario(
                transacao("100.00", MES_ANTERIOR, TipoTransacao.DESPESA),
                transacao("130.00", MES_ATUAL, TipoTransacao.DESPESA));

        assertTrue(aumentoGastosService.analisarAumento(1L).isEmpty());
    }

    @Test
    void naoDeveGerarInsightQuandoGastosDiminuem() {
        transacoesDoUsuario(
                transacao("150.00", MES_ANTERIOR, TipoTransacao.DESPESA),
                transacao("100.00", MES_ATUAL, TipoTransacao.DESPESA));

        assertTrue(aumentoGastosService.analisarAumento(1L).isEmpty());
    }

    @Test
    void deveIgnorarReceitasNoCalculo() {
        transacoesDoUsuario(
                transacao("100.00", MES_ANTERIOR, TipoTransacao.DESPESA),
                transacao("140.00", MES_ATUAL, TipoTransacao.DESPESA),
                transacao("5000.00", MES_ATUAL, TipoTransacao.RECEITA));

        Optional<InsightDTO> resultado = aumentoGastosService.analisarAumento(1L);

        assertTrue(resultado.isPresent());
        assertEquals(new BigDecimal("40.00"), resultado.get().percentual());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> aumentoGastosService.analisarAumento(99L));

        verifyNoInteractions(transacaoRepository);
    }
}
