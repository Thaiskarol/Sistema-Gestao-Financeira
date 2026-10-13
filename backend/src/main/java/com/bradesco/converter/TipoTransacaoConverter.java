package com.bradesco.converter;

import com.bradesco.enums.TipoTransacao;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoTransacaoConverter
        implements AttributeConverter<TipoTransacao, String> {

    @Override
    public String convertToDatabaseColumn(TipoTransacao tipo) {

        if (tipo == null) {
            return null;
        }

        return tipo.name().toLowerCase();
    }

    @Override
    public TipoTransacao convertToEntityAttribute(String valor) {

        if (valor == null) {
            return null;
        }

        return TipoTransacao.valueOf(valor.toUpperCase());
    }
}