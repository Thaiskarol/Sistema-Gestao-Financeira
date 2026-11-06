package com.bradesco.controller;

import com.bradesco.dto.request.MetaRequestDTO;
import com.bradesco.dto.request.MetaValorAcumuladoDTO;
import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.MetaResponseDTO;
import com.bradesco.service.MetaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/metas")
public class MetaController {

    private final MetaService metaService;

    public MetaController(MetaService metaService) {
        this.metaService = metaService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MetaResponseDTO>> criar(@Valid @RequestBody MetaRequestDTO dados) {
        MetaResponseDTO meta = metaService.criar(dados);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, "Meta criada com sucesso.", meta));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MetaResponseDTO>>> listar(@RequestParam Long usuarioId) {
        List<MetaResponseDTO> metas = metaService.listarPorUsuario(usuarioId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Metas recuperadas com sucesso.", metas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MetaResponseDTO>> buscar(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {

        MetaResponseDTO meta = metaService.buscar(id, usuarioId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Meta recuperada com sucesso.", meta));
    }

    @PatchMapping("/{id}/valor-acumulado")
    public ResponseEntity<ApiResponse<MetaResponseDTO>> atualizarValorAcumulado(
            @PathVariable Long id,
            @Valid @RequestBody MetaValorAcumuladoDTO dados) {

        MetaResponseDTO meta = metaService.atualizarValorAcumulado(id, dados);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Valor acumulado atualizado com sucesso.", meta));
    }
}
