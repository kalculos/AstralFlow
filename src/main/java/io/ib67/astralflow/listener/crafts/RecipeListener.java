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

package io.ib67.astralflow.listener.crafts;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.internal.RecipeHelper;
import io.ib67.astralflow.item.recipe.IRecipeRegistry;
import io.ib67.util.bukkit.Log;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import java.util.Arrays;

@RequiredArgsConstructor
public class RecipeListener implements Listener {
    private final IRecipeRegistry recipeRegistry;

    @EventHandler(priority = EventPriority.LOW)
    public void onCraftPrepare(PrepareItemCraftEvent event) {
        boolean override = AstralFlow.getInstance().getSettings().getRecipeSetting().isOverrideVanillaRecipe();
        if (event.getRecipe() != null && !override) {
            return;
        }
        var matrix = event.getInventory().getMatrix();
        Log.warn(Arrays.toString(matrix));
        var recipe = recipeRegistry.matchRecipe(RecipeHelper.leftAlignMatrixItems(matrix));
        if (recipe != null) {
            var modifiedMatrix = recipe.apply(matrix);
            event.getInventory().setResult(recipe.produceResult());
            event.getInventory().setMatrix(modifiedMatrix);
        }
    }
}
