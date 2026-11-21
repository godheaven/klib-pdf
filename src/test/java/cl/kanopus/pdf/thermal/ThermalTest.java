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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cl.kanopus.pdf.DocumentPrinterException;
import cl.kanopus.pdf.FontFamily;
import cl.kanopus.pdf.testutil.PdfTestUtils;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.openpdf.text.Image;

class ThermalTest {

    static class Receipt58mm extends AbstractPrint58mm {

        Receipt58mm() throws DocumentPrinterException {
            super(calcularHeight(3));
        }

        ByteArrayOutputStream generate() throws DocumentPrinterException {
            drawRectangle(5, getPositionY() + Space.LOW.size, 130, 25, null, 1);
            println("RECEIPT 58", Align.CENTER);
            println("Señor Muñoz €", Align.RIGHT);
            printLine();
            for (int i = 0; i < 3; i++) {
                print("ITEM" + i);
                println(String.valueOf(i * 10), Align.RIGHT);
            }
            printBarcode("ABC-123", 0, 40);
            printPdf417("TED|58MM|ÑANDÚ");
            println("END", (Align) null);
            return close();
        }
    }

    @Test
    void receipt80mmIsGenerated() throws Exception {
        Receipt80mmPDF pdf = new Receipt80mmPDF();
        byte[] bytes = pdf.generateOutput().toByteArray();
        PdfTestUtils.write(bytes, "receipt-80mm.pdf");

        List<String> pages = PdfTestUtils.pagesText(bytes);
        assertEquals(1, pages.size());
        assertTrue(pages.get(0).contains("ELECTRONIC RECEIPT"), pages.get(0));
        assertTrue(pages.get(0).contains("Electronic Stamp S.I.I"), pages.get(0));
        assertEquals(1, PdfTestUtils.imageCount(bytes, 1), "PDF417 stamp image");
        assertEquals(205, pdf.getPageWidth());
        assertEquals(3, pdf.getMarginLeft());
        assertEquals(3, pdf.getMarginRight());
    }

    @Test
    void receipt58mmIsGenerated() throws Exception {
        Receipt58mm receipt = new Receipt58mm();
        assertEquals(140, receipt.getPageWidth());
        assertEquals(AbstractPrint58mm.calcularHeight(3), receipt.getPageHeight());

        byte[] bytes = receipt.generate().toByteArray();
        PdfTestUtils.write(bytes, "receipt-58mm.pdf");

        String text = PdfTestUtils.allText(bytes);
        assertTrue(text.contains("RECEIPT 58"), text);
        assertTrue(text.contains("Señor Muñoz €"), text);
        assertTrue(text.contains("ITEM2"), text);
        assertTrue(text.contains("END"), text);
        assertEquals(2, PdfTestUtils.imageCount(bytes, 1), "Code39 + PDF417 images");
    }

    @Test
    void nullTextAndNullAlignAdvanceLikeNormalPrinter() throws Exception {
        Receipt58mm receipt = new Receipt58mm();
        receipt.setPositionY(200);
        receipt.setSpace(AbstractThermal.Space.NORMAL);

        receipt.println((String) null);
        assertEquals(185, receipt.getPositionY());
        receipt.println("x", (AbstractThermal.Align) null);
        assertEquals(170, receipt.getPositionY());
        receipt.print("x", (AbstractThermal.Align) null);
        receipt.print("y", AbstractThermal.Align.CENTER);
        receipt.print("z", 10);
        receipt.print("w", 10, AbstractThermal.Align.RIGHT);
        receipt.println("v", 10);
        receipt.println("u", 10, AbstractThermal.Align.CENTER);
        assertEquals(140, receipt.getPositionY());
        receipt.printLine(0, 100, null, true);
        assertEquals(125, receipt.getPositionY());
        receipt.newLine();
        assertEquals(110, receipt.getPositionY());

        receipt.setFont(null);
        receipt.printSplit("Label: ", "some long text that is split", 10);
        receipt.printSplit("", 10);
        assertTrue(receipt.getPositionY() < 110);
        receipt.close();
    }

    @Test
    void generateBarcodeReturnsScaledImage() throws Exception {
        Receipt58mm receipt = new Receipt58mm();
        Image image = receipt.generateBarcode("CODE39");
        assertNotNull(image);
        assertEquals(164f, image.getScaledWidth(), 0.01f);
        assertEquals(62f, image.getScaledHeight(), 0.01f);
        assertEquals(36f, AbstractThermal.pixelsToPoints(100, 200), 0.001f);
        assertEquals(20f, receipt.cm2millimeters(2), 0.001f);
        receipt.printImage(image);
        receipt.close();
    }

    @Test
    void pdf417PositionUsesScaledHeight() throws Exception {
        Receipt58mm receipt = new Receipt58mm();
        receipt.setPositionY(300);
        receipt.printPdf417("TED", StandardCharsets.ISO_8859_1);
        assertEquals(300 - 108 - AbstractThermal.Space.NORMAL.size, receipt.getPositionY());
        receipt.close();
    }

    @Test
    void invalidBarcodeContentThrows() throws Exception {
        Receipt58mm receipt = new Receipt58mm();
        assertThrows(DocumentPrinterException.class, () -> receipt.printBarcode("ñ", 0, 0));
        assertThrows(DocumentPrinterException.class, () -> receipt.printPdf417("Ω", StandardCharsets.ISO_8859_1));
        assertThrows(DocumentPrinterException.class, () -> receipt.printPdf417(null));
        receipt.drawRectangle(0, 10, 10, 5, Color.RED, 1);
        receipt.setFont(FontFamily.FONT_9_BLACK_ITALIC);
        receipt.println("ok");
        receipt.close();
        // closing twice is harmless
        receipt.close();
        receipt.closeQuietly();
    }
}
