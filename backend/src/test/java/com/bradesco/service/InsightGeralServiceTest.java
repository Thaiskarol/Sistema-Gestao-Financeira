package com.bradesco.service;

import com.bradesco.dto.response.InsightDTO;
import com.bradesco.entity.Orcamento;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InsightGeralServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private AumentoGastosService aumentoGastosService;

    @Mock
    private QuedaRendaService quedaRendaService;

    @Mock
    private InsightOrcamentoService insightOrcamentoService;

    @InjectMocks
    private InsightGeralService insightGeralService;

    private InsightDTO insight(String tipo, String categoria) {
        return new InsightDTO(tipo, categoria, new BigDecimal("50.00"), "Mensagem de " + tipo);
    }

    private Orcamento orcamentoComId(Long id) {
        Orcamento o = new Orcamento();
        ReflectionTestUtils.setField(o, "id", id);
        return o;
    }

    @Test
    void deveJuntarTodosOsInsightsPreenchidos() {
        InsightDTO aumento = insight("AUMENTO_GASTOS", "Geral");
        InsightDTO queda = insight("QUEDA_RENDA", null);
        InsightDTO proximo = insight("ORCAMENTO_PROXIMO", "Alimentação");
        InsightDTO excedido = insight("ORCAMENTO_EXCEDIDO", "Lazer");

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(aumentoGastosService.analisarAumento(1L)).thenReturn(Optional.of(aumento));
        when(quedaRendaService.analisarQueda(1L)).thenReturn(Optional.of(queda));
        // Propositalmente fora de ordem
        when(orcamentoRepository.findByUsuarioId(1L))
                .thenReturn(List.of(orcamentoComId(2L), orcamentoComId(1L)));

        when(insightOrcamentoService.orcamentoProximoDoLimite(1L)).thenReturn(Optional.of(proximo));
        when(insightOrcamentoService.orcamentoExcedido(1L)).thenReturn(Optional.empty());
        when(insightOrcamentoService.orcamentoProximoDoLimite(2L)).thenReturn(Optional.empty());
        when(insightOrcamentoService.orcamentoExcedido(2L)).thenReturn(Optional.of(excedido));

        List<InsightDTO> resultado = insightGeralService.listarInsights(1L);

        assertEquals(List.of(aumento, queda, proximo, excedido), resultado);
    }

    @Test
    void deveIncluirSomenteOsInsightsQueVieremPreenchidos() {
        InsightDTO queda = insight("QUEDA_RENDA", null);

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(aumentoGastosService.analisarAumento(1L)).thenReturn(Optional.empty());
        when(quedaRendaService.analisarQueda(1L)).thenReturn(Optional.of(queda));
        when(orcamentoRepository.findByUsuarioId(1L)).thenReturn(List.of());

        List<InsightDTO> resultado = insightGeralService.listarInsights(1L);

        assertEquals(List.of(queda), resultado);
        verifyNoInteractions(insightOrcamentoService);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaDadosSuficientes() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(aumentoGastosService.analisarAumento(1L)).thenReturn(Optional.empty());
        when(quedaRendaService.analisarQueda(1L)).thenReturn(Optional.empty());
        when(orcamentoRepository.findByUsuarioId(1L)).thenReturn(List.of(orcamentoComId(1L)));
        when(insightOrcamentoService.orcamentoProximoDoLimite(1L)).thenReturn(Optional.empty());
        when(insightOrcamentoService.orcamentoExcedido(1L)).thenReturn(Optional.empty());

        assertTrue(insightGeralService.listarInsights(1L).isEmpty());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> insightGeralService.listarInsights(99L));

        verifyNoInteractions(aumentoGastosService, quedaRendaService,
                orcamentoRepository, insightOrcamentoService);
    }
}
