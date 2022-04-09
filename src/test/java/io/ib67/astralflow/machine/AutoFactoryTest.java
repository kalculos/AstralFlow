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

package io.ib67.astralflow.machine;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.machines.AbstractMachine;
import io.ib67.astralflow.machines.AutoFactory;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.MachineProperty;
import io.ib67.astralflow.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class AutoFactoryTest {
    @BeforeAll
    public void setup() {
        TestUtil.init();
    }

    @Test
    public void onTestFactory() {

        Assertions.assertNotNull(AstralFlow.getInstance().getFactories().getMachineFactory(MachineA.class), "Test MachineA -- regular args");
        Assertions.assertNotNull(AstralFlow.getInstance().getFactories().getMachineFactory(MachineB.class), "Test MachineB -- regular args");
        Assertions.assertTrue(() -> {
            try {
                AstralFlow.getInstance().getFactories().getMachineFactory(MachineC.class);
            } catch (Throwable t) {
                return true;
            }
            return false;
        }, "Test MachineC -- unmatchable");
        @AutoFactory
        class MachineD extends AbstractMachine { // there is a hidden constructor argument caused by non-static

            protected MachineD(MachineProperty p) {
                super(p);
            }

            @Override
            public void update(IMachine self) {

            }
        }
        Assertions.assertThrows(IllegalArgumentException.class, () -> AstralFlow.getInstance().getFactories().getMachineFactory(MachineD.class), "Test MachineD -- Invalid types in con");
    }

    @AutoFactory
    static class MachineC extends AbstractMachine {

        protected MachineC() {
            super(null); // do not do this in production
        }

        @Override
        public void update(IMachine self) {

        }
    }

    @AutoFactory
    static class MachineB extends AbstractMachine {

        protected MachineB(MachineProperty p) {
            super(p);
            setState(p.getState());
        }

        @Override
        public void update(IMachine self) {

        }
    }

    @AutoFactory
    static class MachineA extends AbstractMachine {

        protected MachineA(MachineProperty property) {
            super(property);
        }

        @Override
        public void update(IMachine self) {

        }
    }
}
