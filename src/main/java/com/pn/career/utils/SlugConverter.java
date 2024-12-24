package com.pn.career.utils;

import java.text.Normalizer;

public class SlugConverter {
    public static String toSlug(String input){
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Thay thế các ký tự không phải chữ hoặc số thành gạch dưới
        slug = slug.replaceAll("[^a-zA-Z0-9]", "_");

        // Loại bỏ dấu gạch dưới dư thừa
        slug = slug.replaceAll("_+", "_");

        return slug.toLowerCase();
    }
}
