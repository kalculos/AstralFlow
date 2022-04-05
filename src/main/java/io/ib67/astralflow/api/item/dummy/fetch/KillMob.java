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

package io.ib67.astralflow.api.item.dummy.fetch;

import io.ib67.astralflow.api.item.dummy.FetchMethod;
import io.ib67.astralflow.hook.HookType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * A way to fetch {@link io.ib67.astralflow.api.item.dummy.DummyItem}s from a {@link LivingEntity}'s death
 */
@ApiStatus.AvailableSince("0.1.0")
public final class KillMob implements FetchMethod {
    private final Predicate<LivingEntity> entitySelector;
    private Supplier<ItemStack> producer;

    public KillMob(Predicate<LivingEntity> entitySelector) {
        requireNonNull(entitySelector);
        this.entitySelector = entitySelector;
    }

    @Override
    public void init(Supplier<ItemStack> producer) {
        this.producer = producer;
        HookType.ENTITY_DEATH.register(this::onEntityDeath);
    }

    private void onEntityDeath(EntityDeathEvent event) {
        if (entitySelector.test(event.getEntity())) {
            event.getDrops().add(producer.get());
        }
    }
}
