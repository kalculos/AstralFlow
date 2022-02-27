/*
 *
 *   AstralFlow - The plugin who is turning bukkit into mod-pack
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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

@SuppressWarnings("unused")
public class RecipeHelperTest {

    @Test
    public void populateEmptyRows() {
        assertArrayEquals("Empty input #1", new String[]{"   ", "   ", "   "}, RecipeHelper.populateEmptyRows(""));
        assertArrayEquals("Empty input #2", new String[]{"   ", "   ", "   "}, RecipeHelper.populateEmptyRows());
        assertArrayEquals("Test Regular Matrix #1",
                new String[]{
                        "   ",
                        "AA ",
                        "B  "
                },
                RecipeHelper.populateEmptyRows(
                        " ",
                        "AA",
                        "B"));
        assertArrayEquals("Test Regular Matrix #2",
                new String[]{
                        "  A",
                        " B ",
                        "C  "
                },
                RecipeHelper.populateEmptyRows(
                        "  A",
                        " B",
                        "C"));
        assertArrayEquals("Test Regular Matrix #3",
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
        assertArrayEquals("Test Regular Matrix #4",
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
        assertArrayEquals(
                "Test Regular 3 ROW Matrix #1",
                new String[]{
                        "AA",
                        "BB",
                        "CC"
                },
                RecipeHelper.leftAndUpAlignMatrix(" AA",
                        " BB",
                        " CC")
        );
        assertArrayEquals(
                "Test Regular 2 ROW Matrix #2",
                new String[]{
                        "AA",
                        "BB",
                },
                RecipeHelper.leftAndUpAlignMatrix(" AA",
                        " BB")
        );
        assertArrayEquals(
                "Test Regular 2 ROW Matrix #3",
                new String[]{
                        "A",
                        "B",
                },
                RecipeHelper.leftAndUpAlignMatrix(" A",
                        " B")
        );
        assertArrayEquals(
                "Test Regular 3 Row Matrix #4",
                new String[]{
                        "A  ",
                        "B  ",
                        " C "
                },
                RecipeHelper.leftAndUpAlignMatrix(
                        "A  ",
                        "B  ",
                        " C "
                )
        );
        assertArrayEquals(
                "Test Regular 3 Row Matrix #5",
                new String[]{
                        "A ",
                        " B",
                        "C "
                },
                RecipeHelper.leftAndUpAlignMatrix(
                        " A ",
                        "  B",
                        " C "
                )
        );
        Assert.assertThrows("Test Exceptional ElementMoreThanThree Matrix #6", ArrayIndexOutOfBoundsException.class, () -> RecipeHelper.populateEmptyRows("", "", "", ""));

        assertArrayEquals(
                "Test Regular EMPTY Matrix #7",
                new String[]{
                        "", "", ""
                },
                RecipeHelper.leftAndUpAlignMatrix("", "", "")
        );
    }

    @Test
    public void generateMatrixPatternHash() {
        var matrixA = new String[]{
                "A  ",
                "B  ",
                "C C"
        };
        var matrixB = new String[]{
                "c  ",
                "a  ",
                "D D"
        };
        Assert.assertEquals(RecipeHelper.generateMatrixPatternHash(matrixA), RecipeHelper.generateMatrixPatternHash(matrixB));

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
        assertArrayEquals("Test Matrix Up Align", matrixB, mc);
        var pop = RecipeHelper.populateEmptyRows(mc);
        var matrixD = new String[]{
                "A  ",
                "B  ",
                "   "
        };
        assertArrayEquals("Test Matrix Up Align AND POPULATE", matrixD, pop);

        var d = new String[]{
                " A ",
                " B "
        };
        var v = new String[]{
                "A ",
                "B "
        };
        assertArrayEquals("Text Matrix up align", v, RecipeHelper.leftAndUpAlignMatrix(d));
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
                "Test Regular Matrix #1",
                new String[]{
                        "aaa",
                        " b ",
                        "aaa"
                },
                RecipeHelper.toStringMatrix(itemMatrix)
        );
        ItemStack[] itemMatrix2 = new ItemStack[]{
                new ItemStack(Material.STONE)
        };
        assertArrayEquals(
                "Test Regular Matrix #2",
                new String[]{
                        "a"
                },
                RecipeHelper.toStringMatrix(itemMatrix2)
        );
        ItemStack[] itemMatrix3 = new ItemStack[]{
                null, null, null,
                null, new ItemStack(Material.STONE), null,
                null, null, null
        };
        assertArrayEquals(
                "Test Regular Matrix #3",
                new String[]{
                        "   ",
                        " a ",
                        "   "
                },
                RecipeHelper.toStringMatrix(itemMatrix3)
        );


    }
}