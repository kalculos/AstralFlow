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

package io.ib67.astralflow.internal;


import org.jetbrains.annotations.ApiStatus;

import static org.inlambda.kiwi.Kiwi.fromAny;

@ApiStatus.Internal
public final class AstralConstants {
    public static final boolean MOCKING = fromAny(() -> {
        try {
            Class.forName("org.junit.jupiter.api.Test");
            return true;
        } catch (Throwable t) {
            return false;
        }
    }).get();
    public static final boolean DEBUG = Boolean.getBoolean("astralflow.debug");
    public static final String[] LOGO = ("    _        _             _ _____ _               \n" +
            "   / \\   ___| |_ _ __ __ _| |  ___| | _____      __\n" +
            "  / _ \\ / __| __| '__/ _` | | |_  | |/ _ \\ \\ /\\ / /\n" +
            " / ___ \\\\__ \\ |_| | | (_| | |  _| | | (_) \\ V  V / \n" +
            "/_/   \\_\\___/\\__|_|  \\__,_|_|_|   |_|\\___/ \\_/\\_/  \n" +
            "                                                   \n").split("\n");
}
