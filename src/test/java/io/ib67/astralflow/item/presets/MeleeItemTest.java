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
import io.ib67.astralflow.api.item.weapon.MeleeItem;
import io.ib67.astralflow.api.item.weapon.WeaponCategory;
import io.ib67.astralflow.api.item.weapon.WeaponProperty;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.builder.ItemBuilder;
import io.ib67.astralflow.util.ItemStacks;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemFlag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Objects;

import static io.ib67.astralflow.test.TestUtil.init;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author EvanLuo42
 * @date 5/3/22 6:47 PM
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MeleeItemTest {
    @BeforeAll
    public void setup()  {
        init();
    }

    @Test
    public void testMeleeItem() {
        var itemKey = ItemKey.from("test", "test_melee");

        var protoType = ItemStacks.builder(Material.getMaterial("DIAMOND_SWORD"))
                .customModelId(1)
                .itemFlags(ItemFlag.HIDE_DYE)
                .displayName("test_melee")
                .build();

        var property = WeaponProperty.builder()
                .clearOriginalDamage(true)
                .damage(999)
                .criticalChance(0.1)
                .criticalMultiplexer(1.5)
                .build();

        var meleeItem = MeleeItem.builder()
                .id(itemKey)
                .prototype(protoType)
                .property(property)
                .build();

        ItemBuilder.of(WeaponCategory.INSTANCE)
                .prototype(meleeItem)
                .register();

        var player = MockBukkit.mock().addPlayer();

        Objects.requireNonNull(player.getEquipment()).setItemInMainHand(meleeItem.getId().createNewItem().asItemStack());
        var event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1);
        AstralFlow.getInstance().callHooks(HookType.ENTITY_DAMAGE, event);
        assertSame(event.getDamage(), 999);
    }
}
