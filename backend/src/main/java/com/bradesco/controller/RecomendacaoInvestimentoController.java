package com.bradesco.controller;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.RecomendacaoInvestimentoDTO;
import com.bradesco.dto.response.ReservaEmergenciaDTO;
import com.bradesco.service.RecomendacaoInvestimentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recomendacoes")
public class RecomendacaoInvestimentoController {

    private final RecomendacaoInvestimentoService recomendacaoInvestimentoService;

    public RecomendacaoInvestimentoController(
            RecomendacaoInvestimentoService recomendacaoInvestimentoService) {
        this.recomendacaoInvestimentoService = recomendacaoInvestimentoService;
    }

    @GetMapping("/investimento")
    public ResponseEntity<ApiResponse<RecomendacaoInvestimentoDTO>> recomendarInvestimento(
            @RequestParam Long usuarioId) {

        RecomendacaoInvestimentoDTO dados =
            recomendacaoInvestimentoService.recomendarPercentualMinimo(usuarioId);

        return ResponseEntity.ok(
            new ApiResponse<>(true, "Recomendação de investimento gerada com sucesso.", dados));
    }

    @GetMapping("/reserva-emergencia")
    public ResponseEntity<ApiResponse<ReservaEmergenciaDTO>> calcularReservaEmergencia(
            @RequestParam Long usuarioId) {

        ReservaEmergenciaDTO dados =
            recomendacaoInvestimentoService.calcularReservaEmergencia(usuarioId);

        return ResponseEntity.ok(
            new ApiResponse<>(true, "Valor ideal de reserva de emergência calculado com sucesso.", dados));
    }
}
