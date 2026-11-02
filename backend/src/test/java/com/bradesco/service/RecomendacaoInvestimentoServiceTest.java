package com.bradesco.service;

import com.bradesco.dto.response.RecomendacaoInvestimentoDTO;
import com.bradesco.dto.response.ReservaEmergenciaDTO;
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
public class RecomendacaoInvestimentoServiceTest {

    @Mock
    private RendaRepository rendaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private RecomendacaoInvestimentoService recomendacaoInvestimentoService;

    private Renda renda(String valor) {
        Renda r = new Renda();
        r.setValor(new BigDecimal(valor));
        return r;
    }

    private Transacao despesa(String valor, LocalDate data) {
        Transacao t = new Transacao();
        t.setValor(new BigDecimal(valor));
        t.setDataTransacao(data);
        t.setTipo(TipoTransacao.DESPESA);
        return t;
    }

    private void rendasDoUsuario(Renda... rendas) {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(rendaRepository.findByUsuarioId(1L)).thenReturn(List.of(rendas));
    }

    private void despesasDoUsuario(Transacao... despesas) {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(transacaoRepository.findByUsuarioIdAndTipo(1L, TipoTransacao.DESPESA))
                .thenReturn(List.of(despesas));
    }

    @Test
    void deveRecomendarVintePorCentoDaRenda() {
        rendasDoUsuario(renda("3000.00"), renda("2000.00"));   // renda = 5000.00

        RecomendacaoInvestimentoDTO resultado =
                recomendacaoInvestimentoService.recomendarPercentualMinimo(1L);

        assertEquals(new BigDecimal("5000.00"), resultado.rendaIdentificada());
        assertEquals(new BigDecimal("20"), resultado.percentualRecomendado());
        assertEquals(new BigDecimal("1000.00"), resultado.valorRecomendado());
    }

    @Test
    void deveRecomendarZeroQuandoNaoHaRenda() {
        rendasDoUsuario();

        RecomendacaoInvestimentoDTO resultado =
                recomendacaoInvestimentoService.recomendarPercentualMinimo(1L);

        assertEquals(new BigDecimal("0.00"), resultado.valorRecomendado());
    }

    @Test
    void deveLancarExcecaoNaRecomendacaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> recomendacaoInvestimentoService.recomendarPercentualMinimo(99L));

        verifyNoInteractions(rendaRepository);
    }

    @Test
    void deveCalcularReservaComSeisMesesDeDespesaMedia() {
        despesasDoUsuario(despesa("1000.00", LocalDate.of(2026, 9, 10)));

        ReservaEmergenciaDTO resultado =
                recomendacaoInvestimentoService.calcularReservaEmergencia(1L);

        assertEquals(new BigDecimal("1000.00"), resultado.despesaMediaMensal());
        assertEquals(6, resultado.mesesCobertura());
        assertEquals(new BigDecimal("6000.00"), resultado.valorIdealReserva());
    }

    @Test
    void deveCalcularMediaEntreOsMesesComDespesa() {
        despesasDoUsuario(
                despesa("600.00", LocalDate.of(2026, 8, 5)),
                despesa("400.00", LocalDate.of(2026, 8, 20)),    // agosto = 1000.00
                despesa("2000.00", LocalDate.of(2026, 9, 3)));   // setembro = 2000.00

        ReservaEmergenciaDTO resultado =
                recomendacaoInvestimentoService.calcularReservaEmergencia(1L);

        assertEquals(new BigDecimal("1500.00"), resultado.despesaMediaMensal());
        assertEquals(new BigDecimal("9000.00"), resultado.valorIdealReserva());
    }

    @Test
    void deveRetornarReservaZeradaQuandoNaoHaDespesas() {
        despesasDoUsuario();

        ReservaEmergenciaDTO resultado =
                recomendacaoInvestimentoService.calcularReservaEmergencia(1L);

        assertEquals(new BigDecimal("0.00"), resultado.despesaMediaMensal());
        assertEquals(6, resultado.mesesCobertura());
        assertEquals(new BigDecimal("0.00"), resultado.valorIdealReserva());
    }

    @Test
    void deveLancarExcecaoNaReservaQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> recomendacaoInvestimentoService.calcularReservaEmergencia(99L));

        verifyNoInteractions(transacaoRepository);
    }
}
