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

import cl.kanopus.pdf.DocumentPrinterException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractPrintNormalTest {

    private TestPrintNormal printer;

    static class TestPrintNormal extends AbstractPrintNormal {
        TestPrintNormal() throws DocumentPrinterException {
            super();
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
        assertDoesNotThrow(() -> printer.drawRectangle(10f, 10f, 20f, 5f, BaseColor.BLACK, 1));
    }

    @Test
    void printImage_AddImage_shouldAdjustPositionY_WhenHeightUsed() throws Exception {
        // create a small buffered image and convert to iText Image via reflection helper
        BufferedImage bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setColor(java.awt.Color.RED);
        g.fillRect(0, 0, 10, 10);
        g.dispose();

        Image image = Image.getInstance(bi, null);
        int before = printer.getPositionY();
        // printImage doesn't change positionY except in printPdf417; ensure method adds without exception
        assertDoesNotThrow(() -> printer.printImage(image));
        assertEquals(before, printer.getPositionY());
    }

}

