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


abstract class AbstractThermal {

    private Document document = null;
    private PdfContentByte canvas = null;
    private ByteArrayOutputStream baos = null;

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

    public void setFont(Font font) {
        this.font = font;
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
        if (align == Align.LEFT) {
            printText(text, marginLeft, true, align);
        } else if (align == Align.CENTER) {
            printText(text, getPageWidth() / 2, true, align);
        } else if (align == Align.RIGHT) {
            printText(text, getPageWidth() - getMarginRight(), true, align);
        }

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
        if (align == Align.LEFT) {
            printText(text, marginLeft, false, align);
        } else if (align == Align.CENTER) {
            printText(text, getPageWidth() / 2, false, align);
        } else if (align == Align.RIGHT) {
            printText(text, getPageWidth() - getMarginRight(), false, align);
        }

    }

    protected void print(String text, int positionX, Align align) {
        printText(text, positionX, false, align);
    }

    private void printText(String text, int positionX, boolean automaticNewLine, Align align) {
        if (text != null) {
            Phrase phrase = new Phrase(text, font);
            ColumnText.showTextAligned(this.canvas, align.position, phrase, positionX, positionY, 0);
            positionY = automaticNewLine ? positionY - space.size : positionY;
        }

    }

    protected void printLine() {
        printLine(marginLeft, pageWidth - marginRight, Align.CENTER, false);
    }

    protected void printLine(int leftX, int rigthX, Align align, boolean bold) {
        LineSeparator lineSeparator = new LineSeparator(bold ? 1 : 0, 100, BaseColor.BLACK, align.position, 0);
        lineSeparator.drawLine(this.canvas, leftX, rigthX, positionY);
        positionY = positionY - space.size;
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

        try {
            // --- Parámetros visuales ---
            int widthPx = 33;
            int heightPx = 1;

            // --- Configuración de ZXing ---
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 0); // Sin margen blanco extra

            // --- Generar código de barras (Code 39) ---
            BitMatrix bitMatrix = new Code39Writer().encode(code, BarcodeFormat.CODE_39, widthPx, heightPx, hints);

            BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // --- Convertir BufferedImage -> Image (iText) ---
            Image image = bufferedImageToITextImage(barcodeImage);
            image.setAbsolutePosition(absoluteX, absoluteY);
            image.scaleAbsolute(164, 62);

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

    protected void printPdf417(String code) throws DocumentPrinterException {
        try {
            BarcodePDF417 pdf417 = new BarcodePDF417();
            pdf417.setCodeRows(5);
            pdf417.setCodeColumns(18);
            pdf417.setErrorLevel(5);
            pdf417.setLenCodewords(999);
            pdf417.setOptions(BarcodePDF417.PDF417_FORCE_BINARY);
            pdf417.setText(code.getBytes(StandardCharsets.ISO_8859_1));

            Image image = pdf417.getImage();
            image.scaleAbsolute(getPageWidth() - getMarginLeft() - getMarginRight(), 108); //1:3
            float imagePositionY = (getPositionY() - image.getScaledHeight());
            float imagePositionX = (getMarginLeft());
            image.setAbsolutePosition(imagePositionX, imagePositionY);
            printImage(image);
            setPositionY((int) (imagePositionY) - Space.NORMAL.size);
        } catch (BadElementException ex) {
            throw new DocumentPrinterException("It is not possible to generate the barcode39: " + code, ex);
        }
    }

    protected Image generateBarcode(String code) throws DocumentPrinterException {

        try {
            // --- Parámetros visuales ---
            int widthPx = 25;
            int heightPx = 1;

            // --- Configuración de ZXing ---
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 0); // Sin margen blanco extra

            // --- Generar código de barras (Code 39) ---
            BitMatrix bitMatrix = new Code39Writer().encode(code, BarcodeFormat.CODE_39, widthPx, heightPx, hints);

            BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // --- Convertir BufferedImage -> Image (iText) ---
            Image image = bufferedImageToITextImage(barcodeImage);
            image.scaleAbsolute(164, 62);

            return image;

        } catch (IOException | DocumentException e) {
            throw new DocumentPrinterException("It is not possible to generate the barcode: " + code, e);
        }

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

    protected ByteArrayOutputStream close() {
        document.close();
        return baos;
    }
}
