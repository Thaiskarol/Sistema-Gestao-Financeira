package com.bradesco.controller;

import com.bradesco.dto.request.RendaRequestDTO;
import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.RendaResponseDTO;
import com.bradesco.service.RendaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rendas")
public class RendaController {

    private final RendaService rendaService;

    public RendaController(RendaService rendaService) {
        this.rendaService = rendaService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RendaResponseDTO>> cadastrar(@Valid @RequestBody RendaRequestDTO dados) {
        RendaResponseDTO renda = rendaService.cadastrar(dados);
        ApiResponse<RendaResponseDTO> response =
                new ApiResponse<>(true, "Renda cadastrada com sucesso.", renda);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/recorrente")
    public ResponseEntity<ApiResponse<RendaResponseDTO>> cadastrarRecorrente(
            @Valid @RequestBody RendaRequestDTO dados) {

        RendaResponseDTO salva = rendaService.cadastrarReceitaRecorrente(dados);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<>(true, "Receita recorrente cadastrada com sucesso.", salva));
    }

    @PostMapping("/variavel")
    public ResponseEntity<ApiResponse<RendaResponseDTO>> cadastrarVariavel(
            @Valid @RequestBody RendaRequestDTO dados) {

        RendaResponseDTO salva = rendaService.cadastrarReceitaVariavel(dados);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<>(true, "Receita variável cadastrada com sucesso.", salva));
    }
}
