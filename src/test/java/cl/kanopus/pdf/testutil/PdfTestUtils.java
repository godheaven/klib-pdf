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
package cl.kanopus.pdf.testutil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.openpdf.text.pdf.PdfReader;
import org.openpdf.text.pdf.parser.PdfTextExtractor;

/**
 * Helpers to inspect generated PDFs in tests. Files are only written below {@code target/test-output}.
 */
public final class PdfTestUtils {

    private static final Path OUTPUT_DIR = Path.of("target", "test-output");

    private PdfTestUtils() {
    }

    /** Writes the PDF under {@code target/test-output} so it can be inspected manually after the build. */
    public static Path write(byte[] pdf, String fileName) throws IOException {
        Files.createDirectories(OUTPUT_DIR);
        Path file = OUTPUT_DIR.resolve(fileName);
        Files.write(file, pdf);
        return file;
    }

    /** Extracts the text of every page (index 0 = page 1). */
    public static List<String> pagesText(byte[] pdf) throws IOException {
        PdfReader reader = new PdfReader(pdf);
        try {
            PdfTextExtractor extractor = new PdfTextExtractor(reader);
            List<String> pages = new ArrayList<>();
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                pages.add(extractor.getTextFromPage(i));
            }
            return pages;
        } finally {
            reader.close();
        }
    }

    public static String allText(byte[] pdf) throws IOException {
        return String.join("\n", pagesText(pdf));
    }

    public static int count(String text, String token) {
        int count = 0;
        int idx = text.indexOf(token);
        while (idx >= 0) {
            count++;
            idx = text.indexOf(token, idx + token.length());
        }
        return count;
    }

    /** Number of image XObjects in the given page. */
    public static int imageCount(byte[] pdf, int page) throws IOException {
        PdfReader reader = new PdfReader(pdf);
        try {
            org.openpdf.text.pdf.PdfDictionary resources = reader.getPageN(page).getAsDict(org.openpdf.text.pdf.PdfName.RESOURCES);
            org.openpdf.text.pdf.PdfDictionary xobjects = resources == null ? null : resources.getAsDict(org.openpdf.text.pdf.PdfName.XOBJECT);
            return xobjects == null ? 0 : xobjects.size();
        } finally {
            reader.close();
        }
    }
}
