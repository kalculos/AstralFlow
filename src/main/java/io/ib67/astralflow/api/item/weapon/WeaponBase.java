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
import io.ib67.astralflow.api.item.ItemBase;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.factory.ItemPrototypeFactory;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.function.Predicate;

@Getter
public abstract class WeaponBase extends ItemBase {
    private final Predicate<Entity> entitySelector;
    private final WeaponProperty property;
    private final Set<EntityDamageEvent.DamageCause> damageTypes;

    protected WeaponBase(ItemKey id, ItemStack prototype, WeaponProperty property, Predicate<Entity> entitySelector, Set<EntityDamageEvent.DamageCause> types) {
        super(id, prototype);
        this.property = property;
        this.damageTypes = types;
        this.entitySelector = entitySelector;

        HookType.ENTITY_DAMAGE.register(this::onEntityDamage);
    }

    private void onEntityDamage(EntityDamageByEntityEvent event) {
        var damager = event.getDamager();
        if (damager.getType() != EntityType.PLAYER) {
            return;
        }
        if (damageTypes.contains(event.getCause())) {
            var player = (Player) damager;
            var itemInHand = player.getEquipment().getItemInMainHand();
            if (itemInHand.getType() == Material.AIR) {
                return;
            }
            var state = AstralFlow.getInstance().getItemRegistry().getRegistry(itemInHand);
            var isItem = state.map(ItemPrototypeFactory::getHolder)
                    .filter(holder -> holder == this)
                    .isPresent();
            // apply damage.
            if (entitySelector.test(event.getEntity())) {
                var damage = this.property.getDamage();
                if (!property.isClearOriginalDamage()) {
                    damage = damage + event.getFinalDamage();
                }
                if (Math.random() > property.getCriticalChance()) {
                    damage = damage * property.getCriticalMultiplexer();
                }
                event.setDamage(damage);
            }
        }
    }
}
