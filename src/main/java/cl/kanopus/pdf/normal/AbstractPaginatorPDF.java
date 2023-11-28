package cl.kanopus.pdf.normal;

import cl.kanopus.common.util.Utils;
import cl.kanopus.pdf.FontFamily;
import cl.kanopus.pdf.normal.table.PDFTable;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 */
public abstract class AbstractPaginatorPDF extends AbstractPrintNormal {

    protected final Document document = new Document(new Rectangle(PAGE_WIDTH, PAGE_HEIGHT));

    private final List<CustomList> lists = new ArrayList<>();
    private int positionX;
    private boolean alwaysPrintHeader = true;
    private int index = 0;
    private boolean printDateOfGeneration = false;

    public AbstractPaginatorPDF() {
    }

    public void setAlwaysPrintHeader(boolean alwaysPrintHeader) {
        this.alwaysPrintHeader = alwaysPrintHeader;
    }

    public void setPrintDateOfGeneration(boolean printDateOfGeneration) {
        this.printDateOfGeneration = printDateOfGeneration;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void addList(Header header, List list, int limit) {
        this.lists.add(new CustomList(header, list, limit));
    }

    public void addList(List list, int limit) {
        this.lists.add(new CustomList(null, list, limit));
    }

    public final ByteArrayOutputStream generateOutput() throws Exception {
        return getOutputStream();
    }

    protected abstract void header() throws Exception;

    protected abstract PDFTable createTableItems(int section, List sublist) throws DocumentException;

    private ByteArrayOutputStream getOutputStream() throws Exception {
        //Prepare DATA
        document.setMargins(20, 20, 20, 5);
        document.setMarginMirroringTopBottom(false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();
        this.canvas = writer.getDirectContent();

        header();
        int page = 1;
        int totalPages = calculePages();
        /**
         * **************** ITEMS ***********************
         * *************************************************
         */
        int section = 1;
        for (CustomList custom : lists) {
            index = 0;
            List<List> sublist = Utils.chunkList(custom.getList(), custom.getLimit());

            //int cantidadHojas = sublist.size();
            for (List sl : sublist) {
                if (custom.header != null) {
                    custom.header.print();
                }
                PDFTable pdfTableItems = createTableItems(section, sl);
                pdfTableItems.writeSelectedRows(0, -1, positionX, getPositionY(), canvas);
                if (totalPages > 1) {
                    printAbsolute("Página " + page + " de " + totalPages, 570, 30, Align.RIGHT);
                }

                if (!(page == totalPages)) {
                    document.newPage();
                    if (alwaysPrintHeader) {
                        header();
                    }
                }
                page++;
            }
            section++;
        }
        if (printDateOfGeneration) {
            printAbsolute("Documento generado con fecha: " + Utils.getDateTimeFormat(LocalDateTime.now(), "dd MMMM yyyy HH:mm:ss"), 10, 30, FontFamily.FONT_7_BLACK_NORMAL);
        }

        document.close();
        return baos;
    }

    private int calculePages() {
        int totalPages = 0;
        for (CustomList custom : lists) {
            int pages = (custom.getList().size() + custom.getLimit() - 1) / custom.getLimit();
            totalPages += pages;
        }
        return totalPages;
    }

    protected int incrementIndex() {
        return ++index;
    }

    protected class CustomList {

        private final List list;
        private final int limit;
        private final Header header;

        public CustomList(Header header, List list, int limit) {
            this.header = header;
            this.list = list;
            this.limit = limit;
        }

        public List getList() {
            return list;
        }

        public int getLimit() {
            return limit;
        }

        public Header getHeader() {
            return header;
        }

    }

    protected abstract class Header {

        abstract void print() throws Exception;
    }

}
