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
}
