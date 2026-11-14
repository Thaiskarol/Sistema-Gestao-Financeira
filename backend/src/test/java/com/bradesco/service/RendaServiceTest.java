package com.bradesco.service;

import com.bradesco.dto.request.RendaRequestDTO;
import com.bradesco.dto.response.RendaResponseDTO;
import com.bradesco.entity.Renda;
import com.bradesco.entity.Usuario;
import com.bradesco.enums.TipoRenda;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.RendaRepository;
import com.bradesco.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RendaServiceTest {

    @Mock
    private RendaRepository rendaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private RendaService rendaService;

    private RendaRequestDTO novaRequisicao() {
        return new RendaRequestDTO(
                "Salário mensal",
                new BigDecimal("3000.00"),
                TipoRenda.SALARIO_LIQUIDO,
                1L
        );
    }
    
    @Test
    void deveCadastrarRendaQuandoUsuarioExiste() {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rendaRepository.save(any(Renda.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        RendaResponseDTO resultado = rendaService.cadastrar(novaRequisicao());

        assertEquals(new BigDecimal("3000.00"), resultado.valor());
        assertEquals(TipoRenda.SALARIO_LIQUIDO, resultado.tipo());
        assertEquals(1L, resultado.usuarioId());
        verify(rendaRepository).save(any(Renda.class));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> rendaService.cadastrar(novaRequisicao()));

        verify(rendaRepository, never()).save(any());
    }

    private void prepararCadastroComSucesso() {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rendaRepository.save(any(Renda.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    @Test
    void deveCadastrarReceitaRecorrenteComTipoRecorrente() {
        prepararCadastroComSucesso();

        RendaRequestDTO dados = new RendaRequestDTO(
                "Aluguel recebido", new BigDecimal("1200.00"), TipoRenda.SALARIO_LIQUIDO, 1L);

        RendaResponseDTO resultado = rendaService.cadastrarReceitaRecorrente(dados);

        assertEquals(TipoRenda.RECORRENTE, resultado.tipo());
        assertEquals(new BigDecimal("1200.00"), resultado.valor());

        ArgumentCaptor<Renda> captor = ArgumentCaptor.forClass(Renda.class);
        verify(rendaRepository).save(captor.capture());
        assertEquals(TipoRenda.RECORRENTE, captor.getValue().getTipo());
    }

    @Test
    void deveCadastrarReceitaVariavelComTipoVariavel() {
        prepararCadastroComSucesso();

        RendaRequestDTO dados = new RendaRequestDTO(
                "Freelance", new BigDecimal("800.00"), TipoRenda.SALARIO_LIQUIDO, 1L);

        RendaResponseDTO resultado = rendaService.cadastrarReceitaVariavel(dados);

        assertEquals(TipoRenda.VARIAVEL, resultado.tipo());
        assertEquals(new BigDecimal("800.00"), resultado.valor());

        ArgumentCaptor<Renda> captor = ArgumentCaptor.forClass(Renda.class);
        verify(rendaRepository).save(captor.capture());
        assertEquals(TipoRenda.VARIAVEL, captor.getValue().getTipo());
    }

    private Renda rendaDe(Long id, String descricao, String valor, TipoRenda tipo) {
        Usuario usuario = new Usuario();
        ReflectionTestUtils.setField(usuario, "id", 1L);

        Renda renda = new Renda();
        ReflectionTestUtils.setField(renda, "id", id);
        renda.setDescricao(descricao);
        renda.setValor(new BigDecimal(valor));
        renda.setTipo(tipo);
        renda.setUsuario(usuario);
        return renda;
    }

    @Test
    void deveListarRendasDoUsuarioEmOrdemDeId() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        // Propositalmente fora de ordem
        when(rendaRepository.findByUsuarioId(1L)).thenReturn(List.of(
                rendaDe(2L, "Freelance", "800.00", TipoRenda.VARIAVEL),
                rendaDe(1L, "Salário", "3000.00", TipoRenda.SALARIO_LIQUIDO)));

        List<RendaResponseDTO> resultado = rendaService.listar(1L);

        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).id());
        assertEquals("Salário", resultado.get(0).descricao());
        assertEquals(new BigDecimal("3000.00"), resultado.get(0).valor());
        assertEquals(TipoRenda.SALARIO_LIQUIDO, resultado.get(0).tipo());
        assertEquals(2L, resultado.get(1).id());
        assertEquals(1L, resultado.get(1).usuarioId());
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioNaoTemRendas() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(rendaRepository.findByUsuarioId(1L)).thenReturn(List.of());

        assertTrue(rendaService.listar(1L).isEmpty());
    }

    @Test
    void deveLancarExcecaoAoListarRendasQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> rendaService.listar(99L));

        verify(rendaRepository, never()).findByUsuarioId(any());
    }
}
