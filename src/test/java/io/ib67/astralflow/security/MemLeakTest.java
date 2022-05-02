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

package io.ib67.astralflow.security;

import io.ib67.astralflow.security.mem.impl.SimpleLeakTracker;
import io.ib67.astralflow.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemLeakTest {

    @BeforeAll
    public void setup() {
        TestUtil.init();
    }

    @Test
    public void onSLDTest() {
        var leakde = new SimpleLeakTracker();
        var object = new Object();
        leakde.track(object);
        for (int i = 0; i < 21; i++) {
            if (i == 19) {
                Assertions.assertThrows(IllegalStateException.class, leakde::onTick); // the 20th round should throw an exception
            } else {
                leakde.onTick();
            }
        }

        object = null;
        System.gc(); // recycle the object.
        for (int i = 0; i < 21; i++) {
            leakde.onTick(); // nothing should happen
        }
    }
}
