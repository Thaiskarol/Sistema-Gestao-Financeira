package com.bradesco.gestao_financeira.dto.response;

public record ApiResponse<T>(
  boolean success,
  String message,
  T data
) {

}
