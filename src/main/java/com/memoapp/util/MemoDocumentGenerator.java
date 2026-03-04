package com.memoapp.util;

import com.memoapp.model.Memo;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class MemoDocumentGenerator {

    public static byte[] generateMemoDocument(Memo memo) throws IOException {
        XWPFDocument document = new XWPFDocument();
        
        // Add margins
        CTSectPr sectPr = document.getDocument().getBody().getSectPr();
        if (sectPr == null) {
            sectPr = document.getDocument().getBody().addNewSectPr();
        }

        // Add header with "MEMORANDUM"
        XWPFParagraph headerParagraph = document.createParagraph();
        headerParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun headerRun = headerParagraph.createRun();
        headerRun.setText("MEMORANDUM");
        headerRun.setBold(true);
        headerRun.setFontSize(14);
        
        // Add empty line
        document.createParagraph();

        // Add memo details section
        addMemoField(document, "TO:", memo.getToRecipients());
        addMemoField(document, "FROM:", memo.getFromSender());
        
        if (memo.getCcRecipients() != null && !memo.getCcRecipients().isEmpty()) {
            addMemoField(document, "CC:", memo.getCcRecipients());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String dateFormatted = memo.getCreatedAt() != null ? memo.getCreatedAt().format(formatter) : "N/A";
        addMemoField(document, "DATE:", dateFormatted);
        addMemoField(document, "RE:", memo.getTitle());

        // Add horizontal line separator
        XWPFParagraph separator = document.createParagraph();
        separator.setBorderBottom(org.apache.poi.xwpf.usermodel.Borders.SINGLE);

        // Add empty line
        document.createParagraph();

        // Add memo body
        addMemoBody(document, memo.getBody());

        // Add attachments section if present
        if (memo.getAttachments() != null && !memo.getAttachments().isEmpty()) {
            document.createParagraph();
            XWPFParagraph attachmentsParagraph = document.createParagraph();
            XWPFRun attachmentsRun = attachmentsParagraph.createRun();
            attachmentsRun.setText("Attachments: " + memo.getAttachments());
            attachmentsRun.setItalic(true);
        }

        // Convert to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.write(outputStream);
        document.close();

        return outputStream.toByteArray();
    }

    private static void addMemoField(XWPFDocument document, String label, String value) {
        XWPFParagraph paragraph = document.createParagraph();
        
        XWPFRun labelRun = paragraph.createRun();
        labelRun.setText(label);
        labelRun.setBold(true);
        
        XWPFRun valueRun = paragraph.createRun();
        valueRun.setText(" " + (value != null ? value : ""));
    }

    private static void addMemoBody(XWPFDocument document, String body) {
        XWPFParagraph bodyParagraph = document.createParagraph();
        XWPFRun bodyRun = bodyParagraph.createRun();
        bodyRun.setText(body != null ? body : "");
        
        // Set line spacing and formatting
        bodyParagraph.setSpacingBetweenLines(240); // 1.5 line spacing
    }

}
