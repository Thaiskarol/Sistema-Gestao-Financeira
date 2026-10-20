package com.bradesco.service;

import com.bradesco.dto.response.GrupoValorDTO;
import com.bradesco.entity.Categoria;
import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;
import com.bradesco.service.GastosService;
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
public class GastosServiceTest {
  
    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private GastosService gastosService;

    private Transacao despesa(String categoria, String estabelecimento,
                          String valor, LocalDate data) {
        Categoria c = new Categoria();
        c.setNome(categoria);

        Transacao t = new Transacao();
        t.setDescricao("Despesa");
        t.setValor(new BigDecimal(valor));
        t.setDataTransacao(data);
        t.setTipo(TipoTransacao.DESPESA);
        t.setCategoria(c);
        t.setEstabelecimento(estabelecimento);
        return t;
    }

    private void usuarioExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
    }

    private void despesasDoUsuario(Transacao... transacoes) {
        when(transacaoRepository.findByUsuarioIdAndTipo(1L, TipoTransacao.DESPESA))
                .thenReturn(List.of(transacoes));
    }

    private static final LocalDate DATA = LocalDate.of(2026, 10, 15);

    @Test
    void porCategoriaAgrupaESomaDespesasDaMesmaCategoria() {
        usuarioExiste();
        despesasDoUsuario(
                despesa("Alimentação", "Mercado", "30.00", DATA),
                despesa("Alimentação", "Padaria", "20.00", DATA),
                despesa("Transporte", "Uber", "10.00", DATA));

        List<GrupoValorDTO> resultado = gastosService.gastosPorCategoria(1L);

        assertEquals(2, resultado.size());
        assertEquals("Alimentação", resultado.get(0).rotulo());
        assertEquals(new BigDecimal("50.00"), resultado.get(0).valor());
        assertEquals("Transporte", resultado.get(1).rotulo());
        assertEquals(new BigDecimal("10.00"), resultado.get(1).valor());
    }

    @Test
    void porCategoriaNaoMostraCategoriaSemGasto() {
        usuarioExiste();
        despesasDoUsuario(despesa("Alimentação", "Mercado", "30.00", DATA));

        List<GrupoValorDTO> resultado = gastosService.gastosPorCategoria(1L);

        assertEquals(1, resultado.size());
        assertTrue(resultado.stream().noneMatch(g -> g.rotulo().equals("Lazer")));
    }

    @Test
    void porCategoriaRetornaListaVaziaQuandoNaoHaDespesas() {
        usuarioExiste();
        despesasDoUsuario();

        assertTrue(gastosService.gastosPorCategoria(1L).isEmpty());
    }

    @Test
    void porEstabelecimentoIgnoraDespesasSemEstabelecimento() {
        usuarioExiste();
        despesasDoUsuario(
                despesa("Alimentação", "Mercado", "30.00", DATA),
                despesa("Alimentação", "Mercado", "15.00", DATA),
                despesa("Lazer", null, "40.00", DATA),
                despesa("Lazer", "  ", "25.00", DATA));

        List<GrupoValorDTO> resultado = gastosService.gastosPorEstabelecimento(1L);

        assertEquals(1, resultado.size());
        assertEquals("Mercado", resultado.get(0).rotulo());
        assertEquals(new BigDecimal("45.00"), resultado.get(0).valor());
    }

    @Test
    void porPeriodoSomaSomenteODentroDoIntervalo() {
        usuarioExiste();
        despesasDoUsuario(
                despesa("Alimentação", "A", "100.00", LocalDate.of(2026, 9, 30)),  // fora
                despesa("Alimentação", "A", "10.00", LocalDate.of(2026, 10, 1)),   // limite
                despesa("Alimentação", "A", "20.00", LocalDate.of(2026, 10, 15)),
                despesa("Alimentação", "A", "5.00", LocalDate.of(2026, 10, 31)),   // limite
                despesa("Alimentação", "A", "100.00", LocalDate.of(2026, 11, 1))); // fora

        GrupoValorDTO resultado = gastosService.gastosPorPeriodo(
                1L, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31));

        assertEquals("Período", resultado.rotulo());
        assertEquals(new BigDecimal("35.00"), resultado.valor());
    }

    @Test
    void porPeriodoSemGastoRetornaZero() {
        usuarioExiste();
        despesasDoUsuario(despesa("Alimentação", "A", "100.00", LocalDate.of(2026, 9, 1)));

        GrupoValorDTO resultado = gastosService.gastosPorPeriodo(
                1L, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31));

        assertEquals(0, BigDecimal.ZERO.compareTo(resultado.valor()));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> gastosService.gastosPorCategoria(99L));

        verifyNoInteractions(transacaoRepository);
    }
}
