/*
 *
 *   AstralFlow - Storage utilities for spigot servers.
 *   Copyright (C) 2022 iceBear67
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *   USA
 */

package io.ib67.astralflow.internal;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@ApiStatus.Internal
public class RecipeHelper {
    public static String[] populateEmptyRows(@NotNull String... matrix) {
        if (matrix.length > 3) {
            throw new ArrayIndexOutOfBoundsException("Shaped recipes can only have 3 rows.");
        }
        String[] realMatrix = new String[3];
        // populate the empty rows.
        for (int i = 0; i < realMatrix.length; i++) {
            if (matrix.length - 1 >= i) {
                String row = matrix[i];
                StringBuilder rowBuilder = new StringBuilder(matrix[i]);
                if (row.length() == 0) {
                    realMatrix[i] = "   "; // special case for empty
                    continue;
                }
                if (row.length() > 3) {
                    throw new ArrayIndexOutOfBoundsException("Row " + row + " is too long.(" + i + " chars), except <=3");
                }
                if (row.length() < 3) {
                    for (int i1 = 0; i1 < 3 - row.length(); i1++) {
                        rowBuilder.append(" ");
                    }
                }
                realMatrix[i] = rowBuilder.toString();
            } else {
                realMatrix[i] = "   ";
            }
        }
        return realMatrix;
    }

    public static String[] leftAlignMatrix(String... matrix) {
        //String[] populatedMatrix = populateEmptyRows(matrix);
        int leftOffset;
        if (matrix.length > 3) {
            throw new ArrayIndexOutOfBoundsException("Shaped recipes can only have 3 rows max.");
        }
        if (matrix.length == 0) {
            return matrix;
        }
        int[] lengths = new int[]{4, 4, 4};
        for (int i = 0; i < matrix.length; i++) {
            var s = matrix[i];
            if (s.length() > 3) {
                throw new ArrayIndexOutOfBoundsException("Row " + s + " is too long.(" + i + " chars), except <=3");
            }
            int count = 0;
            for (int i1 = 0; i1 < s.length(); i1++) {
                if (s.charAt(i1) != ' ') {
                    break;
                }
                count++;
            }
            lengths[i] = count;
        }
        Arrays.sort(lengths);
        leftOffset = lengths[0];
        if (leftOffset == 0) {
            return matrix;
        }
        assert leftOffset != 4;
        String[] newMatrix = new String[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            if (leftOffset < matrix.length) {
                newMatrix[i] = matrix[i].substring(leftOffset);
            }
        }
        return newMatrix;
    }

}
