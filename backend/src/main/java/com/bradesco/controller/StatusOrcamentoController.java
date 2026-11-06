package com.bradesco.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.AlertaOrcamentoDTO;
import com.bradesco.dto.response.StatusOrcamentoDTO;
import com.bradesco.service.StatusOrcamentoService;

@RestController
@RequestMapping("/orcamentos")
public class StatusOrcamentoController {

    private final StatusOrcamentoService statusOrcamentoService;

    public StatusOrcamentoController(StatusOrcamentoService statusOrcamentoService) {
        this.statusOrcamentoService = statusOrcamentoService;
    }

    @GetMapping("/{id}/status")
    public ApiResponse<StatusOrcamentoDTO> obterStatus(@PathVariable Long id) {

        StatusOrcamentoDTO status =
                statusOrcamentoService.obterStatus(id);

        return new ApiResponse<>(
                true,
                "Status do orçamento recuperado com sucesso.",
                status
        );
    }

    @GetMapping("/{id}/alerta")
    public ApiResponse<AlertaOrcamentoDTO> gerarAlerta(@PathVariable Long id) {

        AlertaOrcamentoDTO alerta =
                statusOrcamentoService.gerarAlerta(id);

        return new ApiResponse<>(
                true,
                "Alerta do orçamento gerado com sucesso.",
                alerta
        );
    }
}