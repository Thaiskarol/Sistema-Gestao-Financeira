package com.bradesco.service;

import com.bradesco.dto.response.ResumoFinanceiroDTO;
import com.bradesco.dto.response.TransacaoResponseDTO;
import com.bradesco.entity.Transacao;
import com.bradesco.entity.Usuario;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaFinanceiraServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ConsultaFinanceiraService consultaFinanceiraService;

    private Transacao transacao(TipoTransacao tipo, String valor, LocalDate data) {
        Transacao t = new Transacao();
        t.setDescricao("teste");
        t.setValor(new BigDecimal(valor));
        t.setDataTransacao(data);
        t.setTipo(tipo);
        return t;
    }

    @Test
    void deveCalcularResumoFinanceiro() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(transacaoRepository.findByUsuarioId(1L)).thenReturn(List.of(
            transacao(TipoTransacao.RECEITA, "1000.00", LocalDate.of(2026, 1, 10)),
            transacao(TipoTransacao.DESPESA, "300.00", LocalDate.of(2026, 1, 15))
        ));

        ResumoFinanceiroDTO resumo = consultaFinanceiraService.consultarResumo(1L);

        assertThat(resumo.totalReceitas()).isEqualByComparingTo("1000.00");
        assertThat(resumo.totalDespesas()).isEqualByComparingTo("300.00");
        assertThat(resumo.saldo()).isEqualByComparingTo("700.00");
        assertThat(resumo.economiaAcumulada()).isEqualByComparingTo("700.00");
        // 300 / 1000 * 100 = 30.00
        assertThat(resumo.percentualComprometimentoRenda()).isEqualByComparingTo("30.00");
    }

    @Test
    void deveRetornarComprometimentoZeroQuandoNaoHaReceitas() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(transacaoRepository.findByUsuarioId(1L)).thenReturn(List.of(
            transacao(TipoTransacao.DESPESA, "300.00", LocalDate.of(2026, 1, 15))
        ));

        ResumoFinanceiroDTO resumo = consultaFinanceiraService.consultarResumo(1L);

        assertThat(resumo.percentualComprometimentoRenda()).isEqualByComparingTo("0");
    }

    @Test
    void deveLancarExcecaoAoConsultarResumoDeUsuarioInexistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> consultaFinanceiraService.consultarResumo(99L));
        verify(transacaoRepository, never()).findByUsuarioId(any());
    }

    @Test
    void deveFiltrarApenasDespesasPorTipo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(transacaoRepository.findByUsuarioId(1L)).thenReturn(List.of(
            transacao(TipoTransacao.RECEITA, "1000.00", LocalDate.of(2026, 1, 10)),
            transacao(TipoTransacao.DESPESA, "300.00", LocalDate.of(2026, 1, 15))
        ));

        List<TransacaoResponseDTO> resultado = consultaFinanceiraService.filtrar(
            1L, null, null, null, TipoTransacao.DESPESA, null, null);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).tipo()).isEqualTo(TipoTransacao.DESPESA);
    }

    @Test
    void deveFiltrarTransacoesPorPeriodo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(transacaoRepository.findByUsuarioId(1L)).thenReturn(List.of(
            transacao(TipoTransacao.DESPESA, "50.00", LocalDate.of(2026, 1, 5)),
            transacao(TipoTransacao.DESPESA, "80.00", LocalDate.of(2026, 3, 20))
        ));

        List<TransacaoResponseDTO> resultado = consultaFinanceiraService.filtrar(
            1L, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), null, null, null, null);

        assertThat(resultado).hasSize(1);
    }
}
