package com.bradesco.controller;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.EvolucaoPatrimonialDTO;
import com.bradesco.dto.response.MetaSugeridaDTO;
import com.bradesco.service.PatrimonioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patrimonio")
public class PatrimonioController {

    private final PatrimonioService patrimonioService;

    public PatrimonioController(PatrimonioService patrimonioService) {
        this.patrimonioService = patrimonioService;
    }

    @GetMapping("/evolucao")
    public ResponseEntity<ApiResponse<List<EvolucaoPatrimonialDTO>>> evolucao(
            @RequestParam("usuarioId") Long usuarioId) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Evolução patrimonial recuperada com sucesso.",
                patrimonioService.evolucaoPatrimonial(usuarioId)));
    }

    @GetMapping("/metas-sugeridas")
    public ResponseEntity<ApiResponse<List<MetaSugeridaDTO>>> metasSugeridas(
            @RequestParam("usuarioId") Long usuarioId) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Metas sugeridas geradas com sucesso.",
                patrimonioService.metasSugeridas(usuarioId)));
    }
}
