package com.bradesco.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bradesco.dto.request.EsqueciSenhaDTO;
import com.bradesco.dto.request.RedefinirSenhaDTO;
import com.bradesco.dto.response.ApiResponse;
import com.bradesco.service.RecuperacaoSenhaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class RecuperacaoSenhaController {

    private final RecuperacaoSenhaService recuperacaoSenhaService;

    public RecuperacaoSenhaController(RecuperacaoSenhaService recuperacaoSenhaService) {
        this.recuperacaoSenhaService = recuperacaoSenhaService;
    }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<ApiResponse<Void>> esqueciSenha(@Valid @RequestBody EsqueciSenhaDTO dto) {
        recuperacaoSenhaService.solicitar(dto);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Se o e-mail estiver cadastrado, você receberá as instruções para redefinir a senha.",
                null));
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<ApiResponse<Void>> redefinirSenha(@Valid @RequestBody RedefinirSenhaDTO dto) {
        recuperacaoSenhaService.redefinir(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Senha redefinida com sucesso.", null));
    }
}