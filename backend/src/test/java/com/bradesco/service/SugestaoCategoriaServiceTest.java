package com.bradesco.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SugestaoCategoriaServiceTest {

    private final SugestaoCategoriaService service =
            new SugestaoCategoriaService();

    @Test
    void deveSugerirAlimentacaoParaSupermercado() {

        String resultado =
                service.sugerirCategoria("Supermercado Bom Preço");

        assertEquals("Alimentação", resultado);
    }

    @Test
    void naoDeveSugerirCategoriaQuandoEstabelecimentoForVazio() {

        String resultado =
                service.sugerirCategoria("");

        assertNull(resultado);
    }

    @Test
    void naoDeveSugerirCategoriaQuandoEstabelecimentoForNulo() {

        String resultado =
                service.sugerirCategoria(null);

        assertNull(resultado);
    }
}