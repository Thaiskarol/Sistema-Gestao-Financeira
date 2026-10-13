package com.bradesco.gestao_financeira.dto.response;

import java.util.List;

public record ErrorResponse(
  boolean success,
  String message,
  Object data,
  List<ErrorDetail> errors
) {

}
