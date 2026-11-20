package com.bradesco.controller;

import com.bradesco.service.ScoreHistoricoService;
import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.ScoreSaudeFinanceiraDTO;
import com.bradesco.service.ScoreSaudeFinanceiraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/score")
public class ScoreSaudeFinanceiraController {

    private final ScoreSaudeFinanceiraService scoreSaudeFinanceiraService;
    private final ScoreHistoricoService scoreHistoricoService;

    public ScoreSaudeFinanceiraController(ScoreSaudeFinanceiraService scoreSaudeFinanceiraService,
                                          ScoreHistoricoService scoreHistoricoService) {
        this.scoreSaudeFinanceiraService = scoreSaudeFinanceiraService;
        this.scoreHistoricoService = scoreHistoricoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ScoreSaudeFinanceiraDTO>> consultarScore(
            @RequestParam("usuarioId") Long usuarioId) {

        ScoreSaudeFinanceiraDTO score = scoreSaudeFinanceiraService.consultarScore(usuarioId);
        scoreHistoricoService.registrar(usuarioId, score);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Score financeiro calculado com sucesso.",
                score));
    }
}

