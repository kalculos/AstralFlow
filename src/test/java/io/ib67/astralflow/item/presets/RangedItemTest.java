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
import io.ib67.astralflow.api.item.weapon.RangedItem;
import io.ib67.astralflow.api.item.weapon.WeaponProperty;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.builder.ItemBuilder;
import io.ib67.astralflow.util.ItemStacks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.ib67.astralflow.test.TestUtil.init;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RangedItemTest {
    @BeforeAll
    public void setup() {
        init();
    }

    @Test
    public void testNormal() {
        var itemKey = ItemKey.from("rangedtest", "a");
        var ranged = RangedItem.builder()
                .id(itemKey)
                .property(WeaponProperty
                        .builder()
                        .clearOriginalDamage(true)
                        .damage(999)
                        .criticalChance(0)
                        .build())
                .prototype(ItemStacks.builder(Material.BOW).build())
                .entitySelector(t -> true)
                .build();
        ItemBuilder.ofSimpleWeapon()
                .prototype(ranged)
                .register();
        var item = itemKey.createNewItem();
        var world = MockBukkit.getMock().addSimpleWorld("rangedNormal");
        var arrow = (Arrow) world.spawnEntity(new Location(world, 0, 0, 0), EntityType.ARROW); // Arrow is not implemented yet.
        var player = MockBukkit.getMock().addPlayer();
        player.setHealth(0);
        arrow.setShooter(player);
        player.setItemInHand(item.asItemStack());
        var event = new ProjectileHitEvent(
                arrow,
                player
        );
        AstralFlow.getInstance().callHooks(HookType.PROJECTILE_HIT, event);
        Assertions.assertSame(player.getHealth(), -999);
    }
}
