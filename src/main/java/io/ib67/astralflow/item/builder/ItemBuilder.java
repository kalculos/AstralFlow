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

package io.ib67.astralflow.item.builder;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.item.factory.ItemPrototypeFactory;
import io.ib67.astralflow.item.recipe.AstralRecipe;
import io.ib67.astralflow.texture.Texture;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class ItemBuilder
        <T extends Enum<T>,
                C extends ItemCategory<T, P, S>,
                P extends ItemPrototypeFactory,
                S extends PrototypeSupplier<T>> {
    private final ItemCategory<T, P, S> category;
    private Texture texture;
    private String oreDictId;
    private P registry;
    private List<AstralRecipe> recipes = new ArrayList<>();

    private ItemBuilder(ItemCategory<T, P, S> category) {
        this.category = category;
    }

    public static <T extends Enum<T>,
            P extends ItemPrototypeFactory,
            C extends ItemCategory<T, P, S>,
            S extends PrototypeSupplier<T>>
    ItemBuilder<T, C, P, S> of(@NotNull ItemCategory<T, P, S> category) {
        return new ItemBuilder<>(category);
    }

    @ApiStatus.Experimental
    public ItemBuilder<T, C, P, S> bind(String textureId) {
        this.texture = AstralFlow.getInstance().getTextureRegistry().getTexture(textureId).orElseThrow(); // FIXME: default fallback texture
        return this;
    }

    public ItemBuilder<T, C, P, S> bind(Texture texture) {
        texture.getModelId(); //TODO: @BEFORE_RELEASE@ Unstable behaviour
        // ensure it is valid registered.
        this.texture = texture;
        return this;
    }

    public ItemBuilder<T, C, P, S> oreDict(String oreDictId) {
        this.oreDictId = oreDictId;
        return this;
    }

    public ItemBuilder<T, C, P, S> recipe(AstralRecipe recipe) {
        recipes.add(recipe);
        return this;
    }


    public WrappedBuilder prototype(P prototypeRegistry) {
        this.registry = prototypeRegistry;
        return new WrappedBuilder(this);
    }

    private void register() {
        ItemPrototypeFactory p = registry;
        p = new PrototypeDecorator(p, i -> {
            var item = i.clone();
            var im = item.getItemMeta();
            if (im == null) {
                throw new IllegalStateException("ItemMeta is null or AIR! " + registry.getId());
            }
            im.setCustomModelData(texture.getModelId()); //todo: @BEFORE_RELEASE@ @BREAKING_CHANGE@ Textures should be dynamic generated from the texture registry and updated via packet modification when the texture is changed.
            i.setItemMeta(im);
            return i;
        }, UnaryOperator.identity());
        // process item.
        if (oreDictId != null) {
            var od = AstralFlow.getInstance().getItemRegistry().getOreDict();
            od.registerItem(p, oreDictId);
        }
        //todo register recipes.
    }

    public class WrappedBuilder {
        private final ItemBuilder<T, C, P, S> builder;

        private WrappedBuilder(ItemBuilder<T, C, P, S> builder) {
            this.builder = builder;
        }

        public void register() {
            builder.register();
        }
    }
}
