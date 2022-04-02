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

package io.ib67.astralflow.listener;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.hook.HookType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;

public class WorldListener implements Listener {
    @EventHandler
    private void onWorldSave(WorldSaveEvent event) {
        // save data.
        // TODO: WIP configuration delay
        //Bukkit.getScheduler().runTaskLater(AstralFlow.getInstance().asPlugin(), () -> AstralFlow.getInstance().getHooks(HookType.SAVE_DATA).forEach(e -> e.accept(null)), 10L);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        AstralFlow.getInstance().callHooks(HookType.CHUNK_LOAD, event);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        AstralFlow.getInstance().callHooks(HookType.CHUNK_UNLOAD, event);
    }

}
