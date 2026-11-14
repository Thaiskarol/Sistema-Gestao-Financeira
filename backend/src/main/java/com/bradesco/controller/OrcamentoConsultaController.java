package com.bradesco.controller;

import com.bradesco.dto.request.OrcamentoAtualizacaoDTO;
import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.OrcamentoDetalheDTO;
import com.bradesco.dto.response.OrcamentoResponseDTO;
import com.bradesco.service.OrcamentoConsultaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orcamentos")
public class OrcamentoConsultaController {

    private final OrcamentoConsultaService orcamentoConsultaService;

    public OrcamentoConsultaController(OrcamentoConsultaService orcamentoConsultaService) {
        this.orcamentoConsultaService = orcamentoConsultaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrcamentoDetalheDTO>>> listar(
            @RequestParam("usuarioId") Long usuarioId) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Orçamentos listados com sucesso.",
                orcamentoConsultaService.listar(usuarioId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrcamentoResponseDTO>> atualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody OrcamentoAtualizacaoDTO dados) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Orçamento atualizado com sucesso.",
                orcamentoConsultaService.atualizar(id, dados)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> excluir(
            @PathVariable("id") Long id,
            @RequestParam("usuarioId") Long usuarioId) {

        orcamentoConsultaService.excluir(id, usuarioId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Orçamento excluído com sucesso.",
                null));
    }
}
