package com.pn.career.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Converter
public class JpaConverterFlexible implements AttributeConverter<Object, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        try {
            return attribute == null ? null : objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Lỗi chuyển đổi đối tượng thành JSON", e);
        }
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return new ArrayList<>();
            }

            // Kiểm tra xem dữ liệu bắt đầu bằng mảng hay đối tượng
            if (dbData.trim().startsWith("[")) {
                return objectMapper.readValue(dbData, new TypeReference<List<Object>>() {});
            } else if (dbData.trim().startsWith("{")) {
                return objectMapper.readValue(dbData, new TypeReference<Map<String, Object>>() {});
            } else {
                // Trường hợp giá trị đơn lẻ
                return dbData;
            }
        } catch (IOException e) {
            System.err.println("Lỗi chuyển đổi JSON: " + e.getMessage());
            System.err.println("Dữ liệu JSON gây lỗi: " + dbData);
            // Trả về một giá trị an toàn thay vì ném ngoại lệ
            return new ArrayList<>();
        }
    }
}
