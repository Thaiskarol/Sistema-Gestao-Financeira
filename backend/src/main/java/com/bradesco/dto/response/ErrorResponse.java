package com.bradesco.dto.response;

import java.util.List;

public record ErrorResponse(
  boolean success,
  String message,
  Object data,
  List<ErrorDetail> errors
) {

}
