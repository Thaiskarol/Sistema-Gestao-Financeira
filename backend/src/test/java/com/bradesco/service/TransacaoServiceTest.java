package com.bradesco.service;

import com.bradesco.dto.request.TransacaoRequestDTO;
import com.bradesco.dto.response.TransacaoResponseDTO;
import com.bradesco.entity.Categoria;
import com.bradesco.entity.Transacao;
import com.bradesco.entity.Usuario;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.CategoriaRepository;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private TransacaoService transacaoService;

    private TransacaoRequestDTO novaRequisicao() {
        return new TransacaoRequestDTO(
                "Salário",
                null,
                new BigDecimal("1000.00"),
                LocalDate.now(),
                null,
                TipoTransacao.RECEITA,
                1L,
                1L
        );
    }

    @Test
    void deveCadastrarTransacaoQuandoUsuarioECategoriaExistem() {
        TransacaoRequestDTO dados = novaRequisicao();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(new Categoria()));
        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Transacao resultado = transacaoService.cadastrar(dados);

        assertNotNull(resultado);
        assertEquals("Salário", resultado.getDescricao());
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        TransacaoRequestDTO dados = novaRequisicao();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transacaoService.cadastrar(dados));
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoExiste() {
        TransacaoRequestDTO dados = novaRequisicao();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transacaoService.cadastrar(dados));
    }

    @Test
    void deveListarTransacoesDoUsuario() {
        Categoria categoria = mock(Categoria.class);
        when(categoria.getId()).thenReturn(1L);

        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(1L);

        Transacao transacao = new Transacao();
        transacao.setDescricao("Salário");
        transacao.setValor(new BigDecimal("1000.00"));
        transacao.setDataTransacao(LocalDate.now());
        transacao.setTipo(TipoTransacao.RECEITA);
        transacao.setCategoria(categoria);
        transacao.setUsuario(usuario);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(transacaoRepository.findByUsuarioId(1L)).thenReturn(List.of(transacao));

        List<TransacaoResponseDTO> resultado = transacaoService.listarPorUsuario(1L);

        assertEquals(1, resultado.size());
        assertEquals("Salário", resultado.get(0).descricao());
        verify(transacaoRepository).findByUsuarioId(1L);
    }

    @Test
    void deveLancarExcecaoAoListarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transacaoService.listarPorUsuario(1L));
    }

    @Test
    void deveClassificarTransacaoQuandoTransacaoECategoriaExistem() {
        Categoria categoria = mock(Categoria.class);
        when(categoria.getId()).thenReturn(1L);

        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(1L);

        Transacao transacao = new Transacao();
        transacao.setDescricao("Mercado");
        transacao.setValor(new BigDecimal("200.00"));
        transacao.setDataTransacao(LocalDate.now());
        transacao.setTipo(TipoTransacao.DESPESA);
        transacao.setUsuario(usuario);

        when(transacaoRepository.findById(10L)).thenReturn(Optional.of(transacao));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(transacaoRepository.save(any(Transacao.class)))
            .thenAnswer(invocacao -> invocacao.getArgument(0));

        TransacaoResponseDTO resultado = transacaoService.classificar(10L, 1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.categoriaId());
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    void deveLancarExcecaoAoClassificarQuandoCategoriaNaoExiste() {
        Transacao transacao = new Transacao();
        transacao.setUsuario(new Usuario());
        when(transacaoRepository.findById(10L)).thenReturn(Optional.of(transacao));

        assertThrows(ResourceNotFoundException.class,
                () -> transacaoService.classificar(10L, 1L));
    }

    private Categoria categoriaComId(Long id) {
        Categoria categoria = new Categoria();
        ReflectionTestUtils.setField(categoria, "id", id);
        return categoria;
    }

    private Transacao despesaComCategoria(Categoria categoria) {
        Transacao t = new Transacao();
        t.setDescricao("Mercado");
        t.setValor(new BigDecimal("50.00"));
        t.setDataTransacao(LocalDate.now());
        t.setTipo(TipoTransacao.DESPESA);
        t.setUsuario(new Usuario());
        t.setCategoria(categoria);
        return t;
    }

    @Test
    void deveAlterarCategoriaDaTransacao() {
        Categoria antiga = categoriaComId(1L);
        Categoria nova = categoriaComId(2L);
        Transacao transacao = despesaComCategoria(antiga);

        when(transacaoRepository.findById(10L)).thenReturn(Optional.of(transacao));
        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(nova));
        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        TransacaoResponseDTO resultado = transacaoService.alterarCategoria(10L, 2L);

        assertEquals(2L, resultado.categoriaId());
        assertSame(nova, transacao.getCategoria());   // CA03: só a nova categoria ficou
        verify(transacaoRepository).save(transacao);
    }

    @Test
    void deveLancarExcecaoQuandoNovaCategoriaNaoExiste() {
        Transacao transacao = despesaComCategoria(categoriaComId(1L));

        when(transacaoRepository.findById(10L)).thenReturn(Optional.of(transacao));
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transacaoService.alterarCategoria(10L, 99L));

        verify(transacaoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoTransacaoNaoExisteAoAlterarCategoria() {
        when(transacaoRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transacaoService.alterarCategoria(10L, 2L));

        verifyNoInteractions(categoriaRepository);
    }

    private Transacao despesaCompleta(String descricao, String estabelecimento, String nomeCategoria) {
        Categoria categoria = new Categoria();
        ReflectionTestUtils.setField(categoria, "id", 3L);
        categoria.setNome(nomeCategoria);

        Usuario usuario = new Usuario();
        ReflectionTestUtils.setField(usuario, "id", 1L);

        Transacao transacao = new Transacao();
        transacao.setDescricao(descricao);
        transacao.setEstabelecimento(estabelecimento);
        transacao.setValor(new BigDecimal("50.00"));
        transacao.setDataTransacao(LocalDate.now());
        transacao.setTipo(TipoTransacao.DESPESA);
        transacao.setCategoria(categoria);
        transacao.setUsuario(usuario);
        return transacao;
    }

    @Test
    void deveIncluirEstabelecimentoECategoriaNomeNaListagemCompleta() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(transacaoRepository.findByUsuarioId(1L))
                .thenReturn(List.of(despesaCompleta("Compras", "Mercado Central", "Alimentação")));

        List<TransacaoResponseDTO> resultado = transacaoService.listarPorUsuario(1L);

        assertEquals("Mercado Central", resultado.get(0).estabelecimento());
        assertEquals("Alimentação", resultado.get(0).categoriaNome());
        assertEquals(3L, resultado.get(0).categoriaId());
    }

    @Test
    void deveListarUltimasTransacoesComLimite() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(transacaoRepository.findByUsuarioIdOrderByDataTransacaoDesc(eq(1L), any(Pageable.class)))
                .thenReturn(List.of(
                        despesaCompleta("Mais recente", "Loja A", "Lazer"),
                        despesaCompleta("Anterior", "Loja B", "Lazer")));

        List<TransacaoResponseDTO> resultado = transacaoService.listarUltimas(1L, 5);

        assertEquals(2, resultado.size());
        assertEquals("Mais recente", resultado.get(0).descricao());
        assertEquals("Loja A", resultado.get(0).estabelecimento());
        assertEquals("Lazer", resultado.get(0).categoriaNome());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(transacaoRepository).findByUsuarioIdOrderByDataTransacaoDesc(eq(1L), captor.capture());
        assertEquals(0, captor.getValue().getPageNumber());
        assertEquals(5, captor.getValue().getPageSize());
    }

    @Test
    void deveRetornarListaVaziaQuandoLimiteForMenorQueUm() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));

        assertTrue(transacaoService.listarUltimas(1L, 0).isEmpty());

        verify(transacaoRepository, never()).findByUsuarioIdOrderByDataTransacaoDesc(any(), any());
    }

    @Test
    void deveLancarExcecaoAoListarUltimasQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transacaoService.listarUltimas(99L, 5));

        verify(transacaoRepository, never()).findByUsuarioIdOrderByDataTransacaoDesc(any(), any());
    }
}
