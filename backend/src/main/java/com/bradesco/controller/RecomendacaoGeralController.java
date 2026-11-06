package com.bradesco.controller;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.RecomendacaoGeralDTO;
import com.bradesco.service.RecomendacaoGeralService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recomendacoes")
public class RecomendacaoGeralController {

    private final RecomendacaoGeralService recomendacaoGeralService;

    public RecomendacaoGeralController(
            RecomendacaoGeralService recomendacaoGeralService) {
        this.recomendacaoGeralService = recomendacaoGeralService;
    }

    @GetMapping("/geral")
    public ApiResponse<RecomendacaoGeralDTO> gerarRecomendacoes(
            @RequestParam Long usuarioId) {

        RecomendacaoGeralDTO recomendacoes =
                recomendacaoGeralService.gerarRecomendacoes(usuarioId);

        return new ApiResponse<>(
                true,
                "Recomendações financeiras geradas com sucesso.",
                recomendacoes
        );
    }
}