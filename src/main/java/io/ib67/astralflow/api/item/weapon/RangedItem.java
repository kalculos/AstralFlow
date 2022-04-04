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

package io.ib67.astralflow.api.item.weapon;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.item.ItemKey;
import lombok.Builder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.function.Predicate;

/**
 * For weapons like bow.
 */
@ApiStatus.AvailableSince("0.1.0")
public class RangedItem extends WeaponBase {
    @Builder
    protected RangedItem(ItemKey id, ItemStack prototype, WeaponProperty property, Predicate<Entity> entitySelector) {
        super(id, prototype, property, entitySelector, Collections.emptySet());
        HookType.PROJECTILE_HIT.register(this::onProjHit);
    }

    private void onProjHit(ProjectileHitEvent event) {
        if (event.getHitEntity() == null || !(event.getHitEntity() instanceof LivingEntity victim)) {
            return;
        }
        if (getEntitySelector().test(victim)) {
            var shooter0 = event.getEntity().getShooter();
            if (!(shooter0 instanceof Player shooter)) {
                return;
            }
            var item = shooter.getEquipment().getItemInMainHand();
            var isItem = AstralFlow.getInstance().getItemRegistry().getRegistry(item).filter(e -> e.getHolder() == this).isPresent();
            if (!isItem) {
                return;
            }
            // calc
            event.setCancelled(true);
            victim.damage(damageCalc(victim, 0), shooter);
        }
    }
}
