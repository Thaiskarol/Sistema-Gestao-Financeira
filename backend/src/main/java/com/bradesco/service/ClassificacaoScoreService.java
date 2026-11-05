package com.bradesco.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bradesco.dto.response.ClassificacaoScoreDTO;
import com.bradesco.dto.response.ScoreSaudeFinanceiraDTO;
import com.bradesco.enums.NivelSaudeFinanceira;

@Service
public class ClassificacaoScoreService {

    private static final String MSG_INSUFICIENTE =
            "Dados insuficientes para classificar o score.";

    private final ScoreSaudeFinanceiraService scoreSaudeFinanceiraService;

    public ClassificacaoScoreService(
            ScoreSaudeFinanceiraService scoreSaudeFinanceiraService) {
        this.scoreSaudeFinanceiraService = scoreSaudeFinanceiraService;
    }

    public NivelSaudeFinanceira classificar(int score) {

        if (score < 0 || score > 100) {
            throw new IllegalArgumentException(
                    "O score deve estar entre 0 e 100.");
        }

        if (score <= 39) {
            return NivelSaudeFinanceira.CRITICO;
        }

        if (score <= 59) {
            return NivelSaudeFinanceira.ATENCAO;
        }

        if (score <= 79) {
            return NivelSaudeFinanceira.BOM;
        }

        return NivelSaudeFinanceira.EXCELENTE;
    }

    @Transactional(readOnly = true)
    public ClassificacaoScoreDTO consultarClassificacao(Long usuarioId) {

        ScoreSaudeFinanceiraDTO resultado =
                scoreSaudeFinanceiraService.consultarScore(usuarioId);

        if (!resultado.dadosSuficientes()) {
            return new ClassificacaoScoreDTO(
                    usuarioId,
                    null,
                    null,
                    MSG_INSUFICIENTE);
        }

        NivelSaudeFinanceira nivel = classificar(resultado.score());

        return new ClassificacaoScoreDTO(
                usuarioId,
                resultado.score(),
                nivel,
                nivel.getDescricao());
    }
}