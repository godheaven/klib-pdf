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
package cl.kanopus.pdf;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code39Writer;
import com.google.zxing.pdf417.PDF417Writer;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Image;
import org.openpdf.text.pdf.BarcodePDF417;

/**
 * Barcode rendering shared by the normal and thermal printers.
 */
public final class Barcodes {

    /** Width/height (px) used for the ZXing PDF417 bitmap before it is scaled into the PDF. */
    private static final int PDF417_RASTER_WIDTH = 540;
    private static final int PDF417_RASTER_HEIGHT = 180;
    private static final int PDF417_ERROR_LEVEL = 5;

    private Barcodes() {
    }

    /**
     * Generates a Code 39 barcode image.
     *
     * @param code
     *            the content (Code 39 / extended Code 39 characters)
     * @param widthPx
     *            minimum raster width
     * @param heightPx
     *            minimum raster height
     * @return the barcode as a PDF image (not yet scaled or positioned)
     * @throws DocumentPrinterException
     *             if the content cannot be encoded
     */
    public static Image code39(String code, int widthPx, int heightPx) throws DocumentPrinterException {
        if (code == null || code.isEmpty()) {
            throw new DocumentPrinterException("It is not possible to generate the Code39 barcode: empty content");
        }
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 0); // No extra white margin
            BitMatrix bitMatrix = new Code39Writer().encode(code, BarcodeFormat.CODE_39, widthPx, heightPx, hints);
            return toImage(MatrixToImageWriter.toBufferedImage(bitMatrix));
        } catch (IllegalArgumentException | IOException | DocumentException e) {
            throw new DocumentPrinterException("It is not possible to generate the Code39 barcode: " + code, e);
        }
    }

    /**
     * Returns the charset used by default for PDF417 content: ISO-8859-1 (required by the Chilean SII electronic stamp and the historical behavior) when the whole text can be represented in it, otherwise UTF-8.
     *
     * @param code
     *            the content
     * @return the charset to use
     */
    public static Charset defaultPdf417Charset(String code) {
        return StandardCharsets.ISO_8859_1.newEncoder().canEncode(code) ? StandardCharsets.ISO_8859_1 : StandardCharsets.UTF_8;
    }

    /**
     * Generates a PDF417 barcode image.
     * <p>
     * ISO-8859-1 content is encoded in binary mode with the historical parameters (5 rows, 18 columns, error level 5). Any other charset is encoded with ZXing and an ECI designator, so readers know how to decode the bytes.
     *
     * @param code
     *            the content
     * @param charset
     *            the charset; the content must be fully representable in it
     * @return the barcode as a PDF image (not yet scaled or positioned)
     * @throws DocumentPrinterException
     *             if the content is null or cannot be encoded in {@code charset}
     */
    public static Image pdf417(String code, Charset charset) throws DocumentPrinterException {
        if (code == null) {
            throw new DocumentPrinterException("It is not possible to generate the PDF417 barcode: null content");
        }
        if (!charset.newEncoder().canEncode(code)) {
            throw new DocumentPrinterException("It is not possible to generate the PDF417 barcode: content cannot be encoded as " + charset.name());
        }
        try {
            if (StandardCharsets.ISO_8859_1.equals(charset)) {
                BarcodePDF417 pdf417 = new BarcodePDF417();
                pdf417.setCodeRows(5);
                pdf417.setCodeColumns(18);
                pdf417.setErrorLevel(PDF417_ERROR_LEVEL);
                pdf417.setLenCodewords(999);
                pdf417.setOptions(BarcodePDF417.PDF417_FORCE_BINARY);
                pdf417.setText(code.getBytes(charset));
                return pdf417.getImage();
            }
            return toImage(MatrixToImageWriter.toBufferedImage(pdf417Matrix(code, charset)));
        } catch (WriterException | IllegalArgumentException | IOException | DocumentException e) {
            throw new DocumentPrinterException("It is not possible to generate the PDF417 barcode: " + code, e);
        }
    }

    /**
     * Encodes a PDF417 symbol with ZXing using an explicit character set (ECI).
     */
    static BitMatrix pdf417Matrix(String code, Charset charset) throws WriterException {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.ERROR_CORRECTION, PDF417_ERROR_LEVEL);
        hints.put(EncodeHintType.CHARACTER_SET, charset.name());
        return new PDF417Writer().encode(code, BarcodeFormat.PDF_417, PDF417_RASTER_WIDTH, PDF417_RASTER_HEIGHT, hints);
    }

    private static Image toImage(BufferedImage bufferedImage) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", output);
            return Image.getInstance(output.toByteArray());
        }
    }
}
