package com.pn.career.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pn.career.responses.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ExportService implements IExportService{
    private final IUserService userService;

    @Override
    public ByteArrayInputStream exportUserToPdf(List<UserResponse> userResponses) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            String fontPath = getClass().getClassLoader().getResource("fonts/arial.ttf").getPath();
            // Tạo font với encoding Identity-H để hỗ trợ Unicode
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            Font vietnameseFont = new Font(baseFont, 11);
            Font vietnameseBoldFont = new Font(baseFont, 11, Font.BOLD);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);

            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Add title
            Paragraph title = new Paragraph("Danh sách người dùng", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);

            float[] columnWidths = new float[]{8f, 15f, 20f, 25f, 15f, 15f, 10f};
            table.setWidths(columnWidths);

            String[] headers = {"STT", "Họ", "Tên", "Email", "Số điện thoại", "Ngày sinh", "Vai trò"};
            for (String headerTitle : headers) {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setPhrase(new Phrase(headerTitle, vietnameseBoldFont));
                header.setPadding(5);
                header.setMinimumHeight(25f); // Thêm chiều cao tối thiểu
                table.addCell(header);
            }

            int i = 0;
            for (UserResponse user : userResponses) {
                PdfPCell sttCell = new PdfPCell(new Phrase(String.valueOf(++i), vietnameseFont));
                sttCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sttCell);

                PdfPCell lastNameCell = new PdfPCell(new Phrase(user.getLastName(), vietnameseFont));
                lastNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(lastNameCell);

                PdfPCell firstNameCell = new PdfPCell(new Phrase(user.getFirstName(), vietnameseFont));
                firstNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(firstNameCell);

                PdfPCell emailCell = new PdfPCell(new Phrase(user.getEmail(), vietnameseFont));
                emailCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(emailCell);

                PdfPCell phoneCell = new PdfPCell(new Phrase(user.getPhone(), vietnameseFont));
                phoneCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(phoneCell);

                PdfPCell dobCell = new PdfPCell(new Phrase(String.valueOf(user.getDob()), vietnameseFont));
                dobCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(dobCell);

                PdfPCell roleCell = new PdfPCell(new Phrase(user.getRole(), vietnameseFont));
                roleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(roleCell);
            }

            document.add(table);
            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public ByteArrayInputStream exportUserExcel(List<UserResponse> userResponses) {
        return null;
    }
}
