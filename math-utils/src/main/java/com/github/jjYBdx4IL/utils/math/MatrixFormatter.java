/*
 * Copyright © 2017 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.utils.math;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class MatrixFormatter {

    /**
     * No localization.
     *
     * @param title optional, default (null) is none
     * @param matrix matrix[row][column]
     * @param lineSeparator optional, default (null) is system dependent
     * @return the string representation of the digest
     */
    public static String format(String title, int[][] matrix, String lineSeparator) {
        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException("empty matrix");
        }

        String lf = lineSeparator;
        if (lf == null) {
            lf = System.lineSeparator();
        }
        StringBuilder sb = new StringBuilder();
        if (title != null) {
            sb.append(title).append(lf);
        }
        // get maximum width of each column
        int[] colWidths = new int[matrix[0].length];
        for (int[] row : matrix) {
            for (int colIdx = 0; colIdx < row.length; colIdx++) {
                int printedLength = Integer.toString(row[colIdx]).length();
                if (printedLength > colWidths[colIdx]) {
                    colWidths[colIdx] = printedLength;
                }
            }
        }
        for (int[] row : matrix) {
            for (int colIdx = 0; colIdx < row.length; colIdx++) {
                sb.append(String.format("%" + (colWidths[colIdx] + (colIdx == 0 ? 0 : 1) ) + "s", Integer.toString(row[colIdx])));
            }
            sb.append(lf);
        }

        return sb.toString();
    }

    private MatrixFormatter() {
    }
}
