package com.bradesco.controller;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.ResumoFinanceiroDTO;
import com.bradesco.dto.response.TransacaoResponseDTO;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.service.ConsultaFinanceiraService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/consulta")
public class ConsultaController {

    private final ConsultaFinanceiraService consultaFinanceiraService;

    public ConsultaController(ConsultaFinanceiraService consultaFinanceiraService) {
        this.consultaFinanceiraService = consultaFinanceiraService;
    }

    // Feature 01 — GET /consulta/resumo?usuarioId=1
    @GetMapping("/resumo")
    public ResponseEntity<ApiResponse<ResumoFinanceiroDTO>> consultarResumo(@RequestParam Long usuarioId) {
        ResumoFinanceiroDTO resumo = consultaFinanceiraService.consultarResumo(usuarioId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Resumo financeiro recuperado com sucesso.", resumo));
    }

    // Feature 02 — GET /consulta/filtro?usuarioId=1&dataInicio=...&tipo=DESPESA&...
    @GetMapping("/filtro")
    public ResponseEntity<ApiResponse<List<TransacaoResponseDTO>>> filtrar(
            @RequestParam Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) TipoTransacao tipo,
            @RequestParam(required = false) String estabelecimento,
            @RequestParam(required = false) String recorrencia) {
        List<TransacaoResponseDTO> movimentacoes = consultaFinanceiraService.filtrar(
            usuarioId, dataInicio, dataFim, categoriaId, tipo, estabelecimento, recorrencia);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Movimentações filtradas com sucesso.", movimentacoes));
    }
}
