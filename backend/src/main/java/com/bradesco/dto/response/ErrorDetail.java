package com.bradesco.gestao_financeira.dto.response;

public record ErrorDetail(
  String field, 
  String message
) {

}
