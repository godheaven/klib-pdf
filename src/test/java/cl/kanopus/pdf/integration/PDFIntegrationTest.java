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

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PDFIntegrationTest {
    
    @Test
    void testGeneratePdfAndExtractText() throws Exception {
        String content = "Unique test text 12345";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            document.add(new Paragraph(content));
        } finally {
            document.close();
        }

        byte[] pdfBytes = baos.toByteArray();
        assertTrue(pdfBytes.length > 0, "The generated PDF must contain bytes");

        // Read the PDF in memory and extract text from the first page
        PdfReader reader = new PdfReader(pdfBytes);
        try {
            String extracted = PdfTextExtractor.getTextFromPage(reader, 1);
            assertNotNull(extracted);
            assertTrue(extracted.contains("Unique test text 12345"), "Extracted text should contain the inserted content");
        } finally {
            reader.close();
        }
    }
}
