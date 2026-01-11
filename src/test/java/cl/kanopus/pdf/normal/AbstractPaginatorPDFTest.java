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
import cl.kanopus.pdf.normal.table.PDFTable;
import cl.kanopus.pdf.testutil.TestPDFTable;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbstractPaginatorPDFTest {

    static class TestPaginator extends AbstractPaginatorPDF<String> {

        private int createdSections = 0;
        private int incrementCalls = 0;

        TestPaginator(List<String> list, int limit) throws DocumentPrinterException {
            super(list, limit);
        }

        @Override
        protected void header() throws DocumentPrinterException {
            // Ensure document has a page and initial positionY for tests
            newPage();
            setPositionY(getPageHeight() - 20);
            // Add a small text so the document is considered to have content/pages
            println(" ");
        }

        @Override
        protected PDFTable createTableItems(int section, List<String> sublist) throws DocumentPrinterException {
            createdSections++;
            TestPDFTable table = new TestPDFTable(3, 10f);
            for (String s : sublist) {
                table.createValueCell(s);
                incrementCalls++;
            }
            return table;
        }

        int getCreatedSections() {
            return createdSections;
        }

        int getIncrementCalls() {
            return incrementCalls;
        }
    }

    @Test
    void generateOutput_ShouldProduceStream_WhenItemsPresent() throws Exception {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 7; i++) items.add("item" + i);

        TestPaginator paginator = new TestPaginator(items, 3);
        ByteArrayOutputStream baos = paginator.generateOutput();
        assertNotNull(baos);
        assertTrue(baos.size() > 0);
        assertEquals(3, paginator.getCreatedSections());
    }

    @Test
    void generateOutput_ShouldNotFail_WhenListEmpty() throws Exception {
        List<String> items = new ArrayList<>();
        TestPaginator paginator = new TestPaginator(items, 3);
        ByteArrayOutputStream baos = paginator.generateOutput();
        assertNotNull(baos);
        assertTrue(baos.size() > 0);
        // cuando la lista está vacía no se genera ninguna sección
        assertEquals(0, paginator.getCreatedSections());
    }

}
