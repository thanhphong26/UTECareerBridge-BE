package com.pn.career.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatCurrency {
    public static String formatCurrency(long number) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormat.format(number);
    }
}
