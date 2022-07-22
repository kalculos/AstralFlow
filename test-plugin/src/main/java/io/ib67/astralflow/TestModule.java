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
import io.ib67.astralflow.api.item.machine.MachineItem;
import io.ib67.astralflow.api.item.weapon.MeleeItem;
import io.ib67.astralflow.api.item.weapon.WeaponProperty;
import io.ib67.astralflow.item.*;
import io.ib67.astralflow.item.recipe.kind.Shaped;
import io.ib67.astralflow.item.recipe.kind.Shapeless;
import io.ib67.astralflow.machines.InteractiveBarrel;
import io.ib67.astralflow.machines.JebWool;
import io.ib67.astralflow.util.ItemStacks;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public final class TestModule extends AstralExtension {
    private final NamespacedKey jebWoolKey = new NamespacedKey("tester", "jeb_wool");

    public TestModule() {
        super(ExtensionInfo.builder() // describe your extension.
                .extensionAuthors(new String[]{"iceBear67"})
                .extensionName("TestModule")
                .extensionVersion("0.0.1")
                .issueTrackerUrl("https://github.com/iceBear67/AstralFlow/issues")
                .extensionDescription("A module for testing purposes")
                .build()
        );
        registerThis();
    }

    @Override
    public void init() {
        // register items.
        itemMachine() // register a machine item. A machine item, just like block item in forge, can be used to place a machine. ItemBuilder<MachineCategory,MachineItem>
                .oreDict("wool") // oredict id, can be called for multiple times to add more IDs
                .recipe(Shaped.of(jebWoolKey)
                        .shape("AAA", "A A", "AAA") // the shape of item. like bukkit's recipe API
                        .setIngredient('A', materialChoice(Material.BLACK_WOOL, Material.WHITE_WOOL))
                        .build()
                ).prototype(new MachineItem( // The "prototype" of your item, where you handle events from players who are using your items
                        TestItems.JEB_WOOL, // A ItemKey.
                        ItemStacks.builder(Material.WHITE_WOOL) // Our utility
                                .displayName("&aJeb Wool!")
                                .lore("&b Such a colorful woooooool")
                                .build(),
                        JebWool.class // Class of your Machine. We'll create it later (via IMachineFactory<M>)
                ))
                .register();
        item(new SimpleStatelessCategory()) // You can define it's category by yourself. A category specifies the type of prototype. It can be used to make API typesafe.
                .prototype(TestItems.STATELESS_ITEM)
                .register();
        item(new SimpleStatefulCategory())
                .prototype(TestItems.STATEFUL_ITEM)
                .register();
        itemMachine()
                .prototype(
                        new MachineItem(
                                TestItems.INTERACTIVE_BARREL,
                                ItemStacks.builder(Material.BARREL)
                                        .displayName("Explosing Barrel!")
                                        .build(),
                                InteractiveBarrel.class
                        )
                ).register();
        item(new SimpleStatelessUnstackableCategory())
                .recipe(Shapeless.of(TestItems.CANT_STACK.toNamespacedKey(), null)
                        .addIngredients(materialChoice(Material.GOLD_INGOT))
                        .build()
                )
                .prototype(TestItems.CANT_STACK)
                .register();

        // an example for custom item logics. See item.ExCalibur
        item().prototype(new ExCalibur(TestItems.EX_CALIBUR)).register();

        // ... which is equal to:
        itemSimpleWeapon().prototype(
                MeleeItem.builder()
                        .prototype(ItemStacks.of(Material.GOLDEN_SWORD, "name", "lores", "lores"))
                        .id(ItemKey.from("tester", "anotherExcalibur"))
                        .property(WeaponProperty.builder()
                                .criticalChance(0.6)
                                .damage(1000)
                                .clearOriginalDamage(true)
                                .build())
                        .entitySelector(entity -> true)
                        .build()
        ).register();
    }
}
