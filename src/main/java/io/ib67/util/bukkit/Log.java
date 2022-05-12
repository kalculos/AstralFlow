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

package io.ib67.util.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Log {
    public static String pattern = "&b%s&8: &f";
    public static String modulePattern = "&b%s/%m&8: &f";
    private static String realPrefix = pattern.replace("%s", "AstralFlow").replaceAll("&", ChatColor.COLOR_CHAR + "");
    private static String modulePrefix = modulePattern.replace("%s", "AstralFlow").replaceAll("&", ChatColor.COLOR_CHAR + "");

    @Deprecated
    public static void info(String message) {
        Bukkit.getConsoleSender().sendMessage(realPrefix + message.replaceAll("&", ChatColor.COLOR_CHAR + ""));
    }

    public static void info(String module, String message) {
        Bukkit.getConsoleSender().sendMessage(modulePrefix.replace("%m", module) + message.replaceAll("&", ChatColor.COLOR_CHAR + ""));
    }

    public static void warn(String message) {
        Bukkit.getConsoleSender().sendMessage(realPrefix + ChatColor.RED + message.replaceAll("&", ChatColor.COLOR_CHAR + ""));
    }

    public static void warn(String module, String message) {
        Bukkit.getConsoleSender().sendMessage(modulePrefix.replace("%m", module) + ChatColor.RED + message.replaceAll("&", ChatColor.COLOR_CHAR + ""));
    }
}