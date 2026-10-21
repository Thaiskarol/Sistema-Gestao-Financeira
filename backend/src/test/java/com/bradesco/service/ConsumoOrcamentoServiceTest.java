package com.bradesco.service;

import com.bradesco.dto.response.ConsumoOrcamentoDTO;
import com.bradesco.entity.Categoria;
import com.bradesco.entity.Orcamento;
import com.bradesco.entity.Transacao;
import com.bradesco.entity.Usuario;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.OrcamentoRepository;
import com.bradesco.repository.TransacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsumoOrcamentoServiceTest {
  @Mock
  private OrcamentoRepository orcamentoRepository;

  @Mock
  private TransacaoRepository transacaoRepository;

  @InjectMocks
  private ConsumoOrcamentoService consumoOrcamentoService;

  private final Usuario usuario = usuarioComId(1L);
  private final Categoria alimentacao = categoria(10L, "Alimentação");
  private final Categoria lazer = categoria(20L, "Lazer");

  private static Usuario usuarioComId(Long id) {
      Usuario u = new Usuario();
      ReflectionTestUtils.setField(u, "id", id);
      return u;
  }

  private static Categoria categoria(Long id, String nome) {
      Categoria c = new Categoria();
      ReflectionTestUtils.setField(c, "id", id);
      c.setNome(nome);
      return c;
  }

  private Orcamento orcamentoDeAlimentacao(String limite) {
      Orcamento o = mock(Orcamento.class);
      when(o.getId()).thenReturn(5L);
      when(o.getLimite()).thenReturn(new BigDecimal(limite));
      when(o.getUsuario()).thenReturn(usuario);
      when(o.getCategoria()).thenReturn(alimentacao);
      when(orcamentoRepository.findById(5L)).thenReturn(Optional.of(o));
      return o;
    }

    private Transacao despesa(Categoria categoria, String valor, LocalDate data) {
      Transacao t = new Transacao();
      t.setValor(new BigDecimal(valor));
      t.setDataTransacao(data);
      t.setTipo(TipoTransacao.DESPESA);
      t.setCategoria(categoria);
      return t;
    }

    private void despesasDoUsuario(Transacao... transacoes) {
      when(transacaoRepository.findByUsuarioIdAndTipo(1L, TipoTransacao.DESPESA))
              .thenReturn(List.of(transacoes));
    }

  @Test
  void deveSomarDuasDespesasDaCategoriaNoMes() {
    orcamentoDeAlimentacao("1000.00");
    despesasDoUsuario(
            despesa(alimentacao, "100.00", LocalDate.now()),
            despesa(alimentacao, "200.00", LocalDate.now()));

    ConsumoOrcamentoDTO resultado = consumoOrcamentoService.calcularConsumo(5L);

    assertEquals(new BigDecimal("300.00"), resultado.valorConsumido());
    assertEquals(5L, resultado.orcamentoId());
    assertEquals(10L, resultado.categoriaId());
    assertEquals("Alimentação", resultado.categoriaNome());
  }

  @Test
  void deveCalcularPercentualESaldo() {
      orcamentoDeAlimentacao("1000.00");
      despesasDoUsuario(despesa(alimentacao, "300.00", LocalDate.now()));

      ConsumoOrcamentoDTO resultado = consumoOrcamentoService.calcularConsumo(5L);

      assertEquals(new BigDecimal("30.00"), resultado.percentualUtilizado());
      assertEquals(new BigDecimal("700.00"), resultado.saldoRestante());
  }

  @Test
  void deveArredondarPercentualComDuasCasas() {
      orcamentoDeAlimentacao("900.00");
      despesasDoUsuario(despesa(alimentacao, "300.00", LocalDate.now()));

      ConsumoOrcamentoDTO resultado = consumoOrcamentoService.calcularConsumo(5L);

      assertEquals(new BigDecimal("33.33"), resultado.percentualUtilizado());
  }

  @Test
  void deveRetornarPercentualZeroQuandoLimiteForZero() {
      orcamentoDeAlimentacao("0.00");
      despesasDoUsuario(despesa(alimentacao, "50.00", LocalDate.now()));

      ConsumoOrcamentoDTO resultado = consumoOrcamentoService.calcularConsumo(5L);

      assertEquals(0, BigDecimal.ZERO.compareTo(resultado.percentualUtilizado()));
      assertEquals(new BigDecimal("-50.00"), resultado.saldoRestante());
  }

  @Test
  void naoDeveContarDespesaDeOutroMes() {
      orcamentoDeAlimentacao("1000.00");
      despesasDoUsuario(
              despesa(alimentacao, "500.00", LocalDate.now().minusMonths(1)),
              despesa(alimentacao, "50.00", LocalDate.now()));

      ConsumoOrcamentoDTO resultado = consumoOrcamentoService.calcularConsumo(5L);

      assertEquals(new BigDecimal("50.00"), resultado.valorConsumido());
  }

  @Test
  void naoDeveContarDespesaDeOutraCategoria() {
      orcamentoDeAlimentacao("1000.00");
      despesasDoUsuario(
              despesa(lazer, "400.00", LocalDate.now()),
              despesa(alimentacao, "60.00", LocalDate.now()));

      ConsumoOrcamentoDTO resultado = consumoOrcamentoService.calcularConsumo(5L);

      assertEquals(new BigDecimal("60.00"), resultado.valorConsumido());
  }

  @Test
  void deveLancarExcecaoQuandoOrcamentoNaoExiste() {
      when(orcamentoRepository.findById(99L)).thenReturn(Optional.empty());

      assertThrows(ResourceNotFoundException.class,
              () -> consumoOrcamentoService.calcularConsumo(99L));

      verifyNoInteractions(transacaoRepository);
  }
}
