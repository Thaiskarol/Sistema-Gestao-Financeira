package com.bradesco.gestao_financeira.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping ("/api/transacoes")
public class TransacaoController {
  
  @GetMapping 
  public String listar() {
    return "Lista de transações";
  }
}
