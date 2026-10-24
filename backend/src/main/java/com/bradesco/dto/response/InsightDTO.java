package com.bradesco.dto.response;

import java.math.BigDecimal;

public record InsightDTO(
  String tipo,
  String categoria,
  BigDecimal percentual,
  String mensagem
) {}