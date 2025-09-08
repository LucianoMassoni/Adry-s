package com.negocio.adris.utils;

public abstract class Utils {
    public static String capitalize(String str){
        if (str.isEmpty() || str == null){
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
