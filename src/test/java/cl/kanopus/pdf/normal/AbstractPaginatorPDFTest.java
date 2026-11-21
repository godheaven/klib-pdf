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

import static org.junit.jupiter.api.Assertions.*;

import cl.kanopus.pdf.DocumentPrinterException;
import cl.kanopus.pdf.normal.table.PDFTable;
import cl.kanopus.pdf.testutil.PdfTestUtils;
import cl.kanopus.pdf.testutil.TestPDFTable;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

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
        for (int i = 0; i < 7; i++)
            items.add("item" + i);

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
        // when the list is empty, no sections are generated
        assertEquals(0, paginator.getCreatedSections());
    }

    /** Paginator that prints recognizable header/footer marks. */
    static class MarkedPaginator extends AbstractPaginatorPDF<String> {

        private final boolean failOnTable;

        MarkedPaginator(List<String> list, int limit, boolean failOnTable) throws DocumentPrinterException {
            super(list, limit);
            this.failOnTable = failOnTable;
        }

        MarkedPaginator(List<CustomList<String>> lists) throws DocumentPrinterException {
            super(lists);
            this.failOnTable = false;
        }

        @Override
        protected void header() throws DocumentPrinterException {
            setPositionY(getPageHeight() - 30);
            println("HEADERMARK");
        }

        @Override
        protected void footer() throws DocumentPrinterException {
            printAbsolute("FOOTERMARK", 20, 50);
        }

        @Override
        protected PDFTable createTableItems(int section, List<String> sublist) throws DocumentPrinterException {
            if (failOnTable) {
                throw new DocumentPrinterException("boom");
            }
            PDFTable table = new PDFTable(1);
            table.setTotalWidth(new float[]{200f});
            table.setLockedWidth(true);
            for (String s : sublist) {
                table.createValueCell(s + "#" + incrementIndex());
            }
            return table;
        }
    }

    private static List<String> items(int n) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < n; i++)
            items.add("item" + i);
        return items;
    }

    @Test
    void footerAndHeaderArePrintedExactlyOncePerPage() throws Exception {
        MarkedPaginator paginator = new MarkedPaginator(items(7), 3, false);
        byte[] pdf = paginator.generateOutput().toByteArray();
        PdfTestUtils.write(pdf, "paginator-footer.pdf");

        List<String> pages = PdfTestUtils.pagesText(pdf);
        assertEquals(3, pages.size());
        for (int i = 0; i < pages.size(); i++) {
            String page = pages.get(i);
            assertEquals(1, PdfTestUtils.count(page, "FOOTERMARK"), "page " + (i + 1) + ": " + page);
            assertEquals(1, PdfTestUtils.count(page, "HEADERMARK"), "page " + (i + 1) + ": " + page);
            assertTrue(page.contains("Página " + (i + 1) + " de 3"), page);
        }
        assertTrue(pages.get(2).contains("item6#7"), pages.get(2));
    }

    @Test
    void footerOnlyOnLastPageWhenNotAlwaysPrinted() throws Exception {
        MarkedPaginator paginator = new MarkedPaginator(items(5), 2, false);
        paginator.setAlwaysPrintFooter(false);
        paginator.setAlwaysPrintHeader(false);
        paginator.setPrintDateOfGeneration(true);
        paginator.setPositionX(20);
        List<String> pages = PdfTestUtils.pagesText(paginator.generateOutput().toByteArray());

        assertEquals(3, pages.size());
        assertEquals(0, PdfTestUtils.count(pages.get(0), "FOOTERMARK"));
        assertEquals(0, PdfTestUtils.count(pages.get(1), "FOOTERMARK"));
        assertEquals(1, PdfTestUtils.count(pages.get(2), "FOOTERMARK"));
        assertEquals(1, PdfTestUtils.count(pages.get(0), "HEADERMARK"));
        assertEquals(0, PdfTestUtils.count(pages.get(1), "HEADERMARK"));
        assertTrue(pages.get(2).contains("Documento generado con fecha"), pages.get(2));
    }

    @Test
    void singlePageShowsTotalOnlyWhenRequested() throws Exception {
        MarkedPaginator paginator = new MarkedPaginator(items(2), 5, false);
        assertFalse(PdfTestUtils.allText(paginator.generateOutput().toByteArray()).contains("Página"));

        MarkedPaginator always = new MarkedPaginator(items(2), 5, false);
        always.setAlwaysShowTotalPages(true);
        assertTrue(PdfTestUtils.allText(always.generateOutput().toByteArray()).contains("Página 1 de 1"));
    }

    @Test
    void multipleSectionsPrintTheirOwnHeader() throws Exception {
        AtomicReference<MarkedPaginator> current = new AtomicReference<>();
        MarkedPaginator base = new MarkedPaginator(items(0), 1, false);
        List<AbstractPaginatorPDF<String>.CustomList<String>> sections = new ArrayList<>();
        sections.add(base.new CustomList<>(() -> current.get().println("SECTION-A"), items(3), 2));
        sections.add(base.new CustomList<>(() -> current.get().println("SECTION-B"), items(1), 2));
        sections.add(null);
        base.closeQuietly();

        MarkedPaginator paginator = new MarkedPaginator(sections);
        current.set(paginator);
        List<String> pages = PdfTestUtils.pagesText(paginator.generateOutput().toByteArray());
        assertEquals(3, pages.size());
        assertTrue(pages.get(0).contains("SECTION-A"), pages.get(0));
        assertTrue(pages.get(2).contains("SECTION-B"), pages.get(2));
        assertTrue(pages.get(2).contains("item0#1"), "index restarts per section: " + pages.get(2));
        assertNotNull(sections.get(1).getHeader());
        assertEquals(2, sections.get(1).getLimit());
        assertEquals(1, sections.get(1).getList().size());
    }

    @Test
    void invalidLimitAndNullListAreHandled() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> new MarkedPaginator(items(3), 0, false));
        assertThrows(IllegalArgumentException.class, () -> new MarkedPaginator(items(3), -1, false));

        MarkedPaginator nullList = new MarkedPaginator(null, 3, false);
        byte[] pdf = nullList.generateOutput().toByteArray();
        assertEquals(1, PdfTestUtils.pagesText(pdf).size());

        MarkedPaginator nullLists = new MarkedPaginator((List<AbstractPaginatorPDF<String>.CustomList<String>>) null);
        assertTrue(nullLists.generateOutput().size() > 0);
    }

    @Test
    void documentIsClosedWhenGenerationFails() throws Exception {
        MarkedPaginator paginator = new MarkedPaginator(items(3), 2, true);
        DocumentPrinterException ex = assertThrows(DocumentPrinterException.class, paginator::generateOutput);
        assertEquals("boom", ex.getMessage());
        // the document was closed: the partial PDF is complete and readable
        assertTrue(PdfTestUtils.allText(paginator.close().toByteArray()).contains("HEADERMARK"));
    }
}
