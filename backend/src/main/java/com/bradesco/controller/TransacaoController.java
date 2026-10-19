package com.bradesco.controller;

import com.bradesco.dto.request.ImportacaoRequestDTO;
import com.bradesco.dto.request.TransacaoRequestDTO;
import com.bradesco.dto.request.TransacaoUpdateDTO;
import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.TransacaoResponseDTO;
import com.bradesco.entity.Transacao;
import com.bradesco.service.TransacaoService;
import jakarta.validation.Valid;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransacaoResponseDTO>>> listar(
            @RequestParam Long usuarioId) {

        List<TransacaoResponseDTO> transacoes =
                transacaoService.listarPorUsuario(usuarioId);

        ApiResponse<List<TransacaoResponseDTO>> response = new ApiResponse<>(
                true,
                "Histórico financeiro recuperado com sucesso.",
                transacoes
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransacaoResponseDTO>> editar(
            @PathVariable Long id,
            @Valid @RequestBody TransacaoUpdateDTO dados) {

        TransacaoResponseDTO transacao = transacaoService.editar(id, dados);

        ApiResponse<TransacaoResponseDTO> response = new ApiResponse<>(
                true,
                "Transação atualizada com sucesso.",
                transacao
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/categoria")
    public ResponseEntity<ApiResponse<TransacaoResponseDTO>> classificar(
            @PathVariable Long id,
            @RequestParam Long categoriaId) {

        TransacaoResponseDTO transacao = transacaoService.classificar(id, categoriaId);

        ApiResponse<TransacaoResponseDTO> response = new ApiResponse<>(
                true,
                "Despesa classificada com sucesso.",
                transacao
        );

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> excluir(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {

        transacaoService.excluir(id, usuarioId);

        ApiResponse<Object> response = new ApiResponse<>(
                true,
                "Transação excluída com sucesso.",
                null
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/importar")
    public ResponseEntity<ApiResponse<List<TransacaoResponseDTO>>> importar(
            @Valid @RequestBody ImportacaoRequestDTO dados) {

        List<TransacaoResponseDTO> importadas =
                transacaoService.importar(dados);

        ApiResponse<List<TransacaoResponseDTO>> response = new ApiResponse<>(
                true,
                "Transações importadas com sucesso.",
                importadas
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
