package com.bradesco.service;

import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bradesco.exception.ResourceNotFoundException;

import com.bradesco.dto.response.ClassificacaoScoreDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bradesco.dto.response.ScoreSaudeFinanceiraDTO;
import com.bradesco.enums.NivelSaudeFinanceira;

@ExtendWith(MockitoExtension.class)
class ClassificacaoScoreServiceTest {

    @Mock
    private ScoreSaudeFinanceiraService scoreSaudeFinanceiraService;

    @InjectMocks
    private ClassificacaoScoreService service;

    @Test
    void deveClassificarScoreCritico() {
        assertEquals(NivelSaudeFinanceira.CRITICO, service.classificar(0));
        assertEquals(NivelSaudeFinanceira.CRITICO, service.classificar(39));
    }

    @Test
    void deveClassificarScoreAtencao() {
        assertEquals(NivelSaudeFinanceira.ATENCAO, service.classificar(40));
        assertEquals(NivelSaudeFinanceira.ATENCAO, service.classificar(59));
    }

    @Test
    void deveClassificarScoreBom() {
        assertEquals(NivelSaudeFinanceira.BOM, service.classificar(60));
        assertEquals(NivelSaudeFinanceira.BOM, service.classificar(79));
    }

    @Test
    void deveClassificarScoreExcelente() {
        assertEquals(NivelSaudeFinanceira.EXCELENTE, service.classificar(80));
        assertEquals(NivelSaudeFinanceira.EXCELENTE, service.classificar(100));
    }

    @Test
    void deveLancarExcecaoParaScoreForaDoIntervalo() {
        assertThrows(IllegalArgumentException.class, () -> service.classificar(-1));
        assertThrows(IllegalArgumentException.class, () -> service.classificar(101));
    }

    @Test
void deveConsultarClassificacaoComScoreBom() {

    when(scoreSaudeFinanceiraService.consultarScore(1L))
            .thenReturn(new ScoreSaudeFinanceiraDTO(
                    68,
                    true,
                    "Score financeiro calculado com sucesso.",
                    List.of()
            ));

    ClassificacaoScoreDTO resultado =
            service.consultarClassificacao(1L);

    assertEquals(1L, resultado.usuarioId());
    assertEquals(68, resultado.score());
    assertEquals(NivelSaudeFinanceira.BOM, resultado.nivel());
    assertEquals("Bom", resultado.descricao());

    verify(scoreSaudeFinanceiraService).consultarScore(1L);
}

@Test
void deveRetornarDadosInsuficientesQuandoNaoHouverDados() {

    when(scoreSaudeFinanceiraService.consultarScore(1L))
            .thenReturn(new ScoreSaudeFinanceiraDTO(
                    0,
                    false,
                    "Dados insuficientes para calcular o score financeiro.",
                    List.of()
            ));

    ClassificacaoScoreDTO resultado =
            service.consultarClassificacao(1L);

    assertEquals(1L, resultado.usuarioId());
    assertEquals(null, resultado.score());
    assertEquals(null, resultado.nivel());
    assertEquals(
            "Dados insuficientes para classificar o score.",
            resultado.descricao()
    );
}

@Test
void deveLancarExcecaoQuandoUsuarioNaoExistir() {

    when(scoreSaudeFinanceiraService.consultarScore(99L))
            .thenThrow(new ResourceNotFoundException(
                    "Usuário não encontrado."
            ));

    ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> service.consultarClassificacao(99L)
    );

    assertEquals("Usuário não encontrado.", exception.getMessage());

    verify(scoreSaudeFinanceiraService).consultarScore(99L);
}
}