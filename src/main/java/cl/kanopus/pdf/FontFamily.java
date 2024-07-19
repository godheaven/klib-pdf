package cl.kanopus.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 */
public class FontFamily {

    public static final Font FONT_6_BLACK_NORMAL = FontFactory.getFont("arial", 6, Font.NORMAL, BaseColor.BLACK);

    public static final Font FONT_7_BLACK_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
    public static final Font FONT_7_BLACK_BOLD = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

    public static final Font FONT_8_BLACK_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
    public static final Font FONT_8_BLACK_BOLD = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
    public static final Font FONT_8_RED_NORMAL = FontFactory.getFont("arial", 8, Font.NORMAL, BaseColor.RED);

    public static final Font FONT_10_BLACK_ITALIC = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC, BaseColor.BLACK);
    public static final Font FONT_10_BLACK_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    public static final Font FONT_10_BLACK_BOLD = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);

    public static final Font FONT_10_RED_BOLD = FontFactory.getFont("arial", 10, Font.BOLD, BaseColor.RED);

    public static final Font FONT_12_BLACK_NORMAL = FontFactory.getFont("arial", 12, Font.NORMAL, BaseColor.BLACK);
    public static final Font FONT_12_BLACK_BOLD = FontFactory.getFont("arial", 12, Font.BOLD, BaseColor.BLACK);
    public static final Font FONT_12_RED_BOLD = FontFactory.getFont("arial", 12, Font.BOLD, BaseColor.RED);

    public static final Font FONT_16_BLACK_NORMAL = FontFactory.getFont("arial", 16, Font.NORMAL, BaseColor.BLACK);
    public static final Font FONT_16_BLACK_BOLD = FontFactory.getFont("arial", 16, Font.BOLD, BaseColor.BLACK);
    public static final Font FONT_16_RED_BOLD = FontFactory.getFont("arial", 16, Font.BOLD, BaseColor.RED);

    private FontFamily() {
    }
}
