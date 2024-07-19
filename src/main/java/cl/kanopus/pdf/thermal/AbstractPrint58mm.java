package cl.kanopus.pdf.thermal;

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
public abstract class AbstractPrint58mm {

    protected PdfContentByte canvas = null;

    public static final int PAGE_WIDTH = 140; // 58mm -> https://www.unitconverters.net/typography/pixel-x-to-millimeter.htm
    public static final int MARGIN_LEFT = 0;
    public static final int MARGIN_RIGHT = 0;
    private int positionY = 0;

    protected Font font = FontFamily.FONT_8_BLACK_NORMAL;
    protected Space space = Space.NORMAL;

    public static enum Space {

        LOW(10),
        NORMAL(15),
        HIGHT(20);
        public final int size;

        Space(int size) {
            this.size = size;
        }

    }

    public static enum Align {

        LEFT(Element.ALIGN_LEFT),
        CENTER(Element.ALIGN_CENTER),
        RIGHT(Element.ALIGN_RIGHT);

        final int position;

        Align(int size) {
            this.position = size;
        }

    }

    public int getPositionY() {
        return positionY;
    }

    protected void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    protected void printSplit(String text, int maxLineSize) {
        List<String> lines = Utils.splitText(text, maxLineSize);
        for (String l : lines) {
            println(l);
        }
    }

    protected void println(String text) {
        printText(text, MARGIN_LEFT, true, Align.LEFT);
    }

    protected void println(String text, int positionX) {
        printText(text, positionX, true, Align.LEFT);
    }

    protected void println(String text, int positionX, Align align) {
        printText(text, positionX, true, align);
    }

    protected void print(String text) {
        printText(text, MARGIN_LEFT, false, Align.LEFT);
    }

    protected void print(String text, int positionX) {
        printText(text, positionX, false, Align.LEFT);
    }

    protected void print(String text, int positionX, Align align) {
        printText(text, positionX, false, align);
    }

    private void printText(String text, int positionX, boolean automaticNewLine, Align align) {
        Phrase phrase = new Phrase(text, font);
        ColumnText.showTextAligned(this.canvas, align.position, phrase, positionX, positionY, 0);
        positionY = automaticNewLine ? positionY - space.size : positionY;

    }

    protected void printLine() {
        printLine(MARGIN_LEFT, PAGE_WIDTH - MARGIN_RIGHT, AbstractPrint58mm.Align.CENTER, false);
    }

    protected void printLine(int leftX, int rigthX, Align align, boolean bold) {
        LineSeparator lineSeparator = new LineSeparator(bold ? 1 : 0, 100, BaseColor.BLACK, align.position, 0);
        lineSeparator.drawLine(this.canvas, leftX, rigthX, positionY);
        positionY = positionY - space.size;
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

        try {
            Barcode barcode39 = BarcodeFactory.createCode39(code, false);//aqui generamos el codigo de barras
            barcode39.setDrawingText(false);//aqui dibujamos el codigo en una imagen
            barcode39.setBarHeight(33);//aqui ponemos la altura del codigo de barras
            barcode39.setBarWidth(1);//aqui ponemos la longitud del codigo de barras
            BufferedImage buffer = BarcodeImageHandler.getImage(barcode39);
            Image image = bufferedImage2Image(buffer);
            image.setAbsolutePosition(absoluteX, absoluteY);
            image.scaleAbsolute(164, 62);
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

    protected Image generateBarcode(String code) throws RuntimeException {
        try {
            Barcode barcode39 = BarcodeFactory.createCode39(code, false);//aqui generamos el codigo de barras
            barcode39.setDrawingText(false);//aqui dibujamos el codigo en una imagen
            barcode39.setBarHeight(25);//aqui ponemos la altura del codigo de barras
            barcode39.setBarWidth(1);//aqui ponemos la longitud del codigo de barras
            BufferedImage buffer = BarcodeImageHandler.getImage(barcode39);
            Image image = bufferedImage2Image(buffer);
            image.scaleAbsolute(164, 51);
            return image;
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el codigo de barra número " + code);
        }
    }

    public static float pixelsToPoints(float value, int dpi) {
        return value / dpi * 72;
    }

}
