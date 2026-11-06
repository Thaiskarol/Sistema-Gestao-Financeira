package com.bradesco.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.InsightDTO;
import com.bradesco.service.InsightOrcamentoService;

@RestController
@RequestMapping("/insights")
public class InsightOrcamentoController {

    private final InsightOrcamentoService insightOrcamentoService;

    public InsightOrcamentoController(InsightOrcamentoService insightOrcamentoService) {
        this.insightOrcamentoService = insightOrcamentoService;
    }

    // Feature 01
    @GetMapping("/orcamento/{id}/proximo-limite")
    public ResponseEntity<ApiResponse<Optional<InsightDTO>>> proximoDoLimite(
            @PathVariable("id") Long id) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Análise de orçamento próximo do limite realizada com sucesso.",
                insightOrcamentoService.orcamentoProximoDoLimite(id)));
    }

    // Feature 02
    @GetMapping("/orcamento/{id}/excedido")
    public ResponseEntity<ApiResponse<Optional<InsightDTO>>> excedido(
            @PathVariable("id") Long id) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Análise de orçamento excedido realizada com sucesso.",
                insightOrcamentoService.orcamentoExcedido(id)));
    }
}
