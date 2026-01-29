package com.negocio.adris.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class Utils {
    public static String capitalize(String str){
        if (str == null || str.isEmpty()){
            str = "-";
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String bigDecimalFormatter(BigDecimal n){
        NumberFormat format = NumberFormat.getInstance(new Locale("es", "AR"));
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);

        return format.format(n);
    }

    public static String dateTimeFormatter(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        return date.format(formatter);
    }
}
