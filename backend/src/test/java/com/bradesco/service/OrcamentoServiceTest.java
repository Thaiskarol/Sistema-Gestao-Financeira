package com.bradesco.service;

import com.bradesco.dto.request.OrcamentoRequestDTO;
import com.bradesco.dto.response.OrcamentoResponseDTO;
import com.bradesco.entity.Categoria;
import com.bradesco.entity.Orcamento;
import com.bradesco.entity.Usuario;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.CategoriaRepository;
import com.bradesco.repository.OrcamentoRepository;
import com.bradesco.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrcamentoServiceTest {

    @Mock
    private OrcamentoRepository orcamentoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private OrcamentoService orcamentoService;

    private OrcamentoRequestDTO novaRequisicao() {
        return new OrcamentoRequestDTO(
                new BigDecimal("500.00"),
                1L,
                1L
        );
    }

    @Test
    void deveCriarOrcamentoQuandoUsuarioECategoriaExistem() {
        OrcamentoRequestDTO dados = novaRequisicao();

        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(1L);

        Categoria categoria = mock(Categoria.class);
        when(categoria.getId()).thenReturn(1L);
        when(categoria.getNome()).thenReturn("Alimentação");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(orcamentoRepository.save(any(Orcamento.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        OrcamentoResponseDTO resultado = orcamentoService.criar(dados);

        assertNotNull(resultado);
        assertEquals(new BigDecimal("500.00"), resultado.limite());
        assertEquals(1L, resultado.categoriaId());
        assertEquals("Alimentação", resultado.categoriaNome());
        assertEquals(1L, resultado.usuarioId());
        verify(orcamentoRepository).save(any(Orcamento.class));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        OrcamentoRequestDTO dados = novaRequisicao();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orcamentoService.criar(dados));
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoExiste() {
        OrcamentoRequestDTO dados = novaRequisicao();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orcamentoService.criar(dados));
    }
}
