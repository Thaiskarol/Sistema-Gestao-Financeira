package com.bradesco.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import com.bradesco.dto.request.SenhaAlteracaoDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bradesco.dto.request.UsuarioAtualizacaoDTO;
import com.bradesco.dto.request.UsuarioCadastroDTO;
import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.UsuarioResponseDTO;
import com.bradesco.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> cadastrar(
            @Valid @RequestBody UsuarioCadastroDTO dto) {

        UsuarioResponseDTO usuario = usuarioService.cadastrar(dto);

        ApiResponse<UsuarioResponseDTO> response = new ApiResponse<>(
                true,
                "Usuário cadastrado com sucesso.",
                usuario
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<UsuarioResponseDTO>> buscarPorId(
                @PathVariable Long id) {

        UsuarioResponseDTO usuario = usuarioService.buscarPorId(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Usuário recuperado com sucesso.", usuario)
        );
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<UsuarioResponseDTO>> atualizar(
                @PathVariable Long id,
                @Valid @RequestBody UsuarioAtualizacaoDTO dto) {

        UsuarioResponseDTO usuario = usuarioService.atualizar(id, dto);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Perfil atualizado com sucesso.", usuario)
        );
        }


        @PatchMapping("/{id}/senha")
        public ResponseEntity<ApiResponse<Void>> alterarSenha(
                @PathVariable Long id,
                @Valid @RequestBody SenhaAlteracaoDTO dto) {

        usuarioService.alterarSenha(id, dto);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Senha alterada com sucesso.", null)
        );
        }


}