package com.bradesco.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CarteiraInvestimentoDTO(List<ItemCarteiraDTO> itens, BigDecimal total) {}
