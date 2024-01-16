package com.github.rafaelfernandes.parquimetro.estacionamento.config;

import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;
import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter implements Converter<String, TipoPeriodo> {
    @Override
    public TipoPeriodo convert(String source) {
        try {
            return TipoPeriodo.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
