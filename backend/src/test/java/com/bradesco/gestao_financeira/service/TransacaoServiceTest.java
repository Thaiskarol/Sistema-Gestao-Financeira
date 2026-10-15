package com.bradesco.gestao_financeira.service;

import com.bradesco.dto.request.TransacaoRequestDTO;
import com.bradesco.entity.Categoria;
import com.bradesco.entity.Transacao;
import com.bradesco.entity.Usuario;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.CategoriaRepository;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;
import com.bradesco.service.TransacaoService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
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
}

