package com.bradesco.service;

import com.bradesco.dto.response.EvolucaoPatrimonialDTO;
import com.bradesco.dto.response.MetaSugeridaDTO;
import com.bradesco.entity.Renda;
import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.RendaRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatrimonioServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private RendaRepository rendaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PatrimonioService patrimonioService;

    private Transacao transacao(String valor, LocalDate data, TipoTransacao tipo) {
        Transacao t = new Transacao();
        t.setValor(new BigDecimal(valor));
        t.setDataTransacao(data);
        t.setTipo(tipo);
        return t;
    }

    private Renda renda(String valor) {
        Renda r = new Renda();
        r.setValor(new BigDecimal(valor));
        return r;
    }

    private void transacoesDoUsuario(Transacao... transacoes) {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(transacaoRepository.findByUsuarioId(1L)).thenReturn(List.of(transacoes));
    }

    private void rendasDoUsuario(Renda... rendas) {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(rendaRepository.findByUsuarioId(1L)).thenReturn(List.of(rendas));
    }

    @Test
    void deveAcumularPatrimonioEntreMeses() {
        // Propositalmente fora de ordem, para testar a ordenação por mês
        transacoesDoUsuario(
                transacao("3000.00", LocalDate.of(2026, 9, 5), TipoTransacao.RECEITA),
                transacao("2500.00", LocalDate.of(2026, 9, 20), TipoTransacao.DESPESA),
                transacao("3000.00", LocalDate.of(2026, 8, 5), TipoTransacao.RECEITA),
                transacao("1000.00", LocalDate.of(2026, 8, 15), TipoTransacao.DESPESA));

        List<EvolucaoPatrimonialDTO> resultado = patrimonioService.evolucaoPatrimonial(1L);

        assertEquals(2, resultado.size());

        assertEquals("2026-08", resultado.get(0).mes());
        assertEquals(new BigDecimal("2000.00"), resultado.get(0).patrimonioAcumulado());

        // Setembro: saldo do mês 500.00 + 2000.00 que já vinham de agosto
        assertEquals("2026-09", resultado.get(1).mes());
        assertEquals(new BigDecimal("2500.00"), resultado.get(1).patrimonioAcumulado());
    }

    @Test
    void devePermitirPatrimonioNegativo() {
        transacoesDoUsuario(
                transacao("500.00", LocalDate.of(2026, 8, 10), TipoTransacao.DESPESA));

        List<EvolucaoPatrimonialDTO> resultado = patrimonioService.evolucaoPatrimonial(1L);

        assertEquals(1, resultado.size());
        assertEquals(new BigDecimal("-500.00"), resultado.get(0).patrimonioAcumulado());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaMovimentacao() {
        transacoesDoUsuario();

        assertTrue(patrimonioService.evolucaoPatrimonial(1L).isEmpty());
    }

    @Test
    void deveLancarExcecaoNaEvolucaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> patrimonioService.evolucaoPatrimonial(99L));

        verifyNoInteractions(transacaoRepository);
    }

    @Test
    void deveGerarMetasAPartirDaRenda() {
        rendasDoUsuario(renda("3000.00"), renda("2000.00"));   // renda = 5000.00

        List<MetaSugeridaDTO> metas = patrimonioService.metasSugeridas(1L);

        assertEquals(2, metas.size());

        assertEquals("Reserva de emergência (6 meses)", metas.get(0).descricao());
        assertEquals(new BigDecimal("30000.00"), metas.get(0).valorObjetivo());

        assertEquals("Investimento anual", metas.get(1).descricao());
        assertEquals(new BigDecimal("12000.00"), metas.get(1).valorObjetivo());
    }

    @Test
    void deveRetornarListaVaziaDeMetasQuandoNaoHaRenda() {
        rendasDoUsuario();

        assertTrue(patrimonioService.metasSugeridas(1L).isEmpty());
    }

    @Test
    void deveLancarExcecaoNasMetasQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> patrimonioService.metasSugeridas(99L));

        verifyNoInteractions(rendaRepository);
    }
}
