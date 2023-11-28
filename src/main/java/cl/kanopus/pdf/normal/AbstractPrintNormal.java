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

    public static final int PAGE_WIDTH = (int) (595); //21cm
    public static final int PAGE_HEIGHT = (int) (935);  //33cm
    public static final int MARGIN_LEFT = (int) (3);
    public static final int MARGIN_RIGHT = (int) (3);
    private int positionY = 0;

    private final Font DEFAULT_FONT = FontFamily.FONT_10_BLACK_NORMAL;
    private Space space = Space.NORMAL;

    public static enum Space {

        LOW(10),
        NORMAL(15),
        HIGHT(20);
        final int size;

        Space(int size) {
            this.size = size;
        }

    }

    public static enum Align {

        LEFT(Element.ALIGN_LEFT),
        CENTER(Element.ALIGN_CENTER),
        RIGHT(Element.ALIGN_RIGHT);

        final int id;

        Align(int id) {
            this.id = id;
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

    protected void printCodigoBarra(Document document, String codigoBarra, int absoluteX, int absoluteY) throws PrinterException {

        try {
            Barcode barcode_c = BarcodeFactory.createCode39(codigoBarra, false);//aqui generamos el codigo de barras
            barcode_c.setDrawingText(false);//aqui dibujamos el codigo en una imagen
            barcode_c.setBarHeight(33);//aqui ponemos la altura del codigo de barras
            barcode_c.setBarWidth(1);//aqui ponemos la longitud del codigo de barras
            BufferedImage image_c = BarcodeImageHandler.getImage(barcode_c);
            Image imageCodigoBarra = bufferedImage2Image(image_c);
            imageCodigoBarra.setAbsolutePosition(absoluteX, absoluteY);
            //imageCodigoBarra.scaleAbsolute(184, 72);
            imageCodigoBarra.scaleAbsolute(164, 62);
            document.add(imageCodigoBarra);
        } catch (Exception e) {
            throw new PrinterException("Error al generar el codigo de barra número " + codigoBarra);
        }

    }

    private Image bufferedImage2Image(BufferedImage bufferedImage) throws IOException, BadElementException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        Image image = Image.getInstance(baos.toByteArray());
        return image;
    }

    protected Image generateBarcode(String code) throws RuntimeException {
        try {
            Barcode barcode_c = BarcodeFactory.createCode39(code, false);//aqui generamos el codigo de barras
            barcode_c.setDrawingText(false);//aqui dibujamos el codigo en una imagen
            //barcode_c.setBarHeight(33);//aqui ponemos la altura del codigo de barras
            barcode_c.setBarHeight(25);//aqui ponemos la altura del codigo de barras
            barcode_c.setBarWidth(1);//aqui ponemos la longitud del codigo de barras
            BufferedImage image_c = BarcodeImageHandler.getImage(barcode_c);
            Image imageBarcode = bufferedImage2Image(image_c);
            //imageBarcode.setAbsolutePosition(absoluteX, absoluteY); FIXME
            //imageCodigoBarra.scaleAbsolute(184, 72);
            //imageBarcode.scaleAbsolute(164, 62);
            imageBarcode.scaleAbsolute(164, 51);
            return imageBarcode;
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el codigo de barra número " + code);
        }
    }

}
