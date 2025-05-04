package com.pn.career.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class JpaConverterList implements AttributeConverter<List<Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Object> attribute) {
        try {
            return attribute == null ? null : objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Lỗi chuyển đổi List thành JSON", e);
        }
    }

    @Override
    public List<Object> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(dbData, new TypeReference<List<Object>>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("Lỗi chuyển đổi JSON thành List", e);
        }
    }
}
