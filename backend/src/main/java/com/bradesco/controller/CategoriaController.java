package com.bradesco.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bradesco.dto.request.CategoriaRequestDTO;
import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.CategoriaResponseDTO;
import com.bradesco.service.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaResponseDTO>>> listar(
            @RequestParam(value = "usuarioId", required = false) Long usuarioId) {

        List<CategoriaResponseDTO> categorias = usuarioId == null
                ? categoriaService.listar()
                : categoriaService.listar(usuarioId);

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Categorias recuperadas com sucesso.", categorias));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaResponseDTO>> criar(
            @Valid @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                true, "Categoria criada com sucesso.", categoriaService.criar(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponseDTO>> atualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Categoria atualizada com sucesso.", categoriaService.atualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(
            @PathVariable("id") Long id,
            @RequestParam("usuarioId") Long usuarioId) {
        categoriaService.excluir(id, usuarioId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Categoria excluída com sucesso.", null));
    }
}