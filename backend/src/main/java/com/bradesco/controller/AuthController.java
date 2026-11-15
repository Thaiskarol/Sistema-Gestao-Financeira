package com.bradesco.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bradesco.dto.request.LoginRequestDTO;
import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.UsuarioResponseDTO;
import com.bradesco.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO dto) {

        UsuarioResponseDTO usuario = usuarioService.login(dto);

        ApiResponse<UsuarioResponseDTO> response = new ApiResponse<>(
                true,
                "Login realizado com sucesso.",
                usuario
        );

        return ResponseEntity.ok(response);
    }
}