package com.bradesco.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.ComparativoMensalDTO;
import com.bradesco.dto.response.ValorMensalDTO;
import com.bradesco.service.EvolucaoService;

@RestController
@RequestMapping("/evolucao")
public class EvolucaoController {

    private final EvolucaoService evolucaoService;

    public EvolucaoController(EvolucaoService evolucaoService) {
        this.evolucaoService = evolucaoService;
    }

    @GetMapping("/despesas")
    public ResponseEntity<ApiResponse<List<ValorMensalDTO>>> evolucaoDespesas(
            @RequestParam Long usuarioId) {

        List<ValorMensalDTO> dados =
                evolucaoService.evolucaoMensalDespesas(usuarioId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Evolução mensal das despesas consultada com sucesso.",
                        dados
                )
        );
    }

    @GetMapping("/receitas")
    public ResponseEntity<ApiResponse<List<ValorMensalDTO>>> evolucaoReceitas(
            @RequestParam Long usuarioId) {

        List<ValorMensalDTO> dados =
                evolucaoService.evolucaoMensalReceitas(usuarioId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Evolução mensal das receitas consultada com sucesso.",
                        dados
                )
        );
    }

    @GetMapping("/comparativo")
    public ResponseEntity<ApiResponse<ComparativoMensalDTO>> comparativo(
            @RequestParam Long usuarioId,
            @RequestParam String mesA,
            @RequestParam String mesB) {

        ComparativoMensalDTO dados =
                evolucaoService.compararMeses(usuarioId, mesA, mesB);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Comparativo mensal consultado com sucesso.",
                        dados
                )
        );
    }
}