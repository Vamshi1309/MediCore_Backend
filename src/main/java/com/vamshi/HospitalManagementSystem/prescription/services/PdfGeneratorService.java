package com.vamshi.HospitalManagementSystem.prescription.services;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import com.vamshi.HospitalManagementSystem.prescription.entities.PrescriptionEntity;
import com.vamshi.HospitalManagementSystem.prescription.entities.PrescriptionItemEntity;

@Service
public class PdfGeneratorService {
        public byte[] generatePrescriptionPdf(PrescriptionEntity prescription) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                try {
                        PdfWriter writer = new PdfWriter(outputStream);
                        PdfDocument pdfDoc = new PdfDocument(writer);
                        Document document = new Document(pdfDoc);

                        Paragraph title = new Paragraph("MediCore Hospitals")
                                        .setFontSize(18)
                                        .setBold()
                                        .setTextAlignment(TextAlignment.CENTER);

                        document.add(title);

                        Paragraph subtitle = new Paragraph("Medical Prescription")
                                        .setFontSize(14)
                                        .setTextAlignment(TextAlignment.CENTER)
                                        .setMarginBottom(20);

                        document.add(subtitle);

                        document.add(new Paragraph(
                                        "Patient Name: "
                                                        + prescription.getPatient().getName())
                                        .setBold());

                        document.add(new Paragraph(
                                        "Doctor Name: Dr. "
                                                        + prescription.getDoctor().getName())
                                        .setBold());

                        document.add(new Paragraph(
                                        "Date: "
                                                        + prescription.getCreatedAt())
                                        .setMarginBottom(15));

                        com.itextpdf.layout.element.Table table = new Table(UnitValue.createPercentArray(
                                        new float[] { 1, 2, 2, 2, 3 })).setWidth(UnitValue.createPercentValue(100));

                        table.addHeaderCell(headerCell("S.No"));
                        table.addHeaderCell(headerCell("Medicine"));
                        table.addHeaderCell(headerCell("Dosage"));
                        table.addHeaderCell(headerCell("Duration"));
                        table.addHeaderCell(headerCell("Instructions"));

                        int count = 1;
                        for (PrescriptionItemEntity item : prescription.getItems()) {

                                table.addCell(new Cell().add(
                                                new Paragraph(String.valueOf(count++))));
                                table.addCell(new Cell().add(
                                                new Paragraph(item.getMedicine().getMedicineName())));
                                table.addCell(new Cell().add(
                                                new Paragraph(item.getDosage())));
                                table.addCell(new Cell().add(
                                                new Paragraph(
                                                                item.getDuration().toString() + " days")));
                                table.addCell(new Cell().add(
                                                new Paragraph(
                                                                item.getInstructions() != null
                                                                                ? item.getInstructions()
                                                                                : "-")));
                        }

                        document.add(table);

                        document.add(new Paragraph(
                                        "\n\nThis is a computer generated "
                                                        + "prescription.")
                                        .setFontSize(8)
                                        .setTextAlignment(TextAlignment.CENTER));

                        document.close();

                } catch (Exception e) {
                        throw new RuntimeException("Failed to load PDF", e);
                }

                return outputStream.toByteArray();
        }

        private Cell headerCell(String text) {
                return new Cell()
                                .add(new Paragraph(text).setBold())
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                .setTextAlignment(TextAlignment.CENTER);
        }
}
