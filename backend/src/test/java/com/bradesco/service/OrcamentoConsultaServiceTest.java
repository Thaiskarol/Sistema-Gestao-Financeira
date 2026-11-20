package com.bradesco.service;

import com.bradesco.dto.request.OrcamentoAtualizacaoDTO;
import com.bradesco.dto.response.ConsumoOrcamentoDTO;
import com.bradesco.dto.response.OrcamentoDetalheDTO;
import com.bradesco.dto.response.OrcamentoResponseDTO;
import com.bradesco.dto.response.StatusOrcamentoDTO;
import com.bradesco.entity.Categoria;
import com.bradesco.entity.Orcamento;
import com.bradesco.entity.Usuario;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.OrcamentoRepository;
import com.bradesco.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoConsultaServiceTest {

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ConsumoOrcamentoService consumoOrcamentoService;

    @Mock
    private StatusOrcamentoService statusOrcamentoService;

    @InjectMocks
    private OrcamentoConsultaService orcamentoConsultaService;

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

    private Orcamento orcamento(Long id, Usuario dono, Categoria categoria, String limite) {
        Orcamento o = new Orcamento();
        ReflectionTestUtils.setField(o, "id", id);
        o.setUsuario(dono);
        o.setCategoria(categoria);
        o.setLimite(new BigDecimal(limite));
        return o;
    }

    @Test
    void deveListarOrcamentosComConsumoEStatusEmOrdemDeId() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        // Propositalmente fora de ordem
        when(orcamentoRepository.findByUsuarioId(1L)).thenReturn(List.of(
                orcamento(2L, usuario, lazer, "2000.00"),
                orcamento(1L, usuario, alimentacao, "2000.00")));

        when(consumoOrcamentoService.calcularConsumo(1L)).thenReturn(new ConsumoOrcamentoDTO(
                1L, 10L, "Alimentação", new BigDecimal("2000.00"), new BigDecimal("500.00"),
                new BigDecimal("25.00"), new BigDecimal("1500.00")));
        when(statusOrcamentoService.obterStatus(1L)).thenReturn(new StatusOrcamentoDTO(
                1L, 10L, "Alimentação", new BigDecimal("25.00"), "NORMAL"));

        when(consumoOrcamentoService.calcularConsumo(2L)).thenReturn(new ConsumoOrcamentoDTO(
                2L, 20L, "Lazer", new BigDecimal("2000.00"), new BigDecimal("1900.00"),
                new BigDecimal("95.00"), new BigDecimal("100.00")));
        when(statusOrcamentoService.obterStatus(2L)).thenReturn(new StatusOrcamentoDTO(
                2L, 20L, "Lazer", new BigDecimal("95.00"), "RISCO"));

        List<OrcamentoDetalheDTO> resultado = orcamentoConsultaService.listar(1L);

        assertEquals(2, resultado.size());

        OrcamentoDetalheDTO primeiro = resultado.get(0);
        assertEquals(1L, primeiro.id());
        assertEquals(10L, primeiro.categoriaId());
        assertEquals("Alimentação", primeiro.categoriaNome());
        assertEquals(new BigDecimal("2000.00"), primeiro.limite());
        assertEquals(new BigDecimal("500.00"), primeiro.valorConsumido());
        assertEquals(new BigDecimal("25.00"), primeiro.percentualUtilizado());
        assertEquals("NORMAL", primeiro.status());

        OrcamentoDetalheDTO segundo = resultado.get(1);
        assertEquals(2L, segundo.id());
        assertEquals("RISCO", segundo.status());
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioNaoTemOrcamentos() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(orcamentoRepository.findByUsuarioId(1L)).thenReturn(List.of());

        assertTrue(orcamentoConsultaService.listar(1L).isEmpty());

        verifyNoInteractions(consumoOrcamentoService, statusOrcamentoService);
    }

    @Test
    void deveLancarExcecaoAoListarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> orcamentoConsultaService.listar(99L));

        verifyNoInteractions(orcamentoRepository);
    }

    @Test
    void deveAtualizarSomenteOLimiteDoOrcamento() {
        Orcamento orcamento = orcamento(1L, usuario, alimentacao, "2000.00");
        when(orcamentoRepository.findById(1L)).thenReturn(Optional.of(orcamento));
        when(orcamentoRepository.save(any(Orcamento.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        OrcamentoResponseDTO resultado = orcamentoConsultaService.atualizar(
                1L, new OrcamentoAtualizacaoDTO(new BigDecimal("3000.00"), 1L));

        assertEquals(new BigDecimal("3000.00"), resultado.limite());
        assertEquals(10L, resultado.categoriaId());
        assertEquals("Alimentação", resultado.categoriaNome());
        assertEquals(1L, resultado.usuarioId());
        assertSame(alimentacao, orcamento.getCategoria());
        verify(orcamentoRepository).save(orcamento);
    }

    @Test
    void deveLancarExcecaoAoAtualizarOrcamentoDeOutroUsuario() {
        Orcamento orcamento = orcamento(1L, usuario, alimentacao, "2000.00");
        when(orcamentoRepository.findById(1L)).thenReturn(Optional.of(orcamento));

        assertThrows(ResourceNotFoundException.class,
                () -> orcamentoConsultaService.atualizar(
                        1L, new OrcamentoAtualizacaoDTO(new BigDecimal("3000.00"), 99L)));

        verify(orcamentoRepository, never()).save(any());
        assertEquals(new BigDecimal("2000.00"), orcamento.getLimite());
    }

    @Test
    void deveLancarExcecaoAoAtualizarOrcamentoInexistente() {
        when(orcamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orcamentoConsultaService.atualizar(
                        99L, new OrcamentoAtualizacaoDTO(new BigDecimal("3000.00"), 1L)));

        verify(orcamentoRepository, never()).save(any());
    }

    @Test
    void deveExcluirOrcamentoDoUsuario() {
        Orcamento orcamento = orcamento(1L, usuario, alimentacao, "2000.00");
        when(orcamentoRepository.findById(1L)).thenReturn(Optional.of(orcamento));

        orcamentoConsultaService.excluir(1L, 1L);

        verify(orcamentoRepository).delete(orcamento);
    }

    @Test
    void deveLancarExcecaoAoExcluirOrcamentoDeOutroUsuario() {
        Orcamento orcamento = orcamento(1L, usuario, alimentacao, "2000.00");
        when(orcamentoRepository.findById(1L)).thenReturn(Optional.of(orcamento));

        assertThrows(ResourceNotFoundException.class,
                () -> orcamentoConsultaService.excluir(1L, 99L));

        verify(orcamentoRepository, never()).delete(any());
    }

    @Test
    void deveLancarExcecaoAoExcluirOrcamentoInexistente() {
        when(orcamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orcamentoConsultaService.excluir(99L, 1L));

        verify(orcamentoRepository, never()).delete(any());
    }

    @Test
    void deveListarOrcamentosDoMesInformadoUsandoConsumoDaqueleMes() {
        YearMonth mes = YearMonth.of(2026, 10);
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(orcamentoRepository.findByUsuarioId(1L)).thenReturn(List.of(
                orcamento(1L, usuario, alimentacao, "2000.00")));
        when(consumoOrcamentoService.calcularConsumo(1L, mes)).thenReturn(new ConsumoOrcamentoDTO(
                1L, 10L, "Alimentação", new BigDecimal("2000.00"), new BigDecimal("1900.00"),
                new BigDecimal("95.00"), new BigDecimal("100.00")));
        when(statusOrcamentoService.obterStatus(1L, mes)).thenReturn(new StatusOrcamentoDTO(
                1L, 10L, "Alimentação", new BigDecimal("95.00"), "RISCO"));

        List<OrcamentoDetalheDTO> resultado = orcamentoConsultaService.listar(1L, mes);

        assertEquals(1, resultado.size());
        assertEquals(new BigDecimal("1900.00"), resultado.get(0).valorConsumido());
        assertEquals(new BigDecimal("95.00"), resultado.get(0).percentualUtilizado());
        assertEquals("RISCO", resultado.get(0).status());

        verify(consumoOrcamentoService).calcularConsumo(1L, mes);
        verify(consumoOrcamentoService, never()).calcularConsumo(1L);
    }

    @Test
    void deveLancarExcecaoAoListarPorMesQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> orcamentoConsultaService.listar(99L, YearMonth.of(2026, 10)));

        verifyNoInteractions(orcamentoRepository, consumoOrcamentoService, statusOrcamentoService);
    }
}
