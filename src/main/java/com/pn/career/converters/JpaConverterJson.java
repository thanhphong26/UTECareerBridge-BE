package com.pn.career.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Converter
public class JpaConverterJson implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return attribute == null ? null : objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // Log lỗi chi tiết
            System.err.println("Lỗi chuyển đổi Map thành JSON: " + e.getMessage());
            throw new IllegalArgumentException("Lỗi chuyển đổi Map thành JSON", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(dbData, Map.class);
        } catch (IOException e) {
            // Log lỗi chi tiết
            System.err.println("Lỗi chuyển đổi JSON thành Map: " + e.getMessage());
            System.err.println("Dữ liệu gây lỗi: " + dbData);
            throw new IllegalArgumentException("Lỗi chuyển đổi JSON thành Map", e);
        }
    }
}