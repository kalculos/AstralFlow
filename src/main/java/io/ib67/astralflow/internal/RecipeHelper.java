/*
 *
 *   AstralFlow - The plugin enriches bukkit servers
 *   Copyright (C) 2022 The Inlined Lambdas and Contributors
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

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ApiStatus.Internal
public final class RecipeHelper {
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
                    rowBuilder.append(" ".repeat(3 - row.length()));
                }
                realMatrix[i] = rowBuilder.toString();
            } else {
                realMatrix[i] = "   ";
            }
        }
        return realMatrix;
    }

    public static ItemStack[] leftAlignMatrixItems(ItemStack... _matrix) {
        //FIXME: looking for a better way to do this.
        var stringMatrix = toStringMatrix(_matrix);
        var map = new HashMap<Character, ItemStack>();
        for (int a = 0; a < stringMatrix.length; a++) {
            var chars = stringMatrix[a].toCharArray();
            for (int i = 0; i < chars.length; i++) {
                var item = _matrix[a * 3 + i];
                map.put(chars[i], item);
            }
        }
        var newMatrix = new ItemStack[9];
        var alignedMatrix = leftAndUpAlignMatrix(stringMatrix);
        for (int i = 0; i < alignedMatrix.length; i++) {
            var chars = alignedMatrix[i].toCharArray();
            for (int q = 0; q < chars.length; q++) {
                newMatrix[i * 3 + q] = Optional.ofNullable(map.get(chars[q])).map(ItemStack::clone).orElse(null);
            }
        }
        return newMatrix;
    }

    public static String[] leftAndUpAlignMatrix(String... matrix) {
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
        String[] newMatrix = new String[matrix.length];
        if (leftOffset != 0) {
            assert leftOffset != 4;
            for (int i = 0; i < matrix.length; i++) {
                if (leftOffset < matrix.length) {
                    newMatrix[i] = matrix[i].substring(leftOffset);
                }
            }
        } else {
            newMatrix = matrix;
        }
        // check all null
        if (Arrays.stream(newMatrix).allMatch(Objects::isNull)) return new String[0];

        // up
        int upOffset = 0;
        for (String s : newMatrix) {
            if (s == null || !s.trim().isEmpty()) {
                break;
            }
            upOffset++;
        }
        if (upOffset == 0) {
            return newMatrix;
        }
        newMatrix = Arrays.copyOfRange(newMatrix, upOffset, newMatrix.length);

        return newMatrix;
    }

    public static String[] toStringMatrix(ItemStack... items) {
        int base = 'a';
        var set = new ArrayList<>(8);
        char[] matrix = new char[items.length];
        for (int i = 0; i < items.length; i++) {
            var item = items[i];
            if (item == null) {
                matrix[i] = ' ';
                continue;
            }
            if (!set.contains(item.getType())) {
                set.add(item.getType());
            }
            var index = set.indexOf(item.getType());
            matrix[i] = (char) (base + index);
        }
        var list = new ArrayList<String>();
        var sb = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            sb.append(matrix[i]);
            if ((i + 1) % 3 == 0) {
                list.add(sb.toString());
                sb = new StringBuilder();
            }
        }
        if (sb.length() != 0) {
            list.add(sb.toString());
        }
        return list.toArray(new String[0]);
    }

    public static ItemStack[] populateEmptyRows(ItemStack... tran) {
        if (tran.length >= 9) {
            return tran;
        }
        var newTran = new ItemStack[9];
        for (int i = 0; i < tran.length; i++) {
            newTran[i] = tran[i];
        }
        return newTran;
    }
}
