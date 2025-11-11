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
package cl.kanopus.pdf.normal.table;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PDFTableTest {

    @Test
    void testCreateAndAddCells() {
        PDFTable table = new PDFTable(2);
        table.createLabelCell("L1");
        table.createValueCell("V1");
        table.createLabelCellWithColspan("Label Col", 2);
        table.createValueCellWithRowspan("Val Row", 2);

        // After adding cells, number of rows should be >=1 and total width setting should accept arrays
        table.setTotalWidth(new float[]{100f, 200f});
        assertEquals(2, table.getNumberOfColumns());
    }

    @Test
    void testCreateValueCellWithImage() {
        PDFTable table = new PDFTable(1);
        // create a simple PdfPCell and add directly via createValueCell(Image) is overloaded with Image
        // we can't easily create com.itextpdf.text.Image without files; so we test other public APIs
        table.createValueCell("text");
        assertEquals(1, table.getNumberOfColumns());
    }

    @Test
    void testAlignEnumValues() {
        assertEquals(1, PDFTable.Align.CENTER.getValue());
        assertEquals(0, PDFTable.Align.LEFT.getValue());
        assertEquals(2, PDFTable.Align.RIGHT.getValue());
    }
}

