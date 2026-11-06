package com.bradesco.controller;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.service.ClassificacaoScoreService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/score")
public class ClassificacaoScoreController {

    private final ClassificacaoScoreService classificacaoScoreService;

    public ClassificacaoScoreController(
            ClassificacaoScoreService classificacaoScoreService) {
        this.classificacaoScoreService = classificacaoScoreService;
    }

    @GetMapping("/classificacao")
    public ResponseEntity<ApiResponse<?>> consultarClassificacao(
            @RequestParam("usuarioId") Long usuarioId) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Score classificado com sucesso.",
                        classificacaoScoreService.consultarClassificacao(usuarioId)
                )
        );
    }
}