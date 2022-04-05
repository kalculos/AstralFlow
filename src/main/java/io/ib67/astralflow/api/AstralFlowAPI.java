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

package io.ib67.astralflow.api;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.config.AstralFlowConfiguration;
import io.ib67.astralflow.extension.IExtensionRegistry;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.item.recipe.IRecipeRegistry;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.manager.IMachineManager;
import io.ib67.astralflow.manager.ITickManager;
import io.ib67.astralflow.manager.ItemRegistry;
import io.ib67.astralflow.texture.ITextureRegistry;
import io.ib67.astralflow.wireless.registry.IWirelessRegistry;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * The OPEN Api from AstralFlow.
 * You can get a reference to this api by using {@link AstralFlow#getInstance()}
 */
@ApiStatus.AvailableSince("0.1.0")
public interface AstralFlowAPI {
    /**
     * @return the {@link IMachineManager}
     */
    IMachineManager getMachineManager();

    /**
     * @return the {@link IFactoryManager}
     */
    IFactoryManager getFactories();

    /**
     * @return the {@link ItemRegistry}
     */
    ItemRegistry getItemRegistry();

    /**
     * @return the {@link ITickManager}
     */
    ITickManager getTickManager();

    /**
     * @return the {@link IWirelessRegistry}
     */
    IWirelessRegistry getWirelessRegistry();

    /**
     * @return the {@link ITextureRegistry}
     */
    @ApiStatus.Experimental
    ITextureRegistry getTextureRegistry();

    /**
     * @return the {@link AstralFlowConfiguration}
     */
    AstralFlowConfiguration getSettings();

    /**
     * @return the {@link IRecipeRegistry}
     */
    IRecipeRegistry getRecipeRegistry();

    /**
     * Register hook but don't need an argument.
     *
     * @param type     the type of the hook
     * @param runnable the runnable
     * @param <T>      the type of the delivering event
     */
    <T> void addHook(HookType<T> type, Runnable runnable);

    /**
     * Add a hook into AstralFlow eventbus.
     * Your hook will be called when the event is fired.
     *
     * @param type     the type of the hook
     * @param runnable the runnable
     * @param <T>      the type of the delivering event
     */
    <T> void addHook(HookType<T> type, Consumer<T> runnable);

    /**
     * Get hooks by a hookType
     *
     * @param hook the hook type
     * @param <T>  the type of the delivering event
     * @return the collection of the hooks
     */
    <T> Collection<? extends Consumer<T>> getHooks(HookType<T> hook);

    /**
     * Call registered hooks for an event.
     *
     * @param hookType the hook type
     * @param event    the event
     * @param <T>      the type of the delivering event
     * @return cancelled (if this event is cancellable) or not
     */
    <T> boolean callHooks(HookType<T> hookType, T event);

    /**
     * @return the {@link IExtensionRegistry}
     */
    IExtensionRegistry getExtensionRegistry();

    /**
     * As a plugin. For most of bukkit apis.
     *
     * @return AstralFlow Instance
     */
    default Plugin asPlugin() {
        return (Plugin) this;
    }
}
