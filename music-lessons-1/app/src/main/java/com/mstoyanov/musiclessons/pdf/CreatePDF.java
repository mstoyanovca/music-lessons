package com.mstoyanov.musiclessons.pdf;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mstoyanov.musiclessons.R;
import com.mstoyanov.musiclessons.model.Cell;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreatePDF {
    private static List<Cell> cells = new ArrayList<>();
    private static String[] weekdays_array;
    private static Context context;
    private static String name;
    private static final Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    private static final Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
    private static final Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
    private static final Font smallFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

    public CreatePDF(List<Cell> cells, String name, Context context) {
        CreatePDF.cells = cells;
        CreatePDF.context = context;
        CreatePDF.name = name;
        weekdays_array = context.getResources().getStringArray(
                R.array.weekdays_array);
    }

    public boolean exportPDF() {
        if (!isExternalStorageWritable()) {
            Toast.makeText(context, "External storage not accessible!", Toast.LENGTH_SHORT).show();
            return false;
        }
        String fileName = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).toString() + "/Schedule.pdf";
        try {
            Document document = new Document(PageSize.LETTER.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.setPageSize(PageSize.LETTER.rotate());
            document.setMargins(72, 36, 36, 36);
            document.open();
            addMetaData(document);
            addContent(document);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(context,
                "Schedule exported to external storage/Download", Toast.LENGTH_LONG).show();
        return true;
    }

    private static void addMetaData(Document document) {
        document.addTitle("Music School");
        document.addSubject("Music Lessons Weekly Schedule");
        document.addKeywords("Piano, Music Theory, Lessons");
        document.addAuthor(name);
        document.addCreator("Created with iTextG under the APGL");
    }

    private static void addContent(Document document) throws DocumentException {
        String dateString = new Date().toString();
        Paragraph par = new Paragraph("Music Lessons Weekly Schedule", catFont);
        par.add(new Paragraph("Teacher: " + name + ", Created on: " + dateString, subFont));
        par.add(new Paragraph(" ", smallFont));
        par.add(createTable());
        document.add(par);
    }

    private static PdfPTable createTable() throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setTotalWidth(new float[]{114, 114, 114, 114, 114, 114});
        table.setLockedWidth(true);
        PdfPCell cell;

        cell = new PdfPCell(new Phrase("Monday", smallBold));
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Tuesday", smallBold));
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Wednesday", smallBold));
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Thursday", smallBold));
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Friday", smallBold));
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Saturday", smallBold));
        table.addCell(cell);

        table.setHeaderRows(1);

        while (cells.size() > 0) {                             // rows
            for (String aWeekdays_array : weekdays_array) {  // columns
                boolean cell_empty = true;
                for (int j = 0; j < cells.size(); j++) {
                    if (cells.get(j).getWeekDay().equalsIgnoreCase(aWeekdays_array)) {
                        Phrase phrase = new Phrase(cells.get(j).getTimeFrom()
                                + " - " + cells.get(j).getTimeTo() + "\n"
                                + cells.get(j).getStudentName() + "\n"
                                + cells.get(j).getPhoneNumber(), smallFont);
                        table.addCell(phrase);
                        cell_empty = false;
                        cells.remove(j);
                        break;
                    }
                }
                if (cell_empty) {
                    table.addCell(new Phrase("\n" + "\n" + "\n", smallFont));
                }
            }
        }
        return table;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}