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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;


public class FontFamily {

    private static final String ARIAL = "arial";

    public static final Font FONT_6_BLACK_NORMAL = FontFactory.getFont(ARIAL, 6, Font.NORMAL, BaseColor.BLACK);

    public static final Font FONT_7_BLACK_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
    public static final Font FONT_7_BLACK_BOLD = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

    public static final Font FONT_8_BLACK_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
    public static final Font FONT_8_BLACK_BOLD = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
    public static final Font FONT_8_RED_NORMAL = FontFactory.getFont(ARIAL, 8, Font.NORMAL, BaseColor.RED);

    public static final Font FONT_9_BLACK_ITALIC = FontFactory.getFont(ARIAL, 9, Font.ITALIC, BaseColor.BLACK);
    public static final Font FONT_9_BLACK_NORMAL = FontFactory.getFont(ARIAL, 9, Font.NORMAL, BaseColor.BLACK);
    public static final Font FONT_9_BLACK_BOLD = FontFactory.getFont(ARIAL, 9, Font.BOLD, BaseColor.BLACK);

    public static final Font FONT_10_BLACK_ITALIC = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC, BaseColor.BLACK);
    public static final Font FONT_10_BLACK_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    public static final Font FONT_10_BLACK_BOLD = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);

    public static final Font FONT_10_RED_BOLD = FontFactory.getFont(ARIAL, 10, Font.BOLD, BaseColor.RED);

    public static final Font FONT_12_BLACK_NORMAL = FontFactory.getFont(ARIAL, 12, Font.NORMAL, BaseColor.BLACK);
    public static final Font FONT_12_BLACK_BOLD = FontFactory.getFont(ARIAL, 12, Font.BOLD, BaseColor.BLACK);
    public static final Font FONT_12_RED_BOLD = FontFactory.getFont(ARIAL, 12, Font.BOLD, BaseColor.RED);

    public static final Font FONT_14_BLACK_NORMAL = FontFactory.getFont(ARIAL, 14, Font.NORMAL, BaseColor.BLACK);
    public static final Font FONT_14_BLACK_BOLD = FontFactory.getFont(ARIAL, 14, Font.BOLD, BaseColor.BLACK);
    public static final Font FONT_14_RED_BOLD = FontFactory.getFont(ARIAL, 14, Font.BOLD, BaseColor.RED);

    public static final Font FONT_16_BLACK_NORMAL = FontFactory.getFont(ARIAL, 16, Font.NORMAL, BaseColor.BLACK);
    public static final Font FONT_16_BLACK_BOLD = FontFactory.getFont(ARIAL, 16, Font.BOLD, BaseColor.BLACK);
    public static final Font FONT_16_RED_BOLD = FontFactory.getFont(ARIAL, 16, Font.BOLD, BaseColor.RED);

    public static final Font FONT_40_GRAY_BOLD = FontFactory.getFont(FontFactory.HELVETICA, 40, Font.BOLD, BaseColor.GRAY);

    private FontFamily() {
    }
}
