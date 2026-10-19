package com.bradesco.service;

import org.springframework.stereotype.Service;

@Service
public class SugestaoCategoriaService {

    public String sugerirCategoria(String estabelecimento) {

        if (estabelecimento == null || estabelecimento.isBlank()) {
            return null;
        }

        String texto = estabelecimento.toLowerCase();

        if (texto.contains("supermercado")
                || texto.contains("mercado")
                || texto.contains("carrefour")
                || texto.contains("atacadão")) {
            return "Alimentação";
        }

        if (texto.contains("posto")
                || texto.contains("combustível")
                || texto.contains("gasolina")) {
            return "Transporte";
        }

        if (texto.contains("farmácia")
                || texto.contains("farmacia")
                || texto.contains("drogaria")) {
            return "Saúde";
        }

        return "Outros";
    }
}