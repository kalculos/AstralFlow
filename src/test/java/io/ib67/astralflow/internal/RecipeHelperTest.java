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

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("unused")
public class RecipeHelperTest {

    @Test
    public void populateEmptyRows() {
        Assert.assertArrayEquals("Empty input #1", new String[]{"   ", "   ", "   "}, RecipeHelper.populateEmptyRows(""));
        Assert.assertArrayEquals("Empty input #2", new String[]{"   ", "   ", "   "}, RecipeHelper.populateEmptyRows());
        Assert.assertArrayEquals("Test Regular Matrix #1",
                new String[]{
                        "   ",
                        "AA ",
                        "B  "
                },
                RecipeHelper.populateEmptyRows(
                        " ",
                        "AA",
                        "B"));
        Assert.assertArrayEquals("Test Regular Matrix #2",
                new String[]{
                        "  A",
                        " B ",
                        "C  "
                },
                RecipeHelper.populateEmptyRows(
                        "  A",
                        " B",
                        "C"));
        Assert.assertArrayEquals("Test Regular Matrix #3",
                new String[]{
                        "AAA",
                        "BBB",
                        "CCC"
                },
                RecipeHelper.populateEmptyRows(
                        "AAA",
                        "BBB",
                        "CCC")
        );
        Assert.assertArrayEquals("Test Regular Matrix #4",
                new String[]{
                        "   ",
                        "   ",
                        "   "
                },
                RecipeHelper.populateEmptyRows(
                        "   ",
                        "   ",
                        "   ")
        );
        Assert.assertThrows("Test Exceptional OutOfBound Matrix #4", ArrayIndexOutOfBoundsException.class, () -> RecipeHelper
                .populateEmptyRows("aaa", "aaa", "aaa", "aaa"));

        Assert.assertThrows("Test Exceptional ElementMoreThanThree Matrix #4", ArrayIndexOutOfBoundsException.class, () -> RecipeHelper
                .populateEmptyRows("aaa", "aaa", "aaac"));
    }

    @Test
    public void leftAlignMatrix() {
        Assert.assertArrayEquals(
                "Test Regular 3 ROW Matrix #1",
                new String[]{
                        "AA",
                        "BB",
                        "CC"
                },
                RecipeHelper.leftAlignMatrix(" AA",
                        " BB",
                        " CC")
        );
        Assert.assertArrayEquals(
                "Test Regular 2 ROW Matrix #2",
                new String[]{
                        "AA",
                        "BB",
                },
                RecipeHelper.leftAlignMatrix(" AA",
                        " BB")
        );
        Assert.assertArrayEquals(
                "Test Regular 2 ROW Matrix #3",
                new String[]{
                        "A",
                        "B",
                },
                RecipeHelper.leftAlignMatrix(" A",
                        " B")
        );
        Assert.assertArrayEquals(
                "Test Regular 3 Row Matrix #4",
                new String[]{
                        "A  ",
                        "B  ",
                        " C "
                },
                RecipeHelper.leftAlignMatrix(
                        "A  ",
                        "B  ",
                        " C "
                )
        );
        Assert.assertArrayEquals(
                "Test Regular 3 Row Matrix #5",
                new String[]{
                        "A ",
                        " B",
                        "C "
                },
                RecipeHelper.leftAlignMatrix(
                        " A ",
                        "  B",
                        " C "
                )
        );
        Assert.assertThrows("Test Exceptional ElementMoreThanThree Matrix #6", ArrayIndexOutOfBoundsException.class, () -> RecipeHelper.populateEmptyRows("", "", "", ""));

        Assert.assertArrayEquals(
                "Test Regular EMPTY Matrix #7",
                new String[]{
                        "", "", ""
                },
                RecipeHelper.leftAlignMatrix("", "", "")
        );
    }

    @Test
    public void generateMatrixPatternHash() {
        System.out.println(RecipeHelper.generateMatrixPatternHash(
                "aaa",
                " b ",
                "aaa"
        ));
    }
}