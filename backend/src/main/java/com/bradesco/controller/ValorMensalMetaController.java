package com.bradesco.controller;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.ValorMensalMetaDTO;
import com.bradesco.service.ValorMensalMetaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metas")
public class ValorMensalMetaController {
  
      private final ValorMensalMetaService valorMensalMetaService;

    public ValorMensalMetaController(ValorMensalMetaService valorMensalMetaService) {
        this.valorMensalMetaService = valorMensalMetaService;
    }

    @GetMapping("/{id}/valor-mensal")
    public ResponseEntity<ApiResponse<ValorMensalMetaDTO>> valorMensal(
            @PathVariable("id") Long id,
            @RequestParam("usuarioId") Long usuarioId) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Valor mensal necessário calculado com sucesso.",
                valorMensalMetaService.consultarValorMensal(id, usuarioId)));
    }
}
