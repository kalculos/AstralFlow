/*
 *
 *   AstralFlow - The plugin who is turning bukkit into mod-pack
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

package io.ib67.astralflow.api;

import io.ib67.astralflow.config.AstralFlowConfiguration;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.hook.event.HookEvent;
import io.ib67.astralflow.item.recipe.IRecipeRegistry;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.manager.IMachineManager;
import io.ib67.astralflow.manager.ITickManager;
import io.ib67.astralflow.manager.ItemRegistry;
import io.ib67.astralflow.texture.ITextureRegistry;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.function.Consumer;

public interface AstralFlowAPI {
    IMachineManager getMachineManager();

    IFactoryManager getFactories();

    ItemRegistry getItemRegistry();

    ITickManager getTickManager();

    ITextureRegistry getTextureRegistry();

    AstralFlowConfiguration getSettings();

    IRecipeRegistry getRecipeRegistry();

    <T extends HookEvent> void addHook(HookType<T> type, Runnable runnable);

    <T extends HookEvent> void addHook(HookType<T> type, Consumer<T> runnable);

    <T extends HookEvent> Collection<? extends Consumer<T>> getHooks(HookType<T> hook);

    default Plugin asPlugin() {
        return (Plugin) this;
    }
}
