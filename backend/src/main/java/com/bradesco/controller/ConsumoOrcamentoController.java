package com.bradesco.controller;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.ConsumoOrcamentoDTO;
import com.bradesco.service.ConsumoOrcamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orcamentos")
public class ConsumoOrcamentoController {

  private final ConsumoOrcamentoService consumoOrcamentoService;
  
  public ConsumoOrcamentoController(ConsumoOrcamentoService consumoOrcamentoService) {
    this.consumoOrcamentoService = consumoOrcamentoService;
  }

  @GetMapping("/{id}/consumo")
  public ResponseEntity<ApiResponse<ConsumoOrcamentoDTO>> calcularConsumo(
        @PathVariable("id") Long id) {

    return ResponseEntity.ok(new ApiResponse<>(
        true,
        "Consumo do orçamento calculado com sucesso.",
        consumoOrcamentoService.calcularConsumo(id)));
  }
}
