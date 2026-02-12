package com.negocio.adris.utils;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

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

    public static TextFormatter<String> telefonoFormatter(){
        return new TextFormatter<>(
                new StringConverter<String>() {
                    @Override
                    public String toString(String s) {
                        return s;
                    }

                    @Override
                    public String fromString(String s) {
                        return s;
                    }
                },
                "",
                c -> c.getControlNewText().matches("\\d*") ? c : null
        );
    }

    public static StringConverter<Month> mesFormatter(){
        return new StringConverter<Month>() {
            @Override
            public String toString(Month month) {
                if (month == null) return "";
                return month.getDisplayName(TextStyle.FULL, Locale.getDefault());
            }

            @Override
            public Month fromString(String string) {
                return null; // no se usa
            }
        };
    }
}
