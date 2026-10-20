package com.bradesco.controller;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.GrupoValorDTO;
import com.bradesco.service.GastosService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/gastos")
public class GastosController {
  
      private final GastosService gastosService;

    public GastosController(GastosService gastosService) {
        this.gastosService = gastosService;
    }

    @GetMapping("/por-categoria")
    public ResponseEntity<ApiResponse<List<GrupoValorDTO>>> porCategoria(
            @RequestParam("usuarioId") Long usuarioId) {

        return ResponseEntity.ok(new ApiResponse<>(
              true,
              "Gastos por categoria listados com sucesso.",
              gastosService.gastosPorCategoria(usuarioId)));
    }

    @GetMapping("/por-estabelecimento")
    public ResponseEntity<ApiResponse<List<GrupoValorDTO>>> porEstabelecimento(
            @RequestParam("usuarioId") Long usuarioId) {

        return ResponseEntity.ok(new ApiResponse<>(
              true,
              "Gastos por estabelecimento listados com sucesso.",
              gastosService.gastosPorEstabelecimento(usuarioId)));
    }

    @GetMapping("/por-periodo")
    public ResponseEntity<ApiResponse<GrupoValorDTO>> porPeriodo(
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        return ResponseEntity.ok(new ApiResponse<>(
              true,
              "Gastos do período calculados com sucesso.",
              gastosService.gastosPorPeriodo(usuarioId, dataInicio, dataFim)));
    }
}
