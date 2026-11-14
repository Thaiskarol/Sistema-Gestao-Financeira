package com.bradesco.dto.response;

import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoResponseDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate dataTransacao,
        String recorrencia,
        TipoTransacao tipo,
        Long categoriaId,
        Long usuarioId,
        String estabelecimento,
        String categoriaNome
) {

    public static TransacaoResponseDTO from(Transacao transacao) {
        return new TransacaoResponseDTO(
                transacao.getId(),
                transacao.getDescricao(),
                transacao.getValor(),
                transacao.getDataTransacao(),
                transacao.getRecorrencia(),
                transacao.getTipo(),
                transacao.getCategoria() != null ? transacao.getCategoria().getId() : null,
                transacao.getUsuario() != null ? transacao.getUsuario().getId() : null,
                transacao.getEstabelecimento(),
                transacao.getCategoria() != null ? transacao.getCategoria().getNome() : null
        );
    }
}
