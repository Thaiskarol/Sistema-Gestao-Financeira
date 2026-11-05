
package com.bradesco.controller;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.PercentualMetaDTO;
import com.bradesco.dto.response.TempoEstimadoMetaDTO;
import com.bradesco.service.ProgressoMetaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/metas")
public class ProgressoMetaController {

    private final ProgressoMetaService progressoMetaService;

    public ProgressoMetaController(ProgressoMetaService progressoMetaService) {
        this.progressoMetaService = progressoMetaService;
    }

    @GetMapping("/{id}/percentual")
    public ResponseEntity<ApiResponse<PercentualMetaDTO>> calcularPercentual(
            @PathVariable("id") Long id,
            @RequestParam("usuarioId") Long usuarioId) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Percentual atingido calculado com sucesso.",
                progressoMetaService.calcularPercentualAtingido(id, usuarioId)));
    }

    @GetMapping("/{id}/tempo-estimado")
    public ResponseEntity<ApiResponse<TempoEstimadoMetaDTO>> calcularTempoEstimado(
            @PathVariable("id") Long id,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam(value = "aporteMensal", required = false)
            BigDecimal aporteMensal) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Tempo estimado calculado com sucesso.",
                progressoMetaService.calcularTempoEstimado(
                        id, usuarioId, aporteMensal)));
    }
}
