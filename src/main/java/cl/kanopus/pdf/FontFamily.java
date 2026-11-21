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
package cl.kanopus.pdf;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import org.openpdf.text.Font;
import org.openpdf.text.pdf.BaseFont;

/**
 * Predefined fonts used by the printers of this library.
 * <p>
 * All fonts are backed by the bundled <b>Liberation Sans</b> TrueType font (SIL Open Font License 1.1, metric compatible with Arial/Helvetica), embedded in the generated PDF with {@code Identity-H} encoding so any Unicode text supported by the font (Latin, Greek, Cyrillic, symbols such as "€")
 * renders correctly.
 * <p>
 * The {@code FONT_*} constants are <b>unmodifiable</b>: calling a setter on them throws {@link UnsupportedOperationException} (they are shared across threads). Use {@link #font(float, int, Color)} or {@code new Font(FontFamily.FONT_10_BLACK_NORMAL)} to obtain an independent, mutable copy.
 */
public final class FontFamily {

    private static final String FONT_PATH = "/cl/kanopus/pdf/fonts/";

    public static final Font FONT_6_BLACK_NORMAL = constant(6, Font.NORMAL, Color.BLACK);

    public static final Font FONT_7_BLACK_NORMAL = constant(7, Font.NORMAL, Color.BLACK);
    public static final Font FONT_7_BLACK_BOLD = constant(7, Font.BOLD, Color.BLACK);

    public static final Font FONT_8_BLACK_NORMAL = constant(8, Font.NORMAL, Color.BLACK);
    public static final Font FONT_8_BLACK_BOLD = constant(8, Font.BOLD, Color.BLACK);
    public static final Font FONT_8_RED_NORMAL = constant(8, Font.NORMAL, Color.RED);

    public static final Font FONT_9_BLACK_ITALIC = constant(9, Font.ITALIC, Color.BLACK);
    public static final Font FONT_9_BLACK_NORMAL = constant(9, Font.NORMAL, Color.BLACK);
    public static final Font FONT_9_BLACK_BOLD = constant(9, Font.BOLD, Color.BLACK);

    public static final Font FONT_10_BLACK_ITALIC = constant(10, Font.ITALIC, Color.BLACK);
    public static final Font FONT_10_BLACK_NORMAL = constant(10, Font.NORMAL, Color.BLACK);
    public static final Font FONT_10_BLACK_BOLD = constant(10, Font.BOLD, Color.BLACK);

    public static final Font FONT_10_RED_BOLD = constant(10, Font.BOLD, Color.RED);

    public static final Font FONT_12_BLACK_NORMAL = constant(12, Font.NORMAL, Color.BLACK);
    public static final Font FONT_12_BLACK_BOLD = constant(12, Font.BOLD, Color.BLACK);
    public static final Font FONT_12_RED_BOLD = constant(12, Font.BOLD, Color.RED);

    public static final Font FONT_14_BLACK_NORMAL = constant(14, Font.NORMAL, Color.BLACK);
    public static final Font FONT_14_BLACK_BOLD = constant(14, Font.BOLD, Color.BLACK);
    public static final Font FONT_14_RED_BOLD = constant(14, Font.BOLD, Color.RED);

    public static final Font FONT_16_BLACK_NORMAL = constant(16, Font.NORMAL, Color.BLACK);
    public static final Font FONT_16_BLACK_BOLD = constant(16, Font.BOLD, Color.BLACK);
    public static final Font FONT_16_RED_BOLD = constant(16, Font.BOLD, Color.RED);

    public static final Font FONT_40_GRAY_BOLD = constant(40, Font.BOLD, Color.GRAY);

    private FontFamily() {
    }

    /**
     * Creates a new, mutable font backed by the embedded Liberation Sans family.
     *
     * @param size
     *            font size in points
     * @param style
     *            {@link Font#NORMAL}, {@link Font#BOLD}, {@link Font#ITALIC} or {@link Font#BOLDITALIC} (other flags such as {@link Font#UNDERLINE} are kept)
     * @param color
     *            text color; {@code null} means black
     * @return a new font instance not shared with anybody else
     */
    public static Font font(float size, int style, Color color) {
        return new Font(baseFont(style), size, decorations(style), color == null ? Color.BLACK : color);
    }

    /**
     * Returns the embedded base font that matches the bold/italic bits of {@code style}.
     *
     * @param style
     *            a {@link Font} style
     * @return the shared (immutable) embedded base font
     */
    public static BaseFont baseFont(int style) {
        int s = style == Font.UNDEFINED ? Font.NORMAL : style;
        boolean bold = (s & Font.BOLD) != 0;
        boolean italic = (s & Font.ITALIC) != 0;
        if (bold && italic) {
            return BaseFonts.BOLD_ITALIC;
        } else if (bold) {
            return BaseFonts.BOLD;
        } else if (italic) {
            return BaseFonts.ITALIC;
        }
        return BaseFonts.REGULAR;
    }

    /**
     * The weight/slant is provided by the base font itself, so only the decoration flags (underline, strike-through) are kept; otherwise OpenPDF would additionally simulate bold/italic.
     */
    private static int decorations(int style) {
        if (style == Font.UNDEFINED) {
            return Font.NORMAL;
        }
        return style & ~(Font.BOLD | Font.ITALIC);
    }

    private static Font constant(float size, int style, Color color) {
        return new UnmodifiableFont(font(size, style, color));
    }

    private static final class BaseFonts {

        static final BaseFont REGULAR = load("LiberationSans-Regular.ttf");
        static final BaseFont BOLD = load("LiberationSans-Bold.ttf");
        static final BaseFont ITALIC = load("LiberationSans-Italic.ttf");
        static final BaseFont BOLD_ITALIC = load("LiberationSans-BoldItalic.ttf");

        private BaseFonts() {
        }

        private static BaseFont load(String file) {
            try (InputStream in = FontFamily.class.getResourceAsStream(FONT_PATH + file)) {
                if (in == null) {
                    throw new IllegalStateException("Bundled font not found: " + FONT_PATH + file);
                }
                return BaseFont.createFont(file, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, false, in.readAllBytes(), null);
            } catch (IOException ex) {
                throw new UncheckedIOException("Unable to load bundled font " + file, ex);
            }
        }
    }

    /**
     * Font whose attributes cannot be changed after construction; shared constants must not be mutated.
     */
    private static final class UnmodifiableFont extends Font {

        private final boolean frozen;

        UnmodifiableFont(Font font) {
            super(font);
            this.frozen = true;
        }

        private void checkMutable() {
            if (frozen) {
                throw new UnsupportedOperationException("FontFamily constants are shared and unmodifiable; use FontFamily.font(...) or new Font(font) to obtain a copy");
            }
        }

        @Override
        public void setFamily(String family) {
            checkMutable();
            super.setFamily(family);
        }

        @Override
        public void setSize(float size) {
            checkMutable();
            super.setSize(size);
        }

        @Override
        public void setStyle(int style) {
            checkMutable();
            super.setStyle(style);
        }

        @Override
        public void setStyle(String style) {
            checkMutable();
            super.setStyle(style);
        }

        @Override
        public void setColor(Color color) {
            checkMutable();
            super.setColor(color);
        }

        @Override
        public void setColor(int red, int green, int blue) {
            checkMutable();
            super.setColor(red, green, blue);
        }
    }
}
