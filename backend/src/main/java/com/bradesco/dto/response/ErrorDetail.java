package com.bradesco.dto.response;

public record ErrorDetail(
  String field, 
  String message
) {

}
