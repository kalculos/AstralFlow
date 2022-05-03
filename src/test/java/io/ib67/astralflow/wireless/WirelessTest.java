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

package io.ib67.astralflow.wireless;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.WorldMock;
import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.test.TestUtil;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WirelessTest {
    WorldMock peerWorld;

    @BeforeAll
    public void setup() {
        TestUtil.init();
        peerWorld = MockBukkit.getMock().addSimpleWorld("testPeers");
    }

    @Test
    public void testPeerDiscoveryAsync() throws InterruptedException, ExecutionException {
        SimplePeer.createBuilder()
                .location(new Location(peerWorld, 3333, 0, 0)).build();
        assertFalse(AstralFlow.getInstance().getWirelessRegistry().findPeersAsync(new Location(peerWorld, 3333, 2, 0), 3).get().isEmpty());
    }

    @Test
    public void testPeerDiscovery() {
        SimplePeer.createBuilder().location(new Location(peerWorld, 0, 0, 0)).build();
        assertFalse(AstralFlow.getInstance().getWirelessRegistry().findPeers(new Location(peerWorld, 0, 2, 0), 3).isEmpty());
    }
}
