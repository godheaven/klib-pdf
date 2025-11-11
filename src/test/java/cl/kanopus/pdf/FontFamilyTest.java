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

import com.itextpdf.text.Font;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void testFontSizes() {
        Font f = FontFamily.FONT_6_BLACK_NORMAL;
        assertEquals(6, f.getSize(), 0.0);

        assertEquals(40, FontFamily.FONT_40_GRAY_BOLD.getSize(), 0.0);
    }
}

