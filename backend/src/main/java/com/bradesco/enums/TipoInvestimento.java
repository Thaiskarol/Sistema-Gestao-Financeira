package com.bradesco.enums;

public enum TipoInvestimento {

    RENDA_FIXA("Renda fixa"),
    FUNDOS("Fundos"),
    ACOES("Ações"),
    CRIPTOATIVOS("Criptoativos"),
    OUTROS("Outros");

    private final String descricao;

    TipoInvestimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
