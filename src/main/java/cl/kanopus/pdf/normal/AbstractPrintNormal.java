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
import cl.kanopus.pdf.DocumentPrinterException;
import cl.kanopus.pdf.FontFamily;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code39Writer;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;


public abstract class AbstractPrintNormal {

    private Document document = null;
    private PdfContentByte canvas = null;
    private ByteArrayOutputStream baos = null;

    public static final int PAGE_WIDTH = 595; //21cm
    public static final int PAGE_HEIGHT = 935;  //33cm
    public static final int MARGIN_LEFT = 3;
    public static final int MARGIN_RIGHT = 3;
    private int positionY = 0;

    private static final Font DEFAULT_FONT = FontFamily.FONT_10_BLACK_NORMAL;
    private Space space = Space.NORMAL;

    public enum Scale {

        NORMAL(164, 51),//62
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
        try {
            this.document = new Document(new Rectangle(pageWidth, pageHeight));
            this.document.setMargins(margin, margin, margin, (margin == 0) ? 0 : 5);
            this.document.setMarginMirroringTopBottom(false);

            this.baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(this.document, baos);
            if (orientation != null) {
                Rotate event = new Rotate();
                event.setOrientation(orientation);
                writer.setPageEvent(event);
            }
            this.document.open();
            this.canvas = writer.getDirectContent();
        } catch (DocumentException ex) {
            throw new DocumentPrinterException("Unable to initialize document", ex);
        }
    }

    protected AbstractPrintNormal() throws DocumentPrinterException {
        this(null, PAGE_WIDTH, PAGE_HEIGHT, 20);
    }

    public class Rotate extends PdfPageEventHelper {

        protected PdfNumber orientation = PdfPage.PORTRAIT;

        public void setOrientation(PdfNumber orientation) {
            this.orientation = orientation;
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            writer.addPageDictEntry(PdfName.ROTATE, orientation);
        }
    }

    protected PdfContentByte getCanvas() {
        return canvas;
    }

    public int getPageHeight() {
        return PAGE_HEIGHT;
    }

    public int getPageWidth() {
        return PAGE_WIDTH;
    }

    public int getMarginLeft() {
        return MARGIN_LEFT;
    }

    public int getMarginRight() {
        return MARGIN_RIGHT;
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
        printText(text, MARGIN_LEFT, true, null, Align.LEFT);
    }

    protected void println(String text, Font font) {
        printText(text, MARGIN_LEFT, true, font, Align.LEFT);
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
        printText(text, MARGIN_LEFT, false, null, Align.LEFT);
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
            Phrase phrase = new Phrase(text.trim(), (font == null) ? DEFAULT_FONT : font);
            ColumnText.showTextAligned(this.canvas, (align == null) ? Align.LEFT.id : align.id, phrase, positionX, positionY, 0);
        }
        positionY = automaticNewLine ? positionY - space.size : positionY;
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
        Phrase phrase = new Phrase(text, (font == null) ? DEFAULT_FONT : font);
        ColumnText.showTextAligned(this.canvas, (align == null) ? Align.LEFT.id : align.id, phrase, x, y, 0);
    }

    protected void printLine() {
        printLine(MARGIN_LEFT, PAGE_WIDTH - MARGIN_RIGHT, Align.CENTER, false);
    }

    protected void printLine(int leftX, int rigthX, Align align, boolean bold) {
        LineSeparator lineSeparator = new LineSeparator(bold ? 1 : 0, 100, BaseColor.BLACK, align.id, 0);
        lineSeparator.drawLine(this.canvas, leftX, rigthX, positionY);
        positionY = positionY - space.size;
    }

    protected void printLine(int leftX, int rigthX, int y, boolean bold) {
        LineSeparator lineSeparator = new LineSeparator(bold ? 1 : 0, 100, BaseColor.BLACK, Element.ALIGN_LEFT, 0);
        lineSeparator.drawLine(this.canvas, leftX, rigthX, y);
    }

    protected void printLine(int leftX, int rigthX, int y) {
        printLine(leftX, rigthX, y, false);
    }

    protected void drawRectangleFixed(float x, float y, float width, float height, BaseColor color, int lineWidth) {
        this.canvas.saveState();
        PdfGState state = new PdfGState();
        state.setFillOpacity(0);
        this.canvas.setGState(state);
        this.canvas.setRGBColorStroke(color.getRed(), color.getGreen(), color.getBlue());
        this.canvas.setLineWidth(lineWidth);
        this.canvas.rectangle(x, y, width, height);
        this.canvas.fillStroke();
        this.canvas.restoreState();
    }

    protected void drawRectangle(float x, float y, float width, float height, BaseColor color, int lineWidth) {
        this.canvas.saveState();
        PdfGState state = new PdfGState();
        state.setFillOpacity(0);
        this.canvas.setGState(state);
        this.canvas.setRGBColorStroke(color.getRed(), color.getGreen(), color.getBlue());
        this.canvas.setLineWidth(lineWidth);
        this.canvas.rectangle(x, y - height, width, height);
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

        try {
            // --- Parámetros visuales ---
            int widthPx = Math.max(scale.getWidth(), 300);
            int heightPx = Math.max(scale.getHeight(), 100);

            // --- Configuración de ZXing ---
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 0); // Sin margen blanco extra

            // --- Generar código de barras (Code 39) ---
            BitMatrix bitMatrix = new Code39Writer().encode(code, BarcodeFormat.CODE_39, widthPx, heightPx, hints);

            BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // --- Convertir BufferedImage -> Image (iText) ---
            Image image = bufferedImageToITextImage(barcodeImage);
            image.setAbsolutePosition(absoluteX, absoluteY);
            image.scaleAbsolute(scale.getWidth(), scale.getHeight());

            document.add(image);

        } catch (IOException | DocumentException e) {
            throw new DocumentPrinterException("It is not possible to generate the barcode: " + code, e);
        }
    }

    private Image bufferedImageToITextImage(BufferedImage bufferedImage) throws IOException, DocumentException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", output);
            return Image.getInstance(output.toByteArray());
        }
    }

    protected void printPdf417(String code, int absoluteX, int absoluteY) throws DocumentPrinterException {
        printPdf417(code, absoluteX, absoluteY, 184, 72);
    }

    protected void printPdf417(String code, int absoluteX, int absoluteY, float width, float height) throws DocumentPrinterException {
        try {
            BarcodePDF417 pdf417 = new BarcodePDF417();
            pdf417.setCodeRows(5);
            pdf417.setCodeColumns(18);
            pdf417.setErrorLevel(5);
            pdf417.setLenCodewords(999);
            pdf417.setOptions(BarcodePDF417.PDF417_FORCE_BINARY);
            pdf417.setText(code.getBytes(StandardCharsets.ISO_8859_1));

            Image image = pdf417.getImage();
            image.scaleAbsolute(width, height);
            image.setAbsolutePosition(absoluteX, absoluteY);
            printImage(image);
            setPositionY((int) (absoluteY) - (int) image.getHeight());
        } catch (BadElementException ex) {
            throw new DocumentPrinterException("It is not possible to generate the barcode39: " + code, ex);
        }
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

    protected ByteArrayOutputStream close() {
        document.close();
        return baos;
    }

    private Image bufferedImage2Image(BufferedImage bufferedImage) throws DocumentPrinterException {
        try {
            ByteArrayOutputStream baosImage = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baosImage);
            return Image.getInstance(baosImage.toByteArray());
        } catch (BadElementException | IOException ex) {
            throw new DocumentPrinterException("It is not possible to insert the image into the document", ex);
        }

    }

}
