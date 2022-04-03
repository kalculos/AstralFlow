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

package io.ib67.astralflow;

import io.ib67.astralflow.api.external.AstralExtension;
import io.ib67.astralflow.api.external.ExtensionInfo;
import io.ib67.astralflow.api.item.MachineCategory;
import io.ib67.astralflow.api.item.MachineItem;
import io.ib67.astralflow.item.SimpleStatefulCategory;
import io.ib67.astralflow.item.SimpleStatelessCategory;
import io.ib67.astralflow.item.builder.ItemBuilder;
import io.ib67.astralflow.item.recipe.choices.MaterialChoice;
import io.ib67.astralflow.item.recipe.kind.Shaped;
import io.ib67.astralflow.machines.JebWool;
import io.ib67.astralflow.util.ItemStacks;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public class TestModule extends AstralExtension {
    private final NamespacedKey jebWoolKey = new NamespacedKey("tester", "jeb_wool");

    public TestModule() {
        super(ExtensionInfo.builder()
                .extensionAuthors(new String[]{"iceBear67"})
                .extensionName("TestModule")
                .extensionVersion("0.0.1")
                .issueTrackerUrl("https://github.com/iceBear67/AstralFlow/issues")
                .extensionDescription("A module for testing purposes")
                .build()
        );
    }

    @Override
    public void init() {
        // register items.
        ItemBuilder.of(MachineCategory.INSTANCE)
                .oreDict("wool")
                .recipe(Shaped.of(jebWoolKey)
                        .shape("AAA", "A A", "AAA")
                        .setIngredient('A', new MaterialChoice(Material.BLACK_WOOL, Material.WHITE_WOOL))
                        .build()
                ).prototype(new MachineItem(
                        TestItems.JEB_WOOL,
                        ItemStacks.builder(Material.WHITE_WOOL)
                                .displayName("&aJeb Wool!")
                                .lore("&b Such a colorful woooooool")
                                .build(),
                        JebWool.class
                ))
                .register();
        ItemBuilder.of(new SimpleStatelessCategory())
                .prototype(TestItems.STATELESS_ITEM)
                .register();
        ItemBuilder.of(new SimpleStatefulCategory())
                .prototype(TestItems.STATEFUL_ITEM)
                .register();
    }
}
