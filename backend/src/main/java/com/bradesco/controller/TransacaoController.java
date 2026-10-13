package com.bradesco.controller;

import com.bradesco.dto.request.TransacaoRequestDTO;
import com.bradesco.dto.response.ApiResponse;
import com.bradesco.entity.Transacao;
import com.bradesco.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Transacao>> cadastrar(
            @Valid @RequestBody TransacaoRequestDTO dados) {

        Transacao transacao = transacaoService.cadastrar(dados);

        ApiResponse<Transacao> response = new ApiResponse<>(
                true,
                "Transação cadastrada com sucesso.",
                transacao
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}