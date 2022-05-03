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

package io.ib67.astralflow.internal.storage.impl.chunk;

import io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class BufferUtil {
    @Deprecated
    public static void writeLocation(Location loc, ByteBuf buf) {
        // [name len] [name] [x(1 byte)] [y(1b)] [z(1b)]
        var worldName = loc.getWorld().getName();
        buf.writeInt(worldName.length());
        buf.writeBytes(worldName.getBytes(UTF_8));
        buf.writeInt(loc.getBlockX());
        buf.writeShort(loc.getBlockY());
        buf.writeInt(loc.getBlockZ());
    }

    public static void writeLocation2(Location loc, ByteBuf buf) {
        // [name len] [name] [x(1 byte)] [y(1b)] [z(1b)]
        var worldName = loc.getWorld().getName();
        buf.writeByte(worldName.length());
        buf.writeBytes(worldName.getBytes(UTF_8));
        buf.writeByte(loc.getBlockX() >= 0 ? loc.getBlockX() & 15 : 16 + (loc.getBlockX() % 16));
        buf.writeShort(loc.getBlockY());
        buf.writeByte(loc.getBlockZ() >= 0 ? loc.getBlockZ() & 15 : 16 + (loc.getBlockZ() % 16));
    }

    @Deprecated
    public static Location readLocation(int chunkX, int chunkZ, ByteBuf buf) {
        var worldNameLen = new byte[buf.readInt()];
        buf.readBytes(worldNameLen);
        var worldName = new String(worldNameLen, UTF_8);
        var x = buf.readInt();
        var y = buf.readShort();
        var z = buf.readInt();
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public static Location readLocation2(int chunkX, int chunkZ, ByteBuf buf) {
        var worldNameLen = new byte[buf.readByte()];
        buf.readBytes(worldNameLen);
        var worldName = new String(worldNameLen, UTF_8);
        var x = chunkX * 16 + buf.readByte();
        var y = buf.readShort();
        var z = chunkZ * 16 + buf.readByte();
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }
}
