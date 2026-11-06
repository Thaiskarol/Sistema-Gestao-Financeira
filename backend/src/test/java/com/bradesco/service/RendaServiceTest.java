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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

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
}
