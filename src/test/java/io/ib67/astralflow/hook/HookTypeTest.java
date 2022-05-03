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
import io.ib67.astralflow.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HookTypeTest {
    private final int value = ThreadLocalRandom.current().nextInt();

    @BeforeAll
    public void setup() {
        TestUtil.init();
    }

    @Test
    public void testHookDelivery() {
        var hook = new HookType<HookTypeTest>("testHookDelivery");
        AtomicBoolean equals = new AtomicBoolean(false);
        hook.register(t -> equals.set(t.value == value));
        AstralFlow.getInstance().callHooks(hook, this);
        Assertions.assertTrue(equals.get());
    }
}
