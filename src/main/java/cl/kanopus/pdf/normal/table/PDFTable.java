/*-
 * !--
 * For support and inquiries regarding this library, please contact:
 *   soporte@kanopus.cl
 * 
 * Project website:
 *   https://www.kanopus.cl
 * %%
 * Copyright (C) 2025 Pablo Díaz Saavedra
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * --!
 */
package cl.kanopus.pdf.normal.table;


import cl.kanopus.pdf.FontFamily;
import com.itextpdf.text.*;
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

    @Override
    public void setTotalWidth(final float[] columnWidth) {
        try {
            super.setTotalWidth(columnWidth);
        } catch (DocumentException ex) {
            throw new IllegalArgumentException("Cannot adjust total width", ex);
        }
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
