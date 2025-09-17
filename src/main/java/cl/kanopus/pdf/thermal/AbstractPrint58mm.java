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
package cl.kanopus.pdf.thermal;


import cl.kanopus.pdf.DocumentPrinterException;
import cl.kanopus.pdf.FontFamily;


public abstract class AbstractPrint58mm extends AbstractThermal {

    protected static final int DEFAULT_HEIGHT = 370;
    protected static final int DEFAULT_HEIGHT_ITEM = 38;

    protected AbstractPrint58mm(int pageHeight) throws DocumentPrinterException {
        super(140, pageHeight, 0, 0);
        setPositionY(pageHeight - Space.LOW.size);
        setFont(FontFamily.FONT_7_BLACK_BOLD);
        setSpace(Space.LOW);
    }

    protected static int calcularHeight(int items) {
        return (items * DEFAULT_HEIGHT_ITEM) + DEFAULT_HEIGHT;
    }

}
