package com.bradesco.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ImportacaoRequestDTO(

  @NotNull (message = "O usuário é obrigatório.")
  Long usuarioId,

  @NotEmpty (message = "A lista de transações não pode ser vazia.")

  @Valid 
  List<TransacaoRequestDTO> transacoes
) {}


