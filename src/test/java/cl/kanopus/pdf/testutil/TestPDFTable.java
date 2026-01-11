/*-
 * !--
 * For support and inquiries regarding this library, please contact:
 *   soporte@kanopus.cl
 *
 * Project website:
 *   https://www.kanopus.cl
 * %%
 * Copyright (C) 2025 - 2026 Pablo Díaz Saavedra
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
package cl.kanopus.pdf.testutil;

import cl.kanopus.pdf.normal.table.PDFTable;

public class TestPDFTable extends PDFTable {

    private final float totalHeight;

    public TestPDFTable(int numColumns, float totalHeight) {
        super(numColumns);
        this.totalHeight = totalHeight;
        // Set default total widths to avoid iText runtime exception "table width must be greater than zero"
        float[] widths = new float[numColumns];
        for (int i = 0; i < numColumns; i++) widths[i] = 100f;
        this.setTotalWidth(widths);
        this.setLockedWidth(true);
    }

    @Override
    public float getTotalHeight() {
        return totalHeight;
    }
}
