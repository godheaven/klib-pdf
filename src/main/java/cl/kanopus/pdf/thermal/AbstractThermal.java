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
package cl.kanopus.pdf.thermal;

import cl.kanopus.common.util.Utils;
import cl.kanopus.pdf.Barcodes;
import cl.kanopus.pdf.DocumentPrinterException;
import cl.kanopus.pdf.FontFamily;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.List;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.Image;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.ColumnText;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfGState;
import org.openpdf.text.pdf.PdfWriter;
import org.openpdf.text.pdf.draw.LineSeparator;

abstract class AbstractThermal {

    private Document document = null;
    private PdfContentByte canvas = null;
    private ByteArrayOutputStream baos = null;
    private boolean closed = false;

    private final int pageWidth;
    private final int pageHeight;
    private final int marginLeft;
    private final int marginRight;
    private int positionY = 0;

    private Font font = FontFamily.FONT_8_BLACK_NORMAL;
    private Space space = Space.NORMAL;

    protected AbstractThermal(int pageWidth, int pageHeight, int marginLeft, int marginRight) throws DocumentPrinterException {
        try {
            this.pageWidth = pageWidth;
            this.marginLeft = marginLeft;
            this.marginRight = marginRight;
            this.pageHeight = pageHeight;

            document = new Document(new Rectangle(pageWidth, pageHeight));
            document.setMargins(marginLeft, marginRight, 2, 2);
            document.setMarginMirroringTopBottom(false);

            baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();
            canvas = writer.getDirectContent();

        } catch (DocumentException ex) {
            throw new DocumentPrinterException("Unable to initialize document", ex);
        }
    }

    public enum Space {
        LOW(10),
        NORMAL(15),
        HIGHT(20);
        public final int size;

        Space(int size) {
            this.size = size;
        }
    }

    public enum Align {
        LEFT(Element.ALIGN_LEFT),
        CENTER(Element.ALIGN_CENTER),
        RIGHT(Element.ALIGN_RIGHT);

        final int position;

        Align(int size) {
            this.position = size;
        }
    }

    public int getPageWidth() {
        return pageWidth;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public int getPositionY() {
        return positionY;
    }

    protected void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    /**
     * @param font
     *            the font used by the following prints; {@code null} restores the default 8pt font
     */
    public void setFont(Font font) {
        this.font = (font == null) ? FontFamily.FONT_8_BLACK_NORMAL : font;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    protected void printSplit(String text, int maxLineSize) {
        printSplit(null, text, maxLineSize);
    }

    protected void printSplit(String label, String text, int maxLineSize) {
        if (!Utils.isNullOrEmpty(text)) {
            if (label != null) {
                text = label.concat(text);
            }
            List<String> lines = Utils.splitText(text, maxLineSize);
            for (String l : lines) {
                println(l);
            }
        }
    }

    protected void println(String text) {
        printText(text, marginLeft, true, Align.LEFT);
    }

    protected void println(String text, int positionX) {
        printText(text, positionX, true, Align.LEFT);
    }

    protected void println(String text, Align align) {
        Align a = (align == null) ? Align.LEFT : align;
        printText(text, positionFor(a), true, a);
    }

    protected void println(String text, int positionX, Align align) {
        printText(text, positionX, true, align);
    }

    protected void print(String text) {
        printText(text, marginLeft, false, Align.LEFT);
    }

    protected void print(String text, int positionX) {
        printText(text, positionX, false, Align.LEFT);
    }

    protected void print(String text, Align align) {
        Align a = (align == null) ? Align.LEFT : align;
        printText(text, positionFor(a), false, a);
    }

    protected void print(String text, int positionX, Align align) {
        printText(text, positionX, false, align);
    }

    private int positionFor(Align align) {
        return switch (align) {
            case CENTER -> getPageWidth() / 2;
            case RIGHT -> getPageWidth() - getMarginRight();
            default -> marginLeft;
        };
    }

    /**
     * Prints the text (if not null) and, for {@code println}, moves to the next line. A {@code null} text still advances the line, consistently with the normal page printer.
     */
    private void printText(String text, int positionX, boolean automaticNewLine, Align align) {
        if (text != null) {
            Phrase phrase = new Phrase(text, font);
            ColumnText.showTextAligned(this.canvas, (align == null) ? Align.LEFT.position : align.position, phrase, positionX, positionY, 0);
        }
        positionY = automaticNewLine ? positionY - space.size : positionY;
    }

    protected void printLine() {
        printLine(marginLeft, pageWidth - marginRight, Align.CENTER, false);
    }

    protected void printLine(int leftX, int rigthX, Align align, boolean bold) {
        int alignment = (align == null) ? Element.ALIGN_CENTER : align.position;
        LineSeparator lineSeparator = new LineSeparator(bold ? 1 : 0, 100, Color.BLACK, alignment, 0);
        lineSeparator.drawLine(this.canvas, leftX, rigthX, positionY);
        positionY = positionY - space.size;
    }

    protected void drawRectangle(float x, float y, float width, float height, Color color, int lineWidth) {
        Color stroke = (color == null) ? Color.BLACK : color;
        this.canvas.saveState();
        PdfGState state = new PdfGState();
        state.setFillOpacity(0);
        this.canvas.setGState(state);
        this.canvas.setRGBColorStroke(stroke.getRed(), stroke.getGreen(), stroke.getBlue());
        this.canvas.setLineWidth(lineWidth);
        this.canvas.rectangle(x, y - height, width, height);
        this.canvas.fillStroke();
        this.canvas.restoreState();
    }

    protected float cm2millimeters(float cm) {
        return cm * 10;
    }

    protected void printBarcode(String code, int absoluteX, int absoluteY) throws DocumentPrinterException {
        Image image = Barcodes.code39(code, 33, 1);
        image.setAbsolutePosition(absoluteX, absoluteY);
        image.scaleAbsolute(164, 62);
        printImage(image);
    }

    /**
     * Prints a PDF417 barcode using the printable width. The content is encoded as ISO-8859-1 when possible (historical behavior, required by the SII electronic stamp) and as UTF-8 (with ECI) otherwise.
     */
    protected void printPdf417(String code) throws DocumentPrinterException {
        printPdf417(code, code == null ? null : Barcodes.defaultPdf417Charset(code));
    }

    /**
     * Prints a PDF417 barcode encoding the content with an explicit charset.
     *
     * @throws DocumentPrinterException
     *             if the content cannot be represented in {@code charset}
     */
    protected void printPdf417(String code, Charset charset) throws DocumentPrinterException {
        Image image = Barcodes.pdf417(code, charset);
        image.scaleAbsolute(getPageWidth() - getMarginLeft() - getMarginRight(), 108); // 1:3
        float imagePositionY = (getPositionY() - image.getScaledHeight());
        float imagePositionX = (getMarginLeft());
        image.setAbsolutePosition(imagePositionX, imagePositionY);
        printImage(image);
        setPositionY((int) (imagePositionY) - Space.NORMAL.size);
    }

    protected Image generateBarcode(String code) throws DocumentPrinterException {
        Image image = Barcodes.code39(code, 25, 1);
        image.scaleAbsolute(164, 62);
        return image;
    }

    public static float pixelsToPoints(float value, int dpi) {
        return value / dpi * 72;
    }

    protected void newLine() {
        setPositionY(getPositionY() - space.size);
    }

    protected void printImage(Image image) throws DocumentPrinterException {
        try {
            document.add(image);
        } catch (DocumentException ex) {
            throw new DocumentPrinterException("Unable to add image to document", ex);
        }
    }

    /**
     * Closes the document (only once) and returns the generated PDF.
     *
     * @return the PDF bytes
     */
    protected ByteArrayOutputStream close() {
        if (!closed) {
            closed = true;
            document.close();
        }
        return baos;
    }

    /**
     * Closes the document after a failure, ignoring secondary errors, so writer resources are released.
     */
    protected void closeQuietly() {
        try {
            close();
        } catch (RuntimeException ignored) {
            // the original failure is more relevant than a failure while closing
        }
    }
}
