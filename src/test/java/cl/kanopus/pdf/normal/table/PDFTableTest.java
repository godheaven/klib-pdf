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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cl.kanopus.pdf.FontFamily;
import java.awt.Color;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.openpdf.text.Image;

class PDFTableTest {

    @Test
    void testCreateAndAddCells() {
        PDFTable table = new PDFTable(2);
        table.createLabelCell("L1");
        table.createValueCell("V1");
        table.createLabelCellWithColspan("Label Col", 2);
        table.createValueCellWithRowspan("Val Row", 2);

        // After adding cells, number of rows should be >=1 and total width setting should accept
        // arrays
        table.setTotalWidth(new float[]{100f, 200f});
        assertEquals(2, table.getNumberOfColumns());
    }

    @Test
    void testCreateValueCellWithImage() throws Exception {
        PDFTable table = new PDFTable(2);
        Image image = Image.getInstance(new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB), null);
        table.createValueCell(image);
        table.createValueCell("text", PDFTable.Align.RIGHT);
        table.createValueCellWithColspan("colspan", 2);
        table.createValueCellWithColspan("colspan", 2, PDFTable.Align.LEFT, FontFamily.FONT_7_BLACK_BOLD);
        table.createValueCellWithRowspan("rowspan", 1, PDFTable.Align.RIGHT);
        table.createLabelCellWithRowspan("label", 1);
        table.createLabelCellWithColspan("label", 2, PDFTable.Align.RIGHT);
        table.createLabelCellWithColspanAndRowspan("label", 2, 1);
        assertEquals(2, table.getNumberOfColumns());
        assertEquals(Color.LIGHT_GRAY, table.getRow(table.size() - 1).getCells()[0].getBackgroundColor());
    }

    @Test
    void invalidWidthsAreRejected() {
        PDFTable table = new PDFTable(2);
        assertThrows(IllegalArgumentException.class, () -> table.setTotalWidth(new float[]{100f}));
    }

    @Test
    void testAlignEnumValues() {
        assertEquals(1, PDFTable.Align.CENTER.getValue());
        assertEquals(0, PDFTable.Align.LEFT.getValue());
        assertEquals(2, PDFTable.Align.RIGHT.getValue());
    }
}
