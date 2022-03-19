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

package io.ib67.astralflow.hook;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.hook.event.HookEvent;
import io.ib67.astralflow.hook.event.chunk.ChunkLoadHook;
import io.ib67.astralflow.hook.event.chunk.ChunkUnloadHook;
import io.ib67.astralflow.hook.event.item.ItemConsumeEvent;
import io.ib67.astralflow.hook.event.item.ItemDamagedEvent;
import io.ib67.astralflow.hook.event.item.ItemInteractBlockEvent;
import io.ib67.astralflow.hook.event.item.ItemInteractEntityEvent;
import io.ib67.astralflow.hook.event.machine.MachineBreakEvent;

import java.util.function.Consumer;

// Constants. 提供一个统一一的监听系统
@SuppressWarnings("unused")
public final class HookType<T extends HookEvent> {
    public static final HookType<?> PLUGIN_SHUTDOWN = new HookType<>();
    public static final HookType<?> SAVE_DATA = new HookType<>();
    public static final HookType<?> ASTRALFLOW_STARTUP_COMPLETED = new HookType<>();

    // For items
    public static final HookType<ItemConsumeEvent> ITEM_CONSUME = new HookType<>();
    public static final HookType<ItemDamagedEvent> ITEM_DAMAGE = new HookType<>();
    public static final HookType<ItemInteractBlockEvent> ITEM_INTERACT_BLOCK = new HookType<>();
    public static final HookType<ItemInteractEntityEvent> ITEM_INTERACT_ENTITY = new HookType<>();

    public static final HookType<ChunkLoadHook> CHUNK_LOAD = new HookType<>();
    public static final HookType<ChunkUnloadHook> CHUNK_UNLOAD = new HookType<>();

    public static final HookType<MachineBreakEvent> MACHINE_BREAK = new HookType<>();

    public void register(Consumer<T> acceptor) {
        AstralFlow.getInstance().addHook(this, acceptor);
    }

    public void register(Runnable acceptor) {
        AstralFlow.getInstance().addHook(this, t -> acceptor.run());
    }
}
