package com.bradesco.service;

import com.bradesco.entity.Renda;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DistribuicaoFinanceiraServiceTest {

    @Mock
    private RendaRepository rendaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private DistribuicaoFinanceiraService service;

    @Test
    void deveCalcularDistribuicaoFinanceira() {
        Renda renda1 = new Renda();
        renda1.setValor(new BigDecimal("3000.00"));

        Renda renda2 = new Renda();
        renda2.setValor(new BigDecimal("2000.00"));

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(rendaRepository.findByUsuarioId(1L))
                .thenReturn(List.of(renda1, renda2));

        var resultado = service.calcularDistribuicao(1L);

        assertEquals(new BigDecimal("5000.00"), resultado.rendaTotal());
        assertEquals(new BigDecimal("2500.00"), resultado.necessidades());
        assertEquals(new BigDecimal("1500.00"), resultado.desejos());
        assertEquals(new BigDecimal("1000.00"), resultado.investimentos());
    }

    @Test
    void deveRetornarZeroQuandoUsuarioNaoPossuiRenda() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(rendaRepository.findByUsuarioId(1L))
                .thenReturn(List.of());

        var resultado = service.calcularDistribuicao(1L);

        assertEquals(new BigDecimal("0.00"), resultado.rendaTotal());
        assertEquals(new BigDecimal("0.00"), resultado.necessidades());
        assertEquals(new BigDecimal("0.00"), resultado.desejos());
        assertEquals(new BigDecimal("0.00"), resultado.investimentos());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.calcularDistribuicao(1L)
        );
    }
}