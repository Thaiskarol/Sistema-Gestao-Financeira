package com.bradesco.gestao_financeira.controller;

import com.bradesco.controller.TransacaoController;

/**
 * WebMvcTest
 */
public @interface WebMvcTest {

  Class<TransacaoController> value();

}
