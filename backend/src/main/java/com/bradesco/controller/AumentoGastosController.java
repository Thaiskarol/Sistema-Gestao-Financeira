package com.bradesco.controller;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.InsightDTO;
import com.bradesco.service.AumentoGastosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/insights")
public class AumentoGastosController {
  
  private final AumentoGastosService aumentoGastosService;

  public AumentoGastosController(AumentoGastosService aumentoGastosService) {
      this.aumentoGastosService = aumentoGastosService;
  }

  @GetMapping("/aumento-gastos")
  public ResponseEntity<ApiResponse<InsightDTO>> aumentoGastos(
          @RequestParam("usuarioId") Long usuarioId) {

      InsightDTO insight = aumentoGastosService.analisarAumento(usuarioId).orElse(null);

      return ResponseEntity.ok(new ApiResponse<>(
          true,
          "Análise de aumento de gastos realizada com sucesso.",
          insight));
  }
}
