package com.bradesco.controller;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.InsightDTO;
import com.bradesco.service.InsightGeralService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/insights")
public class InsightGeralController {

    private final InsightGeralService insightGeralService;

    public InsightGeralController(InsightGeralService insightGeralService) {
        this.insightGeralService = insightGeralService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InsightDTO>>> listar(
            @RequestParam("usuarioId") Long usuarioId) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Insights gerados com sucesso.",
                insightGeralService.listarInsights(usuarioId)));
    }
}
