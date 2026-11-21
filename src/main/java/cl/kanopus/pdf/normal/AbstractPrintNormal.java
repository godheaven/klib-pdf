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
package cl.kanopus.pdf.normal;

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
import org.openpdf.text.pdf.PdfNumber;
import org.openpdf.text.pdf.PdfPage;
import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.pdf.PdfWriter;
import org.openpdf.text.pdf.draw.LineSeparator;

public abstract class AbstractPrintNormal {

    private Document document = null;
    private PdfContentByte canvas = null;
    private ByteArrayOutputStream baos = null;
    private boolean closed = false;

    public static final int PAGE_WIDTH = 595; // 21cm
    public static final int PAGE_HEIGHT = 935; // 33cm
    public static final int MARGIN_LEFT = 3;
    public static final int MARGIN_RIGHT = 3;

    private final int pageWidth;
    private final int pageHeight;
    private int marginLeft = MARGIN_LEFT;
    private int marginRight = MARGIN_RIGHT;
    private int positionY = 0;

    private static final Font DEFAULT_FONT = FontFamily.FONT_10_BLACK_NORMAL;
    private Space space = Space.NORMAL;

    public enum Scale {
        NORMAL(164, 51), // 62
        BIG(330, 72);
        final int width;
        final int height;

        Scale(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    public enum Space {
        LOW(10),
        NORMAL(15),
        HIGHT(20);
        final int size;

        Space(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    public enum Align {
        LEFT(Element.ALIGN_LEFT),
        CENTER(Element.ALIGN_CENTER),
        RIGHT(Element.ALIGN_RIGHT);

        final int id;

        Align(int id) {
            this.id = id;
        }
    }

    protected AbstractPrintNormal(PdfNumber orientation, int pageWidth, int pageHeight, int margin) throws DocumentPrinterException {
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        try {
            this.document = new Document(pageSize(orientation, pageWidth, pageHeight));
            this.document.setMargins(margin, margin, margin, (margin == 0) ? 0 : 5);
            this.document.setMarginMirroringTopBottom(false);

            this.baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(this.document, baos);
            this.document.open();
            this.canvas = writer.getDirectContent();
        } catch (DocumentException ex) {
            throw new DocumentPrinterException("Unable to initialize document", ex);
        }
    }

    protected AbstractPrintNormal() throws DocumentPrinterException {
        this(null, PAGE_WIDTH, PAGE_HEIGHT, 20);
    }

    /**
     * Builds the page size so that the MediaBox is {@code pageWidth x pageHeight} and the page dictionary carries {@code /Rotate orientation} (same output as the former iText page event).
     */
    private static Rectangle pageSize(PdfNumber orientation, int pageWidth, int pageHeight) {
        int rotation = (orientation == null) ? 0 : Math.floorMod(orientation.intValue(), 360);
        if (rotation == 90 || rotation == 270) {
            // OpenPDF swaps the coordinates of rotated pages when writing the MediaBox
            return new Rectangle(pageHeight, pageWidth, rotation);
        }
        return new Rectangle(pageWidth, pageHeight, rotation);
    }

    /**
     * Former page event used to rotate pages.
     *
     * @deprecated OpenPDF has no page dictionary hook; the rotation given to the constructor is now applied through the page size. This event does nothing and will be removed.
     */
    @Deprecated(since = "4.20.0", forRemoval = true)
    public class Rotate extends PdfPageEventHelper {

        protected PdfNumber orientation = PdfPage.PORTRAIT;

        public void setOrientation(PdfNumber orientation) {
            this.orientation = orientation;
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            // no-op: see AbstractPrintNormal(PdfNumber, int, int, int)
        }
    }

    protected PdfContentByte getCanvas() {
        return canvas;
    }

    /**
     * @return the page height given to the constructor ({@link #PAGE_HEIGHT} by default)
     */
    public int getPageHeight() {
        return pageHeight;
    }

    /**
     * @return the page width given to the constructor ({@link #PAGE_WIDTH} by default)
     */
    public int getPageWidth() {
        return pageWidth;
    }

    /**
     * @return the left x coordinate used by {@code print/println} without explicit position ({@link #MARGIN_LEFT} by default)
     */
    public int getMarginLeft() {
        return marginLeft;
    }

    /**
     * @return the right margin used by {@link #printLine()} ({@link #MARGIN_RIGHT} by default)
     */
    public int getMarginRight() {
        return marginRight;
    }

    /**
     * Changes the horizontal text margins used by {@code print/println} without explicit position and by {@link #printLine()}.
     *
     * @param marginLeft
     *            left margin in points
     * @param marginRight
     *            right margin in points
     */
    protected void setTextMargins(int marginLeft, int marginRight) {
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
    }

    public int getPositionY() {
        return positionY;
    }

    protected void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Space getSpace() {
        return space;
    }

    protected void println(String text) {
        printText(text, marginLeft, true, null, Align.LEFT);
    }

    protected void println(String text, Font font) {
        printText(text, marginLeft, true, font, Align.LEFT);
    }

    protected void println(String text, int positionX) {
        printText(text, positionX, true, null, Align.LEFT);
    }

    protected void println(String text, int positionX, Font font) {
        printText(text, positionX, true, font, Align.LEFT);
    }

    protected void println(String text, int positionX, Align align) {
        printText(text, positionX, true, null, align);
    }

    protected void println(String text, int positionX, Font font, Align align) {
        printText(text, positionX, true, font, align);
    }

    protected void print(String text) {
        printText(text, marginLeft, false, null, Align.LEFT);
    }

    protected void print(String text, int positionX) {
        printText(text, positionX, false, null, Align.LEFT);
    }

    protected void print(String text, int positionX, Align align) {
        printText(text, positionX, false, null, align);
    }

    protected void print(String text, int positionX, Font font) {
        printText(text, positionX, false, font, null);
    }

    protected void print(String text, int positionX, Font font, Align align) {
        printText(text, positionX, false, font, align);
    }

    private void printText(String text, int positionX, boolean automaticNewLine, Font font, Align align) {
        if (text != null) {
            showText(text.trim(), positionX, positionY, font, align);
        }
        positionY = automaticNewLine ? positionY - space.size : positionY;
    }

    private void showText(String text, float x, float y, Font font, Align align) {
        Phrase phrase = new Phrase(text, (font == null) ? DEFAULT_FONT : font);
        ColumnText.showTextAligned(this.canvas, (align == null) ? Align.LEFT.id : align.id, phrase, x, y, 0);
    }

    protected void printlnSplit(int maxLineSize, String text, int positionX) {
        printlnSplit(maxLineSize, text, positionX, DEFAULT_FONT);
    }

    protected void printlnSplit(int maxLineSize, String text, int positionX, int positionY, Font font) {
        setPositionY(positionY);
        printlnSplit(maxLineSize, text, positionX, font);
    }

    protected void printlnSplit(int maxLineSize, String text, int positionX, Font font) {
        if (!Utils.isNullOrEmpty(text)) {
            List<String> lines = Utils.splitText(text, maxLineSize);
            for (String l : lines) {
                println(l, positionX, font);
            }
        }
    }

    protected void printAbsolute(String text, float x, float y) {
        printAbsolute(text, x, y, null, null);
    }

    protected void printAbsolute(String text, float x, float y, Align align) {
        printAbsolute(text, x, y, null, align);
    }

    protected void printAbsolute(String text, float x, float y, Font font) {
        printAbsolute(text, x, y, font, null);
    }

    protected void printAbsolute(String text, float x, float y, Font font, Align align) {
        if (text != null) {
            showText(text, x, y, font, align);
        }
    }

    protected void printLine() {
        printLine(marginLeft, pageWidth - marginRight, Align.CENTER, false);
    }

    protected void printLine(int leftX, int rigthX, Align align, boolean bold) {
        int alignment = (align == null) ? Element.ALIGN_CENTER : align.id;
        LineSeparator lineSeparator = new LineSeparator(bold ? 1 : 0, 100, Color.BLACK, alignment, 0);
        lineSeparator.drawLine(this.canvas, leftX, rigthX, positionY);
        positionY = positionY - space.size;
    }

    protected void printLine(int leftX, int rigthX, int y, boolean bold) {
        LineSeparator lineSeparator = new LineSeparator(bold ? 1 : 0, 100, Color.BLACK, Element.ALIGN_LEFT, 0);
        lineSeparator.drawLine(this.canvas, leftX, rigthX, y);
    }

    protected void printLine(int leftX, int rigthX, int y) {
        printLine(leftX, rigthX, y, false);
    }

    protected void drawRectangleFixed(float x, float y, float width, float height, Color color, int lineWidth) {
        strokeRectangle(x, y, width, height, color, lineWidth);
    }

    protected void drawRectangle(float x, float y, float width, float height, Color color, int lineWidth) {
        strokeRectangle(x, y - height, width, height, color, lineWidth);
    }

    private void strokeRectangle(float x, float y, float width, float height, Color color, int lineWidth) {
        Color stroke = (color == null) ? Color.BLACK : color;
        this.canvas.saveState();
        PdfGState state = new PdfGState();
        state.setFillOpacity(0);
        this.canvas.setGState(state);
        this.canvas.setRGBColorStroke(stroke.getRed(), stroke.getGreen(), stroke.getBlue());
        this.canvas.setLineWidth(lineWidth);
        this.canvas.rectangle(x, y, width, height);
        this.canvas.fillStroke();
        this.canvas.restoreState();
    }

    protected float cm2millimeters(float cm) {
        return cm * 10;
    }

    protected void printBarcode(String code, int absoluteX, int absoluteY) throws DocumentPrinterException {
        printBarcode(code, absoluteX, absoluteY, Scale.NORMAL);
    }

    protected void printBarcode(String code, int absoluteX, int absoluteY, Scale scale) throws DocumentPrinterException {
        Scale s = (scale == null) ? Scale.NORMAL : scale;
        // Visual parameters: minimum raster size, the image is then scaled to the requested size
        Image image = Barcodes.code39(code, Math.max(s.getWidth(), 300), Math.max(s.getHeight(), 100));
        image.setAbsolutePosition(absoluteX, absoluteY);
        image.scaleAbsolute(s.getWidth(), s.getHeight());
        printImage(image);
    }

    protected void printPdf417(String code, int absoluteX, int absoluteY) throws DocumentPrinterException {
        printPdf417(code, absoluteX, absoluteY, 184, 72);
    }

    /**
     * Prints a PDF417 barcode. The content is encoded as ISO-8859-1 when possible (historical behavior, required by the SII electronic stamp) and as UTF-8 (with ECI) otherwise.
     */
    protected void printPdf417(String code, int absoluteX, int absoluteY, float width, float height) throws DocumentPrinterException {
        printPdf417(code, absoluteX, absoluteY, width, height, code == null ? null : Barcodes.defaultPdf417Charset(code));
    }

    /**
     * Prints a PDF417 barcode encoding the content with an explicit charset. After printing, the Y position is placed just below the barcode.
     *
     * @throws DocumentPrinterException
     *             if the content cannot be represented in {@code charset}
     */
    protected void printPdf417(String code, int absoluteX, int absoluteY, float width, float height, Charset charset) throws DocumentPrinterException {
        Image image = Barcodes.pdf417(code, charset);
        image.scaleAbsolute(width, height);
        image.setAbsolutePosition(absoluteX, absoluteY);
        printImage(image);
        setPositionY(absoluteY - (int) image.getScaledHeight());
    }

    protected void newLine() {
        setPositionY(getPositionY() - getSpace().getSize());
    }

    protected void printImage(Image image) throws DocumentPrinterException {
        try {
            document.add(image);
        } catch (DocumentException ex) {
            throw new DocumentPrinterException("Unable to add image to document", ex);
        }
    }

    protected void newPage() {
        document.newPage();
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
