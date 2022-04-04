package io.ib67.astralflow.wireless;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.WorldMock;
import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.test.TestUtil;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

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
    public void testPeerDiscovery() {
        SimplePeer.createBuilder().location(new Location(peerWorld, 0, 0, 0)).build();
        assertFalse(AstralFlow.getInstance().getWirelessRegistry().findPeers(new Location(peerWorld, 0, 2, 0), 3).isEmpty());
    }
}
