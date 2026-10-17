package com.bradesco.controller;

import com.bradesco.dto.request.TransacaoRequestDTO;
import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.TransacaoResponseDTO;
import com.bradesco.entity.Transacao;
import com.bradesco.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransacaoResponseDTO>> cadastrar(
            @Valid @RequestBody TransacaoRequestDTO dados) {

        Transacao transacao = transacaoService.cadastrar(dados);

        ApiResponse<TransacaoResponseDTO> response = new ApiResponse<>(
                true,
                "Transação cadastrada com sucesso.",
                transacaoService.toResponse(transacao)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransacaoResponseDTO>>> listar(
            @RequestParam Long usuarioId) {

        List<TransacaoResponseDTO> transacoes = transacaoService.listarPorUsuario(usuarioId);

        ApiResponse<List<TransacaoResponseDTO>> response = new ApiResponse<>(
                true,
                "Histórico financeiro recuperado com sucesso.",
                transacoes
        );

        return ResponseEntity.ok(response);
    }
}
