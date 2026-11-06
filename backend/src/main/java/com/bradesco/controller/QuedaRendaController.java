package com.bradesco.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bradesco.dto.response.ApiResponse;
import com.bradesco.dto.response.InsightDTO;
import com.bradesco.service.QuedaRendaService;

@RestController
@RequestMapping("/insights")
public class QuedaRendaController {

    private final QuedaRendaService quedaRendaService;

    public QuedaRendaController(QuedaRendaService quedaRendaService) {
        this.quedaRendaService = quedaRendaService;
    }

    @GetMapping("/queda-renda")
    public ApiResponse<?> analisarQueda(
            @RequestParam Long usuarioId) {

        return new ApiResponse<>(
                true,
                "Análise de queda de renda realizada com sucesso.",
                quedaRendaService.analisarQueda(usuarioId)
        );
    }
}