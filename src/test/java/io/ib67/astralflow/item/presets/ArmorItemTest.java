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

package io.ib67.astralflow.item.presets;

import be.seeseemelk.mockbukkit.MockBukkit;
import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.api.item.armor.ArmorItem;
import io.ib67.astralflow.api.item.armor.ArmorProperty;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.builder.ItemBuilder;
import io.ib67.astralflow.util.ItemStacks;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static io.ib67.astralflow.test.TestUtil.init;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ArmorItemTest {
    @BeforeAll
    public void setup() {
        init();
    }

    @Test
    public void testArmor() {
        var armor = new SimpleArmor(ItemKey.from("testarmor", "a"),
                ItemStacks.builder(Material.DIAMOND_CHESTPLATE).build(),
                new ArmorProperty(Map.of(), 31F));
        ItemBuilder.ofSimpleArmor()
                .prototype(armor)
                .register();
        var item = armor.getId().createNewItem().asItemStack();
        var player = MockBukkit.getMock().addPlayer();
        player.getEquipment().setChestplate(item);
        var event = new EntityDamageEvent(
                player,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                30F
        );
        AstralFlow.getInstance().callHooks(HookType.ENTITY_DAMAGE, event);
        Assertions.assertSame(event.getDamage(), 1F);
    }

    class SimpleArmor extends ArmorItem {

        protected SimpleArmor(ItemKey itemKey, ItemStack prototype, ArmorProperty property) {
            super(itemKey, prototype, property);
        }
    }
}
