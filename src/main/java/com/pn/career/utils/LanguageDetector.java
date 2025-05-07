package com.pn.career.utils;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class LanguageDetector {

    private static final Pattern VIETNAMESE_PATTERN = Pattern.compile(".*[àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ].*");

    private static final String[] VIETNAMESE_KEYWORDS = {
            "không", "và", "là", "có", "được", "trong", "của", "một", "những", "các",
            "cho", "để", "với", "tôi", "bạn", "xin", "cảm", "ơn", "chào", "việc"
    };

    private static final String[] ENGLISH_KEYWORDS = {
            "the", "and", "is", "are", "in", "of", "to", "for", "with", "that",
            "have", "this", "from", "by", "not", "be", "on", "at", "you", "can"
    };

    /**
     * Phát hiện ngôn ngữ của văn bản đầu vào
     * @param text Văn bản cần phát hiện ngôn ngữ
     * @return "vi" cho tiếng Việt, "en" cho tiếng Anh
     */
    public String detectLanguage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "en"; // Mặc định là tiếng Anh nếu không có văn bản
        }

        String lowerText = text.toLowerCase();

        if (VIETNAMESE_PATTERN.matcher(lowerText).matches()) {
            return "vi";
        }

        int viKeywordCount = countKeywords(lowerText, VIETNAMESE_KEYWORDS);
        int enKeywordCount = countKeywords(lowerText, ENGLISH_KEYWORDS);

        if (viKeywordCount > enKeywordCount) {
            return "vi";
        } else {
            return "en";
        }
    }

    /**
     * Đếm số lượng từ khóa xuất hiện trong văn bản
     */
    private int countKeywords(String text, String[] keywords) {
        int count = 0;
        for (String keyword : keywords) {
            String pattern = "\\b" + keyword + "\\b";
            Pattern p = Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(text);

            while (m.find()) {
                count++;
            }
        }
        return count;
    }
}