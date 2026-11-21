/*-
 * !--
 * For support and inquiries regarding this library, please contact:
 *   soporte@kanopus.cl
 *
 * Project website:
 *   https://www.kanopus.cl
 * %%
 * Copyright (C) 2025 - 2026 Pablo Díaz Saavedra
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cl.kanopus.pdf.DocumentPrinterException;
import cl.kanopus.pdf.FontFamily;
import cl.kanopus.pdf.testutil.PdfTestUtils;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpdf.text.Image;
import org.openpdf.text.pdf.PdfContentByte;

class AbstractPrintNormalTest {

    private TestPrintNormal printer;

    static class TestPrintNormal extends AbstractPrintNormal {
        TestPrintNormal() throws DocumentPrinterException {
            super();
        }

        TestPrintNormal(int width, int height) throws DocumentPrinterException {
            super(null, width, height, 10);
        }

        PdfContentByte getCanvasPublic() {
            return super.getCanvas();
        }
    }

    @BeforeEach
    void setUp() throws DocumentPrinterException {
        printer = new TestPrintNormal();
        printer.setPositionY(500);
    }

    @Test
    void cm2millimeters_ShouldMultiplyBy10() {
        float result = printer.cm2millimeters(2.5f);
        assertEquals(25f, result, 0.0001f);
    }

    @Test
    void println_ShouldDecreasePositionY_BySpaceSize() {
        printer.setSpace(AbstractPrintNormal.Space.LOW);
        int before = printer.getPositionY();
        printer.println("Hello");
        assertEquals(before - AbstractPrintNormal.Space.LOW.getSize(), printer.getPositionY());
    }

    @Test
    void println_Null_shouldStillDecreasePositionY() {
        printer.setSpace(AbstractPrintNormal.Space.NORMAL);
        int before = printer.getPositionY();
        printer.println((String) null);
        assertEquals(before - AbstractPrintNormal.Space.NORMAL.getSize(), printer.getPositionY());
    }

    @Test
    void printAbsolute_DoesNotChangePositionY() {
        int before = printer.getPositionY();
        printer.printAbsolute("abs", 10f, 20f);
        assertEquals(before, printer.getPositionY());
    }

    @Test
    void printLine_DecreasesPositionY() {
        int before = printer.getPositionY();
        printer.printLine(10, 200, AbstractPrintNormal.Align.CENTER, false);
        assertEquals(before - printer.getSpace().getSize(), printer.getPositionY());
    }

    @Test
    void drawRectangle_shouldNotThrow() {
        assertDoesNotThrow(() -> printer.drawRectangle(10f, 10f, 20f, 5f, Color.BLACK, 1));
    }

    @Test
    void printImage_AddImage_shouldAdjustPositionY_WhenHeightUsed() throws Exception {
        // create a small buffered image and convert it to an OpenPDF Image
        BufferedImage bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 10, 10);
        g.dispose();

        Image image = Image.getInstance(bi, null);
        int before = printer.getPositionY();
        // printImage doesn't change positionY except in printPdf417; ensure method adds without
        // exception
        assertDoesNotThrow(() -> printer.printImage(image));
        assertEquals(before, printer.getPositionY());
    }

    @Test
    void defaultGettersMatchConstants() {
        assertEquals(AbstractPrintNormal.PAGE_WIDTH, printer.getPageWidth());
        assertEquals(AbstractPrintNormal.PAGE_HEIGHT, printer.getPageHeight());
        assertEquals(AbstractPrintNormal.MARGIN_LEFT, printer.getMarginLeft());
        assertEquals(AbstractPrintNormal.MARGIN_RIGHT, printer.getMarginRight());
    }

    @Test
    void customSizeAndMarginsAreReported() throws Exception {
        TestPrintNormal custom = new TestPrintNormal(300, 400);
        assertEquals(300, custom.getPageWidth());
        assertEquals(400, custom.getPageHeight());
        custom.setTextMargins(15, 25);
        assertEquals(15, custom.getMarginLeft());
        assertEquals(25, custom.getMarginRight());
        custom.setPositionY(380);
        custom.println("custom");
        custom.printLine();
        byte[] pdf = custom.close().toByteArray();
        assertTrue(PdfTestUtils.allText(pdf).contains("custom"));
    }

    @Test
    void code39BarcodeIsDrawn() throws Exception {
        printer.printBarcode("ABC123", 20, 300);
        printer.printBarcode("XYZ", 20, 200, AbstractPrintNormal.Scale.BIG);
        printer.printBarcode("XYZ", 20, 100, null);
        byte[] pdf = printer.close().toByteArray();
        PdfTestUtils.write(pdf, "normal-code39.pdf");
        assertEquals(3, PdfTestUtils.imageCount(pdf, 1));
        assertThrows(DocumentPrinterException.class, () -> new TestPrintNormal().printBarcode("ñ", 0, 0));
    }

    @Test
    void pdf417OnNormalPageUsesScaledHeight() throws Exception {
        printer.printPdf417("TED|ÑANDÚ|12345", 50, 400);
        assertEquals(400 - 72, printer.getPositionY());
        printer.printPdf417("TED|€ Ω", 50, 200, 200, 60);
        assertEquals(200 - 60, printer.getPositionY());
        byte[] pdf = printer.close().toByteArray();
        PdfTestUtils.write(pdf, "normal-pdf417.pdf");
        assertEquals(2, PdfTestUtils.imageCount(pdf, 1));
    }

    @Test
    void pdf417WithUnsupportedCharsetThrows() {
        DocumentPrinterException ex = assertThrows(DocumentPrinterException.class, () -> printer.printPdf417("Ω", 0, 0, 10, 10, StandardCharsets.ISO_8859_1));
        assertTrue(ex.getMessage().contains("PDF417"), ex.getMessage());
    }

    @Test
    void printOverloadsAndNullsAreHandled() throws Exception {
        int before = printer.getPositionY();
        printer.print("a");
        printer.print("b", 10);
        printer.print("c", 10, AbstractPrintNormal.Align.RIGHT);
        printer.print("d", 10, FontFamily.FONT_8_BLACK_BOLD);
        printer.print("e", 10, FontFamily.FONT_8_BLACK_BOLD, AbstractPrintNormal.Align.CENTER);
        printer.printAbsolute(null, 10, 10);
        printer.printAbsolute("f", 10, 10, AbstractPrintNormal.Align.CENTER);
        printer.printAbsolute("g", 10, 10, FontFamily.FONT_8_BLACK_BOLD);
        assertEquals(before, printer.getPositionY());

        printer.println("h", FontFamily.FONT_8_BLACK_BOLD);
        printer.println("i", 10);
        printer.println("j", 10, AbstractPrintNormal.Align.RIGHT);
        printer.println("k", 10, FontFamily.FONT_8_BLACK_BOLD, AbstractPrintNormal.Align.CENTER);
        assertEquals(before - 4 * printer.getSpace().getSize(), printer.getPositionY());

        printer.printLine(0, 100, null, true);
        printer.printLine(0, 100, 50);
        printer.drawRectangleFixed(0, 0, 10, 10, null, 1);
        printer.newLine();
        printer.printlnSplit(10, "one two three four five", 10);
        printer.printlnSplit(10, "one two", 10, 300, FontFamily.FONT_8_BLACK_NORMAL);
        printer.printlnSplit(10, null, 10);
        assertTrue(printer.getPositionY() < 300);
        printer.newPage();
        printer.println("second page");

        byte[] pdf = printer.close().toByteArray();
        assertEquals(2, PdfTestUtils.pagesText(pdf).size());
        // close is idempotent
        assertEquals(pdf.length, printer.close().size());
        printer.closeQuietly();
    }
}
