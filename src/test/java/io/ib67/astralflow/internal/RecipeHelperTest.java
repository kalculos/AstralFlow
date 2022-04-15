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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.ib67.astralflow.test.TestUtil.init;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("unused")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class RecipeHelperTest {

    @BeforeAll
    public void setup() {
        init();
    }

    @Test
    public void populateEmptyRows() {
        assertArrayEquals(new String[]{"   ", "   ", "   "}, RecipeHelper.populateEmptyRows(""), "Empty input #1");
        assertArrayEquals(new String[]{"   ", "   ", "   "}, RecipeHelper.populateEmptyRows(), "Empty input #2");
        assertArrayEquals(
                new String[]{
                        "   ",
                        "AA ",
                        "B  "
                },
                RecipeHelper.populateEmptyRows(
                        " ",
                        "AA",
                        "B"),
                "Test Regular Matrix #1");
        assertArrayEquals(
                new String[]{
                        "  A",
                        " B ",
                        "C  "
                },
                RecipeHelper.populateEmptyRows(
                        "  A",
                        " B",
                        "C"), "Test Regular Matrix #2");
        assertArrayEquals(
                new String[]{
                        "AAA",
                        "BBB",
                        "CCC"
                },
                RecipeHelper.populateEmptyRows(
                        "AAA",
                        "BBB",
                        "CCC")
                , "Test Regular Matrix #3");
        assertArrayEquals(
                new String[]{
                        "   ",
                        "   ",
                        "   "
                },
                RecipeHelper.populateEmptyRows(
                        "   ",
                        "   ",
                        "   ")
                , "Test Regular Matrix #4");
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> RecipeHelper
                .populateEmptyRows("aaa", "aaa", "aaa", "aaa"), "Test Exceptional OutOfBound Matrix #4");

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> RecipeHelper
                .populateEmptyRows("aaa", "aaa", "aaac"), "Test Exceptional ElementMoreThanThree Matrix #4");
    }

    @Test
    public void leftAlignMatrix() {
        assertArrayEquals(
                new String[]{
                        "AA",
                        "BB",
                        "CC"
                },
                RecipeHelper.leftAndUpAlignMatrix(" AA",
                        " BB",
                        " CC"),
                "Test Regular 3 ROW Matrix #1"
        );
        assertArrayEquals(
                new String[]{
                        "AA",
                        "BB",
                },
                RecipeHelper.leftAndUpAlignMatrix(" AA",
                        " BB")
                , "Test Regular 2 ROW Matrix #2"
        );
        assertArrayEquals(

                new String[]{
                        "A",
                        "B",
                },
                RecipeHelper.leftAndUpAlignMatrix(" A",
                        " B"),
                "Test Regular 2 ROW Matrix #3"
        );
        assertArrayEquals(
                new String[]{
                        "A  ",
                        "B  ",
                        " C "
                },
                RecipeHelper.leftAndUpAlignMatrix(
                        "A  ",
                        "B  ",
                        " C "
                ),
                "Test Regular 3 Row Matrix #4"
        );
        assertArrayEquals(
                new String[]{
                        "A ",
                        " B",
                        "C "
                },
                RecipeHelper.leftAndUpAlignMatrix(
                        " A ",
                        "  B",
                        " C "
                ),
                "Test Regular 3 Row Matrix #5"
        );
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> RecipeHelper.populateEmptyRows("", "", "", ""), "Test Exceptional ElementMoreThanThree Matrix #6");

        assertArrayEquals(
                new String[]{
                },
                RecipeHelper.leftAndUpAlignMatrix("", "", ""),
                "Test Regular EMPTY Matrix #7"
        );
    }

    @Test
    public void testMatrixUp() {
        var matrixA = new String[]{
                "   ",
                " A ",
                " B "
        };
        var matrixB = new String[]{
                "A ",
                "B "
        };
        var mc = RecipeHelper.leftAndUpAlignMatrix(matrixA);
        assertArrayEquals(matrixB, mc, "Test Matrix Up Align");
        var pop = RecipeHelper.populateEmptyRows(mc);
        var matrixD = new String[]{
                "A  ",
                "B  ",
                "   "
        };
        assertArrayEquals(matrixD, pop, "Test Matrix Up Align AND POPULATE");

        var d = new String[]{
                " A ",
                " B "
        };
        var v = new String[]{
                "A ",
                "B "
        };
        assertArrayEquals(v, RecipeHelper.leftAndUpAlignMatrix(d), "Text Matrix up align");
    }
    @Test
    public void toStringMatrix() {
        ItemStack[] itemMatrix = new ItemStack[]{
                new ItemStack(Material.STONE),
                new ItemStack(Material.STONE),
                new ItemStack(Material.STONE),
                null,
                new ItemStack(Material.STICK),
                null,
                new ItemStack(Material.STONE),
                new ItemStack(Material.STONE),
                new ItemStack(Material.STONE)
        };
        assertArrayEquals(
                new String[]{
                        "aaa",
                        " b ",
                        "aaa"
                },
                RecipeHelper.toStringMatrix(itemMatrix),
                "Test Regular Matrix #1"
        );
        ItemStack[] itemMatrix2 = new ItemStack[]{
                new ItemStack(Material.STONE)
        };
        assertArrayEquals(
                new String[]{
                        "a"
                },
                RecipeHelper.toStringMatrix(itemMatrix2),
                "Test Regular Matrix #2"
        );
        ItemStack[] itemMatrix3 = new ItemStack[]{
                null, null, null,
                null, new ItemStack(Material.STONE), null,
                null, null, null
        };
        assertArrayEquals(
                new String[]{
                        "   ",
                        " a ",
                        "   "
                },
                RecipeHelper.toStringMatrix(itemMatrix3),
                "Test Regular Matrix #3"
        );


    }
}