package com.bradesco.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.RelatorioMensalDTO;
import com.bradesco.service.RelatorioService;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/mensal")
    public ResponseEntity<ApiResponse<RelatorioMensalDTO>> relatorioMensal(
            @RequestParam Long usuarioId) {

        RelatorioMensalDTO dados =
                relatorioService.gerarRelatorioMensal(usuarioId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Relatório mensal gerado com sucesso.",
                        dados
                )
        );
    }
}
