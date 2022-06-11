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

package io.ib67.astralflow.hook;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.api.events.MachineBlockBreakEvent;
import io.ib67.astralflow.api.events.MachineBlockPlaceEvent;
import io.ib67.astralflow.hook.event.server.SaveDataEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/**
 * A convenient hooks for events.<br />
 * You can register your callback from here and receive events.<br />
 * We'll only comment the events that defined by AstralFlow, most events came from Bukkit, You should check their documents instead.<br />
 * <code>
 * HookType.XXX.register( event -> {xxxx} );
 * </code>
 * One hook can be called in several conditions, such as (BlockBreakEvent, BlockBurnEvent) -> BLOCK_BREAK
 *
 * @param <T>
 */
// Constants. 提供一个统一一的监听系统
@SuppressWarnings("unused")
@RequiredArgsConstructor
@ApiStatus.AvailableSince("0.1.0")
public final class HookType<T> {
    /**
     * Called when plugin is shutting down
     */
    public static final HookType<?> PLUGIN_SHUTDOWN = new HookType<>("Plugin Shutdown");
    /**
     * Called when you should save your data, periodic.
     */
    public static final HookType<SaveDataEvent> SAVE_DATA = new HookType<>("Save Data");
    /**
     * Called when astralflow is ready for all.
     */
    public static final HookType<?> ASTRALFLOW_STARTUP_COMPLETED = new HookType<>("AstralFlow Startup Completed");


    public static final HookType<PlayerItemConsumeEvent> ITEM_CONSUME = new HookType<>("Item Consume");
    public static final HookType<PlayerItemDamageEvent> ITEM_DAMAGE = new HookType<>("Item Damage");
    /**
     * Fired when a player is interacting with air.
     */
    public static final HookType<PlayerInteractEvent> ITEM_USE = new HookType<>("Item Use");
    public static final HookType<PlayerInteractEvent> PLAYER_INTERACT = new HookType<>("Player Interact");
    public static final HookType<PlayerInteractEntityEvent> PLAYER_INTERACT_ENTITY = new HookType<>("Player Interact Entity");
    public static final HookType<PlayerInteractEvent> PLAYER_INTERACT_BLOCK = new HookType<>("Player Interact Block");
    /**
     * Fired when a player is interacting with a block, but happens before {@link #PLAYER_INTERACT_BLOCK} and Machine Interactions.
     * Use it only if you want to override upstream behavior of interactive machines or you want to receive any block interaction in spite of there are some machines mixed in.
     */
    public static final HookType<PlayerInteractEvent> PLAYER_INTERACT_BLOCK_LOW = new HookType<>("Player Interact Block (LOW)");
    public static final HookType<PlayerItemBreakEvent> ITEM_BROKEN = new HookType<>("Item Broken");
    public static final HookType<EntityDeathEvent> ENTITY_DEATH = new HookType<>("Entity Death");
    // For chunks
    public static final HookType<ChunkLoadEvent> CHUNK_LOAD = new HookType<>("Chunk Load");
    public static final HookType<ChunkUnloadEvent> CHUNK_UNLOAD = new HookType<>("Chunk Unload");

    // For machines
    /**
     * Called when a machine is broken by player, etc
     */
    public static final HookType<MachineBlockBreakEvent> MACHINE_BREAK = new HookType<>("Machine Break");
    /**
     * Called when a machine is placed.
     */
    public static final HookType<MachineBlockPlaceEvent> MACHINE_PLACE = new HookType<>("Machine Place");

    public static final HookType<EntityDamageByEntityEvent> ENTITY_DAMAGE_BY_ENTITY = new HookType<>("Entity Damage By Entity");
    public static final HookType<EntityDamageEvent> ENTITY_DAMAGE = new HookType<>("Entity Damage");
    public static final HookType<AsyncPlayerChatEvent> PLAYER_CHAT = new HookType<>("Player Chat");
    public static final HookType<PlayerMoveEvent> PLAYER_MOVE = new HookType<>("Player Move");

    // For blocks
    /**
     * happens before {@link #BLOCK_BREAK} and Machine Interactions.
     * Use it only if you want to override upstream behavior, or you want to receive any block interaction in spite of there are some machines mixed in.
     */
    public static final HookType<BlockBreakEvent> BLOCK_BREAK_LOW = new HookType<>("Block Break (LOW)");
    public static final HookType<BlockBreakEvent> BLOCK_BREAK = new HookType<>("Block Break");
    /**
     * happens before {@link #BLOCK_PLACE} and Machine Interactions.
     * Use it only if you want to override upstream behavior, or you want to receive any block interaction in spite of there are some machines mixed in.
     */
    public static final HookType<BlockPlaceEvent> BLOCK_PLACE_LOW = new HookType<>("Block Place (LOW)");
    public static final HookType<BlockPlaceEvent> BLOCK_PLACE = new HookType<>("Block Place");
    public static final HookType<BlockDropItemEvent> BLOCK_DROP_ITEM = new HookType<>("Block Drop Item");
    public static final HookType<ProjectileHitEvent> PROJECTILE_HIT = new HookType<>("Projectile Hit");
    /**
     * It also calls {@link #BLOCK_BREAK}
     */
    public static final HookType<BlockBurnEvent> BLOCK_BURNT = new HookType<>("Block Burn");
    /**
     * It also calls {@link #BLOCK_BREAK}
     */
    public static final HookType<BlockFadeEvent> BLOCK_FADE = new HookType<>("Block Fade");
    /**
     * It also calls {@link #BLOCK_BREAK}
     */
    public static final HookType<BlockExplodeEvent> BLOCK_EXPLODE = new HookType<>("Block Explode");

    /**
     * A name for exceptions.
     */
    @Getter
    private final String name;

    /**
     * Register your callback. We'll call your callback when event is triggered.
     * You should remind that we may deliver a null event if the hookType doesn't mention any events (e.g {@link HookType#PLUGIN_SHUTDOWN}).
     *
     * @param acceptor callback
     */
    public void register(Consumer<T> acceptor) {
        AstralFlow.getInstance().addHook(this, acceptor);
    }

    /**
     * Register your callback which doesn't care about event.
     *
     * @param acceptor callback
     */
    public void register(Runnable acceptor) {
        AstralFlow.getInstance().addHook(this, t -> acceptor.run());
    }

    @Override
    public String toString() {
        return name;
    }
}
