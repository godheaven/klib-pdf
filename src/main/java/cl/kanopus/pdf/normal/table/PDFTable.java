package cl.kanopus.pdf.normal.table;

import cl.kanopus.pdf.FontFamily;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class PDFTable extends PdfPTable {

    public enum Align {
        CENTER(1),
        LEFT(0),
        RIGHT(2);

        private final int value;

        Align(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

    public PDFTable(int numColumns) {
        super(numColumns);
    }

    // create cells
    private PdfPCell createLabel(String text, int horizontalAlignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(horizontalAlignment);
        return cell;
    }

    private PdfPCell createValue(String text, int horizontalAlignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(horizontalAlignment);
        cell.setBorder(this.getDefaultCell().getBorder());
        return cell;
    }

    // create cells
    public void createLabelCell(String text) {
        super.addCell(createLabel(text, Element.ALIGN_CENTER, FontFamily.FONT_8_BLACK_NORMAL));
    }

    public void createLabelCellWithRowspan(String text, int rowspan) {
        createLabelCellWithRowspan(text, rowspan, Align.CENTER);
    }

    public void createLabelCellWithRowspan(String text, int rowspan, Align align) {
        PdfPCell cell = createLabel(text, align.getValue(), FontFamily.FONT_8_BLACK_NORMAL);
        cell.setRowspan(rowspan);
        super.addCell(cell);
    }

    public void createLabelCellWithColspan(String text, int colspan) {
        createLabelCellWithColspan(text, colspan, Align.CENTER);
    }

    public void createLabelCellWithColspan(String text, int colspan, Align align) {
        createLabelCellWithColspan(text, colspan, align, FontFamily.FONT_8_BLACK_NORMAL);
    }

    public void createLabelCellWithColspan(String text, int colspan, Align align, Font font) {
        PdfPCell cell = createLabel(text, align.getValue(), font);
        cell.setColspan(colspan);
        super.addCell(cell);
    }

    // create cells
    public void createValueCell(String text) {
        createValueCell(text, Align.LEFT, FontFamily.FONT_8_BLACK_NORMAL);
    }

    public void createValueCell(String text, Align align) {
        createValueCell(text, align, FontFamily.FONT_8_BLACK_NORMAL);
    }

    public void createValueCell(Image image) {
        super.addCell(image);
    }

    public void createValueCell(String text, Align align, Font font) {
        super.addCell(createValue(text, align.getValue(), font));
    }

    public void createValueCellWithRowspan(String text, int rowspan) {
        createValueCellWithRowspan(text, rowspan, Align.CENTER);
    }

    public void createValueCellWithRowspan(String text, int rowspan, Align align) {
        PdfPCell cell = createValue(text, align.getValue(), FontFamily.FONT_8_BLACK_NORMAL);
        cell.setRowspan(rowspan);
        super.addCell(cell);
    }

    public void createValueCellWithColspan(String text, int colspan) {
        createValueCellWithColspan(text, colspan, Align.CENTER);
    }

    public void createValueCellWithColspan(String text, int colspan, Align align) {
        createValueCellWithColspan(text, colspan, align, FontFamily.FONT_8_BLACK_NORMAL);
    }

    public void createValueCellWithColspan(String text, int colspan, Align align, Font font) {
        PdfPCell cell = createValue(text, align.getValue(), font);
        cell.setColspan(colspan);
        super.addCell(cell);
    }

    public void createLabelCellWithColspanAndRowspan(String text, int colspan, int rowspan) {
        PdfPCell cell = createLabel(text, Element.ALIGN_CENTER, FontFamily.FONT_8_BLACK_NORMAL);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        super.addCell(cell);
    }
}
