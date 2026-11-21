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
package cl.kanopus.pdf.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cl.kanopus.pdf.DocumentPrinterException;
import cl.kanopus.pdf.FontFamily;
import cl.kanopus.pdf.normal.AbstractPrintNormal;
import cl.kanopus.pdf.testutil.PdfTestUtils;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.openpdf.text.pdf.PdfName;
import org.openpdf.text.pdf.PdfNumber;
import org.openpdf.text.pdf.PdfPage;
import org.openpdf.text.pdf.PdfReader;

/**
 * End-to-end tests of {@link AbstractPrintNormal}: the generated PDF is parsed back and its text is verified.
 */
class PDFIntegrationTest {

    static class SimpleDocument extends AbstractPrintNormal {

        SimpleDocument() throws DocumentPrinterException {
            super();
            setPositionY(getPageHeight() - 40);
        }

        SimpleDocument(PdfNumber orientation, int width, int height) throws DocumentPrinterException {
            super(orientation, width, height, 10);
            setPositionY(getPageHeight() - 40);
        }

        byte[] render(String... lines) {
            for (String line : lines) {
                println(line, FontFamily.FONT_12_BLACK_NORMAL);
            }
            return close().toByteArray();
        }
    }

    @Test
    void generatedTextCanBeExtracted() throws Exception {
        byte[] pdf = new SimpleDocument().render("Unique test text 12345", "Second line");
        PdfTestUtils.write(pdf, "integration-simple.pdf");

        List<String> pages = PdfTestUtils.pagesText(pdf);
        assertEquals(1, pages.size());
        assertTrue(pages.get(0).contains("Unique test text 12345"), pages.get(0));
        assertTrue(pages.get(0).contains("Second line"), pages.get(0));
    }

    @Test
    void nonLatinTextIsEmbeddedAndExtractedBack() throws Exception {
        String latin = "ñ, á, €, Ω";
        String cyrillic = "Привет мир";
        String greek = "Καλημέρα";
        byte[] pdf = new SimpleDocument().render(latin, cyrillic, greek);
        PdfTestUtils.write(pdf, "integration-unicode.pdf");

        String text = PdfTestUtils.allText(pdf);
        assertTrue(text.contains(latin), text);
        assertTrue(text.contains(cyrillic), text);
        assertTrue(text.contains(greek), text);

        // the bundled TrueType font is embedded (subset) with Identity-H encoding
        String raw = new String(pdf, StandardCharsets.ISO_8859_1);
        assertTrue(raw.contains("/FontFile2"), "font must be embedded");
        assertTrue(raw.contains("/Identity-H"), "font must use Identity-H");
        assertTrue(raw.contains("LiberationSans"), "bundled font must be used");
    }

    @Test
    void customPageSizeAndRotationAreApplied() throws Exception {
        SimpleDocument doc = new SimpleDocument(PdfPage.LANDSCAPE, 400, 300);
        assertEquals(400, doc.getPageWidth());
        assertEquals(300, doc.getPageHeight());
        byte[] pdf = doc.render("rotated");

        PdfReader reader = new PdfReader(pdf);
        try {
            assertEquals(90, reader.getPageN(1).getAsNumber(PdfName.ROTATE).intValue());
            assertEquals(400f, reader.getPageSize(1).getWidth(), 0.01f);
            assertEquals(300f, reader.getPageSize(1).getHeight(), 0.01f);
        } finally {
            reader.close();
        }
    }
}
