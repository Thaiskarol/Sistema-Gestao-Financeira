package com.bradesco.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bradesco.dto.request.InvestimentoRequestDTO;
import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.CarteiraInvestimentoDTO;
import com.bradesco.dto.response.InvestimentoResponseDTO;
import com.bradesco.service.InvestimentoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/investimentos")
public class InvestimentoController {

    private final InvestimentoService investimentoService;

    public InvestimentoController(InvestimentoService investimentoService) {
        this.investimentoService = investimentoService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InvestimentoResponseDTO>> registrar(
            @Valid @RequestBody InvestimentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                true, "Investimento registrado com sucesso.", investimentoService.registrar(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InvestimentoResponseDTO>>> listar(
            @RequestParam("usuarioId") Long usuarioId) {
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Investimentos listados com sucesso.", investimentoService.listar(usuarioId)));
    }

    @GetMapping("/carteira")
    public ResponseEntity<ApiResponse<CarteiraInvestimentoDTO>> carteira(
            @RequestParam("usuarioId") Long usuarioId) {
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Carteira calculada com sucesso.", investimentoService.carteira(usuarioId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(
            @PathVariable("id") Long id,
            @RequestParam("usuarioId") Long usuarioId) {
        investimentoService.excluir(id, usuarioId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Investimento excluído com sucesso.", null));
    }
}