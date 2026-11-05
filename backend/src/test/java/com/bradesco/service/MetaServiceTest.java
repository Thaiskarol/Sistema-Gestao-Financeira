package com.bradesco.service;

import com.bradesco.dto.request.MetaRequestDTO;
import com.bradesco.dto.request.MetaValorAcumuladoDTO;
import com.bradesco.dto.response.MetaResponseDTO;
import com.bradesco.entity.Meta;
import com.bradesco.entity.Usuario;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.MetaRepository;
import com.bradesco.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetaServiceTest {

    @Mock
    private MetaRepository metaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private MetaService metaService;

    private MetaRequestDTO novaRequisicao() {
        return new MetaRequestDTO(
                "Viagem",
                new BigDecimal("12000.00"),
                LocalDate.now().plusMonths(12),
                new BigDecimal("3000.00"),
                1L
        );
    }

    private Usuario usuarioComId(Long id) {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(id);
        return usuario;
    }

    private Meta metaExistente(Usuario usuario) {
        Meta meta = new Meta();
        ReflectionTestUtils.setField(meta, "id", 1L);
        meta.setNome("Viagem");
        meta.setValorObjetivo(new BigDecimal("12000.00"));
        meta.setValorAcumulado(new BigDecimal("3000.00"));
        meta.setDataDesejada(LocalDate.now().plusMonths(12));
        meta.setDataCriacao(LocalDate.now());
        meta.setUsuario(usuario);
        return meta;
    }

    @Test
    void deveCriarMetaQuandoUsuarioExiste() {
        Usuario usuario = usuarioComId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(metaRepository.save(any(Meta.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        MetaResponseDTO resultado = metaService.criar(novaRequisicao());

        assertEquals("Viagem", resultado.nome());
        assertEquals(new BigDecimal("12000.00"), resultado.valorObjetivo());
        assertEquals(new BigDecimal("3000.00"), resultado.valorAcumulado());
        assertEquals(LocalDate.now(), resultado.dataCriacao());
        assertEquals(1L, resultado.usuarioId());

        ArgumentCaptor<Meta> captor = ArgumentCaptor.forClass(Meta.class);
        verify(metaRepository).save(captor.capture());
        assertEquals(LocalDate.now(), captor.getValue().getDataCriacao());
    }

    @Test
    void deveLancarExcecaoAoCriarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> metaService.criar(novaRequisicao()));

        verify(metaRepository, never()).save(any());
    }

    @Test
    void deveListarMetasDoUsuario() {
        Usuario usuario = usuarioComId(1L);
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(metaRepository.findByUsuarioId(1L)).thenReturn(List.of(metaExistente(usuario)));

        List<MetaResponseDTO> resultado = metaService.listarPorUsuario(1L);

        assertEquals(1, resultado.size());
        assertEquals("Viagem", resultado.get(0).nome());
    }

    @Test
    void deveLancarExcecaoAoListarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> metaService.listarPorUsuario(99L));

        verifyNoInteractions(metaRepository);
    }

    @Test
    void deveBuscarMetaDoUsuario() {
        Usuario usuario = usuarioComId(1L);
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(metaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(metaExistente(usuario)));

        MetaResponseDTO resultado = metaService.buscar(1L, 1L);

        assertEquals(1L, resultado.id());
        assertEquals(new BigDecimal("12000.00"), resultado.valorObjetivo());
    }

    @Test
    void deveLancarExcecaoQuandoMetaPertenceAOutroUsuario() {
        when(usuarioRepository.existsById(2L)).thenReturn(true);
        when(metaRepository.findByIdAndUsuarioId(1L, 2L)).thenReturn(Optional.empty());

        ResourceNotFoundException erro = assertThrows(ResourceNotFoundException.class,
                () -> metaService.buscar(1L, 2L));

        assertEquals("Meta não encontrada.", erro.getMessage());
    }

    @Test
    void deveAtualizarValorAcumulado() {
        Usuario usuario = usuarioComId(1L);
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(metaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(metaExistente(usuario)));
        when(metaRepository.save(any(Meta.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        MetaResponseDTO resultado = metaService.atualizarValorAcumulado(
                1L, new MetaValorAcumuladoDTO(1L, new BigDecimal("6000.00")));

        assertEquals(new BigDecimal("6000.00"), resultado.valorAcumulado());
        assertEquals(new BigDecimal("12000.00"), resultado.valorObjetivo());
        verify(metaRepository).save(any(Meta.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarMetaDeOutroUsuario() {
        when(usuarioRepository.existsById(2L)).thenReturn(true);
        when(metaRepository.findByIdAndUsuarioId(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> metaService.atualizarValorAcumulado(
                        1L, new MetaValorAcumuladoDTO(2L, new BigDecimal("500.00"))));

        verify(metaRepository, never()).save(any());
    }
}
