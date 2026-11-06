package com.bradesco.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bradesco.dto.request.OrcamentoRequestDTO;
import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.OrcamentoResponseDTO;
import com.bradesco.service.OrcamentoService;

@RestController
@RequestMapping("/orcamentos")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    public OrcamentoController(OrcamentoService orcamentoService) {
        this.orcamentoService = orcamentoService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrcamentoResponseDTO>> criar(
            @Valid @RequestBody OrcamentoRequestDTO dados) {

        OrcamentoResponseDTO orcamento = orcamentoService.criar(dados);

        ApiResponse<OrcamentoResponseDTO> response = new ApiResponse<>(
                true,
                "Orçamento criado com sucesso.",
                orcamento
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
