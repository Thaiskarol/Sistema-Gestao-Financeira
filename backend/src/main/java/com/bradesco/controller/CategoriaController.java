package com.bradesco.controller;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.CategoriaResponseDTO;
import com.bradesco.service.CategoriaService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaResponseDTO>>> listar() {

        List<CategoriaResponseDTO> categorias = categoriaService.listar();

        ApiResponse<List<CategoriaResponseDTO>> response = new ApiResponse<>(
                true,
                "Categorias recuperadas com sucesso.",
                categorias
        );

        return ResponseEntity.ok(response);
    }
}
