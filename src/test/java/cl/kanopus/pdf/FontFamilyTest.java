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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;
import org.junit.jupiter.api.Test;
import org.openpdf.text.Font;
import org.openpdf.text.pdf.BaseFont;

class FontFamilyTest {

    @Test
    void testFontsNotNull() {
        assertNotNull(FontFamily.FONT_6_BLACK_NORMAL);
        assertNotNull(FontFamily.FONT_7_BLACK_NORMAL);
        assertNotNull(FontFamily.FONT_7_BLACK_BOLD);
        assertNotNull(FontFamily.FONT_8_BLACK_NORMAL);
        assertNotNull(FontFamily.FONT_8_BLACK_BOLD);
        assertNotNull(FontFamily.FONT_8_RED_NORMAL);
        assertNotNull(FontFamily.FONT_9_BLACK_NORMAL);
        assertNotNull(FontFamily.FONT_9_BLACK_BOLD);
        assertNotNull(FontFamily.FONT_10_BLACK_NORMAL);
        assertNotNull(FontFamily.FONT_12_BLACK_NORMAL);
        assertNotNull(FontFamily.FONT_14_BLACK_NORMAL);
        assertNotNull(FontFamily.FONT_16_BLACK_NORMAL);
        assertNotNull(FontFamily.FONT_40_GRAY_BOLD);
    }

    @Test
    void testFontSizesAndColors() {
        assertEquals(6, FontFamily.FONT_6_BLACK_NORMAL.getSize(), 0.0);
        assertEquals(40, FontFamily.FONT_40_GRAY_BOLD.getSize(), 0.0);
        assertEquals(Color.RED, FontFamily.FONT_16_RED_BOLD.getColor());
        assertEquals(Color.GRAY, FontFamily.FONT_40_GRAY_BOLD.getColor());
    }

    @Test
    void fontsUseEmbeddedUnicodeBaseFonts() {
        BaseFont regular = FontFamily.FONT_10_BLACK_NORMAL.getBaseFont();
        assertNotNull(regular);
        assertEquals(BaseFont.IDENTITY_H, regular.getEncoding());
        assertEquals(true, regular.charExists('Ω'));
        assertEquals(true, regular.charExists('€'));
        assertEquals("LiberationSans", regular.getPostscriptFontName());
        assertEquals("LiberationSans-Bold", FontFamily.FONT_10_BLACK_BOLD.getBaseFont().getPostscriptFontName());
        assertEquals("LiberationSans-Italic", FontFamily.FONT_9_BLACK_ITALIC.getBaseFont().getPostscriptFontName());
        assertEquals("LiberationSans-BoldItalic", FontFamily.baseFont(Font.BOLDITALIC).getPostscriptFontName());
        assertSame(regular, FontFamily.baseFont(Font.UNDEFINED));
        // weight comes from the base font, OpenPDF must not simulate bold on top of it
        assertEquals(Font.NORMAL, FontFamily.FONT_10_BLACK_BOLD.getStyle());
    }

    @Test
    void constantsAreUnmodifiable() {
        Font font = FontFamily.FONT_10_BLACK_NORMAL;
        assertThrows(UnsupportedOperationException.class, () -> font.setSize(20));
        assertThrows(UnsupportedOperationException.class, () -> font.setColor(Color.RED));
        assertThrows(UnsupportedOperationException.class, () -> font.setColor(1, 2, 3));
        assertThrows(UnsupportedOperationException.class, () -> font.setStyle(Font.BOLD));
        assertThrows(UnsupportedOperationException.class, () -> font.setStyle("bold"));
        assertThrows(UnsupportedOperationException.class, () -> font.setFamily("Courier"));
        assertEquals(10, font.getSize(), 0.0);
    }

    @Test
    void factoryReturnsIndependentMutableCopies() {
        Font a = FontFamily.font(11, Font.BOLD | Font.UNDERLINE, null);
        Font b = FontFamily.font(11, Font.BOLD, Color.BLUE);
        assertNotSame(a, b);
        assertEquals(Color.BLACK, a.getColor());
        assertEquals(Font.UNDERLINE, a.getStyle());
        a.setSize(15);
        assertEquals(15, a.getSize(), 0.0);
        assertEquals(11, b.getSize(), 0.0);

        Font copy = new Font(FontFamily.FONT_8_BLACK_NORMAL);
        copy.setColor(Color.GREEN);
        assertEquals(Color.GREEN, copy.getColor());
        assertEquals(Color.BLACK, FontFamily.FONT_8_BLACK_NORMAL.getColor());
    }
}
