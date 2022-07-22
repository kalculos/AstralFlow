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

package io.ib67.astralflow.item;

import io.ib67.astralflow.api.AstralHelper;
import io.ib67.astralflow.api.item.ItemBase;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.util.ItemStacks;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * A simple item demonstrating how we create custom items.
 * Actually, AstralFlow provides a lot of pre-sets of items, like {@link io.ib67.astralflow.api.item.weapon.WeaponBase} and {@link io.ib67.astralflow.api.item.weapon.MeleeItem} and you're not supposed to create your own at most time.
 */
public class ExCalibur extends ItemBase { // it depends on your item category.

    public ExCalibur(ItemKey itemKey) {
        super(itemKey, ItemStacks.of(Material.STONE_SWORD, "&bEx. &6Calibur", "A powerful sword"));
        HookType.ENTITY_DAMAGE_BY_ENTITY.register(this::onEntityDamageEntity); // register a listener for that event.
    }

    private void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        // just like regular bukkit plugins.
        if (event.getEntity() instanceof LivingEntity le && le.getEquipment() != null) {
            if (AstralHelper.isHolder(le.getEquipment().getItemInMainHand(), this)) {
                event.setDamage(1000);

                //that's all. you can get this item by simply calling itemKey#createNewItem()
            }
        }
    }

}
