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

        TransacaoResponseDTO responseDTO = new TransacaoResponseDTO(
            transacao.getId(),
            transacao.getDescricao(),
            transacao.getValor(),
            transacao.getDataTransacao(),
            transacao.getRecorrencia(),
            transacao.getTipo(),
            transacao.getCategoria().getId(),
            transacao.getUsuario().getId()
        );

        ApiResponse<TransacaoResponseDTO> response = new ApiResponse<>(
                true,
                "Transação cadastrada com sucesso.",
                responseDTO
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}