package cl.kanopus.pdf.normal;

import cl.kanopus.common.util.Utils;
import cl.kanopus.pdf.FontFamily;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 */
public abstract class AbstractPrintNormal {

    protected PdfContentByte canvas = null;

    public static final int PAGE_WIDTH = 595; //21cm
    public static final int PAGE_HEIGHT = 935;  //33cm
    public static final int MARGIN_LEFT = 3;
    public static final int MARGIN_RIGHT = 3;
    private int positionY = 0;

    private static final Font DEFAULT_FONT = FontFamily.FONT_10_BLACK_NORMAL;
    private Space space = Space.NORMAL;

    public enum Scale {

        NORMAL(164, 62),
        BIG(330, 72);
        final int width;
        final int height;

        Scale(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

    }

    public enum Space {

        LOW(10),
        NORMAL(15),
        HIGHT(20);
        final int size;

        Space(int size) {
            this.size = size;
        }

    }

    public enum Align {

        LEFT(Element.ALIGN_LEFT),
        CENTER(Element.ALIGN_CENTER),
        RIGHT(Element.ALIGN_RIGHT);

        final int id;

        Align(int id) {
            this.id = id;
        }

    }

    public class Rotate extends PdfPageEventHelper {

        protected PdfNumber orientation = PdfPage.PORTRAIT;

        public void setOrientation(PdfNumber orientation) {
            this.orientation = orientation;
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            writer.addPageDictEntry(PdfName.ROTATE, orientation);
        }
    }

    public int getPositionY() {
        return positionY;
    }

    protected void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Space getSpace() {
        return space;
    }

    protected void println(String text) {
        printText(text, MARGIN_LEFT, true, null, Align.LEFT);
    }

    protected void println(String text, int positionX) {
        printText(text, positionX, true, null, Align.LEFT);
    }

    protected void println(String text, int positionX, Font font) {
        printText(text, positionX, true, font, Align.LEFT);
    }

    protected void println(String text, int positionX, Align align) {
        printText(text, positionX, true, null, align);
    }

    protected void println(String text, int positionX, Font font, Align align) {
        printText(text, positionX, true, font, align);
    }

    protected void print(String text) {
        printText(text, MARGIN_LEFT, false, null, Align.LEFT);
    }

    protected void print(String text, int positionX) {
        printText(text, positionX, false, null, Align.LEFT);
    }

    protected void print(String text, int positionX, Align align) {
        printText(text, positionX, false, null, align);
    }

    protected void print(String text, int positionX, Font font) {
        printText(text, positionX, false, font, null);
    }

    protected void print(String text, int positionX, Font font, Align align) {
        printText(text, positionX, false, font, align);
    }

    private void printText(String text, int positionX, boolean automaticNewLine, Font font, Align align) {
        if (text != null) {
            Phrase phrase = new Phrase(text, (font == null) ? DEFAULT_FONT : font);
            ColumnText.showTextAligned(this.canvas, (align == null) ? Align.LEFT.id : align.id, phrase, positionX, positionY, 0);
        }
        positionY = automaticNewLine ? positionY - space.size : positionY;
    }

    protected void printlnSplit(int maxLineSize, String text, int positionX, int positionY, Font font) {
        setPositionY(positionY);
        printlnSplit(maxLineSize, text, positionX, font);
    }

    protected void printlnSplit(int maxLineSize, String text, int positionX, Font font) {
        if (!Utils.isNullOrEmpty(text)) {
            List<String> lines = Utils.splitText(text, maxLineSize);
            for (String l : lines) {
                println(l, positionX, font);
            }
        }
    }

    protected void printAbsolute(String text, float x, float y) {
        printAbsolute(text, x, y, null, null);
    }

    protected void printAbsolute(String text, float x, float y, Align align) {
        printAbsolute(text, x, y, null, align);
    }

    protected void printAbsolute(String text, float x, float y, Font font) {
        printAbsolute(text, x, y, font, null);
    }

    protected void printAbsolute(String text, float x, float y, Font font, Align align) {
        Phrase phrase = new Phrase(text, (font == null) ? DEFAULT_FONT : font);
        ColumnText.showTextAligned(this.canvas, (align == null) ? Align.LEFT.id : align.id, phrase, x, y, 0);
    }

    protected void printLine() {
        printLine(MARGIN_LEFT, PAGE_WIDTH - MARGIN_RIGHT, Align.CENTER, false);
    }

    protected void printLine(int leftX, int rigthX, Align align, boolean bold) {
        LineSeparator lineSeparator = new LineSeparator(bold ? 1 : 0, 100, BaseColor.BLACK, align.id, 0);
        lineSeparator.drawLine(this.canvas, leftX, rigthX, positionY);
        positionY = positionY - space.size;
    }

    protected void printLine(int leftX, int rigthX, int y, boolean bold) {
        LineSeparator lineSeparator = new LineSeparator(bold ? 1 : 0, 100, BaseColor.BLACK, Element.ALIGN_LEFT, 0);
        lineSeparator.drawLine(this.canvas, leftX, rigthX, y);
    }

    protected void printLine(int leftX, int rigthX, int y) {
        printLine(leftX, rigthX, y, false);
    }

    protected void drawRectangleFixed(float x, float y, float width, float height, BaseColor color, int lineWidth) {
        this.canvas.saveState();
        PdfGState state = new PdfGState();
        state.setFillOpacity(0);
        this.canvas.setGState(state);
        this.canvas.setRGBColorStroke(color.getRed(), color.getGreen(), color.getBlue());
        this.canvas.setLineWidth(lineWidth);
        this.canvas.rectangle(x, y, width, height);
        this.canvas.fillStroke();
        this.canvas.restoreState();
    }

    protected void drawRectangle(float x, float y, float width, float height, BaseColor color, int lineWidth) {
        this.canvas.saveState();
        PdfGState state = new PdfGState();
        state.setFillOpacity(0);
        this.canvas.setGState(state);
        this.canvas.setRGBColorStroke(color.getRed(), color.getGreen(), color.getBlue());
        this.canvas.setLineWidth(lineWidth);
        this.canvas.rectangle(x, y - height, width, height);
        this.canvas.fillStroke();
        this.canvas.restoreState();
    }

    protected float cm2millimeters(float cm) {
        return cm * 10;
    }

    protected void printBarcode(Document document, String code, int absoluteX, int absoluteY) throws PrinterException {
        printBarcode(document, code, absoluteX, absoluteY, Scale.NORMAL);
    }

    protected void printBarcode(Document document, String code, int absoluteX, int absoluteY, Scale scale) throws PrinterException {

        try {
            Barcode barcode39 = BarcodeFactory.createCode39(code, false);//aqui generamos el codigo de barras
            barcode39.setDrawingText(false);//aqui dibujamos el codigo en una imagen
            barcode39.setBarHeight(33);//aqui ponemos la altura del codigo de barras
            barcode39.setBarWidth(1);//aqui ponemos la longitud del codigo de barras
            BufferedImage buffer = BarcodeImageHandler.getImage(barcode39);
            Image image = bufferedImage2Image(buffer);
            image.setAbsolutePosition(absoluteX, absoluteY);
            image.scaleAbsolute(scale.getWidth(), scale.getHeight());
            document.add(image);
        } catch (Exception e) {
            throw new PrinterException("Error al generar el codigo de barra número " + code);
        }
    }

    private Image bufferedImage2Image(BufferedImage bufferedImage) throws IOException, BadElementException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        return Image.getInstance(baos.toByteArray());
    }

}
