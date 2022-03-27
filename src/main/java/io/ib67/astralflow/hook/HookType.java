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
import io.ib67.astralflow.hook.event.HookEvent;
import io.ib67.astralflow.hook.event.block.BlockBreakEvent;
import io.ib67.astralflow.hook.event.block.BlockMoveEvent;
import io.ib67.astralflow.hook.event.block.BlockPlaceEvent;
import io.ib67.astralflow.hook.event.chunk.ChunkLoadHook;
import io.ib67.astralflow.hook.event.chunk.ChunkUnloadHook;
import io.ib67.astralflow.hook.event.item.*;
import io.ib67.astralflow.hook.event.machine.MachineBreakEvent;
import io.ib67.astralflow.hook.event.machine.MachinePlaceEvent;
import io.ib67.astralflow.hook.event.player.PlayerAttackEvent;
import io.ib67.astralflow.hook.event.player.PlayerChatEvent;
import io.ib67.astralflow.hook.event.player.PlayerMoveEvent;
import io.ib67.astralflow.hook.event.player.PlayerRawInteractEvent;
import io.ib67.astralflow.hook.event.server.SaveDataEvent;

import java.util.function.Consumer;

// Constants. 提供一个统一一的监听系统
@SuppressWarnings("unused")
public final class HookType<T extends HookEvent> {
    public static final HookType<?> PLUGIN_SHUTDOWN = new HookType<>();
    public static final HookType<SaveDataEvent> SAVE_DATA = new HookType<>();
    public static final HookType<?> ASTRALFLOW_STARTUP_COMPLETED = new HookType<>();

    // For items
    public static final HookType<ItemConsumeEvent> ITEM_CONSUME = new HookType<>();
    public static final HookType<ItemDamagedEvent> ITEM_DAMAGE = new HookType<>();
    public static final HookType<ItemInteractBlockEvent> ITEM_INTERACT_BLOCK = new HookType<>();
    public static final HookType<ItemInteractEntityEvent> ITEM_INTERACT_ENTITY = new HookType<>();
    public static final HookType<ItemUseEvent> ITEM_USE = new HookType<>();
    public static final HookType<ItemBrokenEvent> ITEM_BROKEN = new HookType<>();

    // For chunks
    public static final HookType<ChunkLoadHook> CHUNK_LOAD = new HookType<>();
    public static final HookType<ChunkUnloadHook> CHUNK_UNLOAD = new HookType<>();

    // For machines
    public static final HookType<MachineBreakEvent> MACHINE_BREAK = new HookType<>();
    public static final HookType<MachinePlaceEvent> MACHINE_PLACE = new HookType<>();

    // For players
    public static final HookType<PlayerAttackEvent> PLAYER_ATTACK = new HookType<>();
    public static final HookType<PlayerChatEvent> PLAYER_CHAT = new HookType<>();
    public static final HookType<PlayerMoveEvent> PLAYER_MOVE = new HookType<>();
    public static final HookType<PlayerRawInteractEvent> PLAYER_RAW_INTERACT = new HookType<>();

    // For blocks
    public static final HookType<BlockBreakEvent> BLOCK_BREAK = new HookType<>();
    public static final HookType<BlockMoveEvent> BLOCK_MOVE = new HookType<>();
    public static final HookType<BlockPlaceEvent> BLOCK_PLACE = new HookType<>();

    public void register(Consumer<T> acceptor) {
        AstralFlow.getInstance().addHook(this, acceptor);
    }

    public void register(Runnable acceptor) {
        AstralFlow.getInstance().addHook(this, t -> acceptor.run());
    }
}
