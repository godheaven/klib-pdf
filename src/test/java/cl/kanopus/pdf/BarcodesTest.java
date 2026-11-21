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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.pdf417.PDF417Reader;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.openpdf.text.Image;

class BarcodesTest {

    @Test
    void defaultCharsetIsLatin1WhenPossible() {
        assertEquals(StandardCharsets.ISO_8859_1, Barcodes.defaultPdf417Charset("ÑANDÚ ñ á"));
        assertEquals(StandardCharsets.UTF_8, Barcodes.defaultPdf417Charset("€ Ω"));
    }

    @Test
    void utf8Pdf417RoundTripsThroughDecoder() throws Exception {
        String content = "TED|€ Ω ñ|Привет";
        BitMatrix matrix = Barcodes.pdf417Matrix(content, StandardCharsets.UTF_8);
        assertEquals(content, decode(MatrixToImageWriter.toBufferedImage(matrix)));

        Image image = Barcodes.pdf417(content, StandardCharsets.UTF_8);
        assertNotNull(image);
        assertTrue(image.getWidth() > 0);
    }

    @Test
    void latin1Pdf417UsesBinaryEncoder() throws Exception {
        Image image = Barcodes.pdf417("ÑANDÚ|12345", StandardCharsets.ISO_8859_1);
        assertNotNull(image);
        assertTrue(image.getWidth() > 0 && image.getHeight() > 0);
    }

    @Test
    void invalidContentIsRejected() {
        DocumentPrinterException ex = assertThrows(DocumentPrinterException.class, () -> Barcodes.pdf417("Ω", StandardCharsets.ISO_8859_1));
        assertTrue(ex.getMessage().contains("PDF417"), ex.getMessage());
        assertThrows(DocumentPrinterException.class, () -> Barcodes.pdf417(null, StandardCharsets.UTF_8));

        DocumentPrinterException code39 = assertThrows(DocumentPrinterException.class, () -> Barcodes.code39("Ω", 100, 10));
        assertTrue(code39.getMessage().contains("Code39"), code39.getMessage());
        assertThrows(DocumentPrinterException.class, () -> Barcodes.code39("", 100, 10));
        assertThrows(DocumentPrinterException.class, () -> Barcodes.code39(null, 100, 10));
    }

    @Test
    void code39ImageIsGenerated() throws Exception {
        Image image = Barcodes.code39("ABC-123", 300, 100);
        assertTrue(image.getWidth() >= 300);
        assertTrue(image.getHeight() >= 100);
    }

    private static String decode(BufferedImage symbol) throws Exception {
        // add a quiet zone, decoders need it
        BufferedImage padded = new BufferedImage(symbol.getWidth() + 80, symbol.getHeight() + 80, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = padded.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, padded.getWidth(), padded.getHeight());
        g.drawImage(symbol, 40, 40, null);
        g.dispose();
        LuminanceSource source = new BufferedImageLuminanceSource(padded);
        return new PDF417Reader().decode(new BinaryBitmap(new HybridBinarizer(source))).getText();
    }
}
