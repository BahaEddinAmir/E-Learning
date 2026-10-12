package com.arabsoft.elearning.service;

import com.arabsoft.elearning.entity.Certificate;
import com.arabsoft.elearning.entity.Course;
import com.arabsoft.elearning.entity.User;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class CertificateGeneratorService {

    public byte[] generateCertificate(Certificate certificate, User student, Course course) {
        try {
            Document document = new Document(PageSize.A4.rotate());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            // Fond bleu
            PdfContentByte canvas = writer.getDirectContentUnder();
            canvas.setColorFill(new BaseColor(30, 58, 95));
            canvas.rectangle(0, 0, document.getPageSize().getWidth(),
                    document.getPageSize().getHeight());
            canvas.fill();

            // Fond blanc central
            canvas.setColorFill(BaseColor.WHITE);
            canvas.roundRectangle(40, 40,
                    document.getPageSize().getWidth() - 80,
                    document.getPageSize().getHeight() - 80, 20);
            canvas.fill();

            // Titre
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 42, Font.BOLD,
                    new BaseColor(30, 58, 95));
            Paragraph title = new Paragraph("Certificat d'accomplissement", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(80);
            document.add(title);

            // Séparateur
            Font lineFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL,
                    new BaseColor(33, 150, 243));
            Paragraph line = new Paragraph("─────────────────────────────────────", lineFont);
            line.setAlignment(Element.ALIGN_CENTER);
            document.add(line);

            // Texte "présenté à"
            Font subFont = new Font(Font.FontFamily.HELVETICA, 16, Font.ITALIC,
                    new BaseColor(100, 100, 100));
            Paragraph sub = new Paragraph("Ce certificat est décerné à", subFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingBefore(20);
            document.add(sub);

            // Nom de l'étudiant
            Font nameFont = new Font(Font.FontFamily.HELVETICA, 36, Font.BOLD,
                    new BaseColor(30, 58, 95));
            Paragraph name = new Paragraph(
                    student.getFirstName() + " " + student.getLastName(), nameFont);
            name.setAlignment(Element.ALIGN_CENTER);
            name.setSpacingBefore(10);
            document.add(name);

            // Texte "pour avoir complété"
            Paragraph completed = new Paragraph("pour avoir complété avec succès le cours", subFont);
            completed.setAlignment(Element.ALIGN_CENTER);
            completed.setSpacingBefore(20);
            document.add(completed);

            // Nom du cours
            Font courseFont = new Font(Font.FontFamily.HELVETICA, 28, Font.BOLD,
                    new BaseColor(33, 150, 243));
            Paragraph courseName = new Paragraph(course.getTitle(), courseFont);
            courseName.setAlignment(Element.ALIGN_CENTER);
            courseName.setSpacingBefore(10);
            document.add(courseName);

            // Date
            Font dateFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL,
                    new BaseColor(100, 100, 100));
            String date = certificate.getIssuedAt()
                    .format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
            Paragraph datePara = new Paragraph("Délivré le " + date, dateFont);
            datePara.setAlignment(Element.ALIGN_CENTER);
            datePara.setSpacingBefore(30);
            document.add(datePara);

            // Numéro unique
            Font certFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL,
                    new BaseColor(150, 150, 150));
            Paragraph certNum = new Paragraph(
                    "Certificat N° : " + certificate.getUniqueNumber(), certFont);
            certNum.setAlignment(Element.ALIGN_CENTER);
            certNum.setSpacingBefore(10);
            document.add(certNum);

            // Logo ArabSoft
            Font logoFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD,
                    new BaseColor(30, 58, 95));
            Paragraph logo = new Paragraph("🎓 ArabSoft E-Learning", logoFont);
            logo.setAlignment(Element.ALIGN_CENTER);
            logo.setSpacingBefore(30);
            document.add(logo);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération certificat : " + e.getMessage());
        }
    }
}