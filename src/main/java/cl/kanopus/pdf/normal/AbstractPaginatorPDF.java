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
package cl.kanopus.pdf.normal;


import cl.kanopus.common.util.Utils;
import cl.kanopus.pdf.DocumentPrinterException;
import cl.kanopus.pdf.FontFamily;
import cl.kanopus.pdf.normal.table.PDFTable;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractPaginatorPDF<T> extends AbstractPrintNormal {

    private final List<CustomList<T>> lists = new ArrayList<>();
    private int positionX = 10;
    private boolean alwaysPrintHeader = true;
    private boolean alwaysPrintFooter = true;
    private boolean alwaysShowTotalPages = false;
    private int index = 0;
    private boolean printDateOfGeneration = false;

    protected AbstractPaginatorPDF(List<T> list, int limit) throws DocumentPrinterException {
        super();
        this.lists.add(new CustomList<>(null, list, limit));
    }

    protected AbstractPaginatorPDF(List<CustomList<T>> lists) throws DocumentPrinterException {
        this.lists.addAll(lists);
    }

    public void setAlwaysPrintHeader(boolean alwaysPrintHeader) {
        this.alwaysPrintHeader = alwaysPrintHeader;
    }

    public void setAlwaysPrintFooter(boolean alwaysPrintFooter) {
        this.alwaysPrintFooter = alwaysPrintFooter;
    }

    public void setPrintDateOfGeneration(boolean printDateOfGeneration) {
        this.printDateOfGeneration = printDateOfGeneration;
    }

    public void setAlwaysShowTotalPages(boolean alwaysShowTotalPages) {
        this.alwaysShowTotalPages = alwaysShowTotalPages;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    protected abstract void header() throws DocumentPrinterException;

    protected abstract PDFTable createTableItems(int section, List<T> sublist) throws DocumentPrinterException;

    protected void footer() throws DocumentPrinterException {
    }

    public final ByteArrayOutputStream generateOutput() throws DocumentPrinterException {
        header();
        int page = 1;
        int section = 1;
        int totalPages = calculePages();

        for (CustomList<T> custom : lists) {
            index = 0;
            List<List<T>> sublist = Utils.chunkList(custom.getList(), custom.getLimit());
            for (List<T> sl : sublist) {
                generateBody(custom.header, page, totalPages, section, sl);
                page++;
            }
            section++;
        }
        footer();
        if (printDateOfGeneration) {
            printAbsolute("Documento generado con fecha: " + Utils.getDateTimeFormat(LocalDateTime.now(), "dd MMMM yyyy HH:mm:ss"), 10, 30, FontFamily.FONT_7_BLACK_NORMAL);
        }

        return close();
    }

    private void generateBody(Header customHeader, int currentPage, int totalPages, int section, List<T> sl) throws DocumentPrinterException {
        if (customHeader != null) {
            customHeader.print();
        }
        PDFTable pdfTableItems = createTableItems(section, sl);
        pdfTableItems.writeSelectedRows(0, -1, positionX, getPositionY() - 5, super.getCanvas());

        if (alwaysShowTotalPages || (totalPages > 1)) {
            printAbsolute("Página " + currentPage + " de " + totalPages, 570, 30, Align.RIGHT);
        }

        setPositionY(getPositionY() - (int) pdfTableItems.getTotalHeight() - Space.HIGHT.getSize());

        if (currentPage != totalPages) {
            newPage();
            if (alwaysPrintHeader) {
                header();
            }

            if (alwaysPrintFooter) {
                footer();
            }
        }
    }

    private int calculePages() {
        int totalPages = 0;
        for (CustomList<T> custom : lists) {
            int pages = (custom.getList().size() + custom.getLimit() - 1) / custom.getLimit();
            totalPages += pages;
        }
        return totalPages;
    }

    protected int incrementIndex() {
        return ++index;
    }

    protected class CustomList<K> {

        private final List<K> list;
        private final int limit;
        private final Header header;

        public CustomList(Header header, List<K> list, int limit) {
            this.header = header;
            this.list = list;
            this.limit = limit;
        }

        public List<K> getList() {
            return list;
        }

        public int getLimit() {
            return limit;
        }

        public Header getHeader() {
            return header;
        }

    }

    protected interface Header {

        abstract void print() throws DocumentPrinterException;
    }

}
