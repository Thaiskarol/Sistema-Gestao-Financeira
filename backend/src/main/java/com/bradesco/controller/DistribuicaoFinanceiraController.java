package com.bradesco.controller;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.DistribuicaoFinanceiraDTO;
import com.bradesco.service.DistribuicaoFinanceiraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/distribuicao-financeira")
public class DistribuicaoFinanceiraController {

    private final DistribuicaoFinanceiraService distribuicaoFinanceiraService;

    public DistribuicaoFinanceiraController(
            DistribuicaoFinanceiraService distribuicaoFinanceiraService) {
        this.distribuicaoFinanceiraService = distribuicaoFinanceiraService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DistribuicaoFinanceiraDTO>> calcular(
            @RequestParam Long usuarioId) {

        DistribuicaoFinanceiraDTO dados =
            distribuicaoFinanceiraService.calcularDistribuicao(usuarioId);

        return ResponseEntity.ok(
            new ApiResponse<>(true, "Distribuição financeira calculada com sucesso.", dados));
    }
}
