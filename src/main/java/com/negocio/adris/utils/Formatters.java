package com.negocio.adris.utils;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.math.BigDecimal;

public class Formatters {

    public static TextFormatter<BigDecimal> bigDecimalFormatter() {
        return new TextFormatter<>(
                new BigDecimalStringConverter(),
                null,
                c -> c.getControlNewText().matches("\\d*(\\.\\d*)?") ? c : null
        );
    }

    public static TextFormatter<BigDecimal> bigDecimalFormatter(BigDecimal n) {
        return new TextFormatter<>(
                new BigDecimalStringConverter(),
                n,
                c -> c.getControlNewText().matches("\\d*(\\.\\d*)?") ? c : null
        );
    }

    public static TextFormatter<Double> doubleFormatter() {
        return new TextFormatter<>(
                new DoubleStringConverter(),
                0.0,
                c -> c.getControlNewText().matches("-?\\d*(\\.\\d*)?") ? c : null
        );
    }

    public static TextFormatter<Integer> integerFormatter() {
        return new TextFormatter<>(
                new IntegerStringConverter(),
                0,
                c -> c.getControlNewText().matches("\\d*") ? c : null
        );
    }
}
