package com.bradesco.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.ScoreHistoricoDTO;
import com.bradesco.service.ScoreHistoricoService;

@RestController
@RequestMapping("/score")
public class ScoreHistoricoController {

    private final ScoreHistoricoService scoreHistoricoService;

    public ScoreHistoricoController(ScoreHistoricoService scoreHistoricoService) {
        this.scoreHistoricoService = scoreHistoricoService;
    }

    @GetMapping("/historico")
    public ResponseEntity<ApiResponse<List<ScoreHistoricoDTO>>> historico(
            @RequestParam("usuarioId") Long usuarioId) {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Histórico do score consultado com sucesso.",
                scoreHistoricoService.consultar(usuarioId)));
    }
}