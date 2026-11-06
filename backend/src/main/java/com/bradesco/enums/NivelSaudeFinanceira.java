package com.bradesco.enums;

public enum NivelSaudeFinanceira {

    CRITICO("Crítico"),
    ATENCAO("Atenção"),
    BOM("Bom"),
    EXCELENTE("Excelente");

    private final String descricao;

    NivelSaudeFinanceira(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}