package ninja.trek.cakewood.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class TreeProfilesTest {
    @Test
    void everyUniformCardinalCombinationSelectsItsProfile() {
        for (TreeProfile profile : TreeProfiles.all()) {
            assertSame(profile, TreeProfiles.resolveCardinals(List.of(
                    profile.id(), profile.id(), profile.id(), profile.id())));
        }
    }

    @Test
    void mixedCardinalsKeepNativeTopologyAndBoundedTraits() {
        TreeProfile mixed = TreeProfiles.resolveCardinals(List.of("spruce", "acacia", "mangrove", "cherry"));
        assertEquals(TreeProfile.Topology.NATIVE, mixed.topology());
        assertEquals("native_mixed", mixed.id());
        assertTrue(mixed.height().min() >= TreeProfiles.NATIVE.height().min());
        assertTrue(mixed.height().max() <= TreeProfiles.NATIVE.height().max());
        assertTrue(mixed.branchLength().min() >= TreeProfiles.NATIVE.branchLength().min());
        assertTrue(mixed.branchLength().max() <= TreeProfiles.NATIVE.branchLength().max());
        assertTrue(mixed.rootLength().min() >= TreeProfiles.NATIVE.rootLength().min());
        assertTrue(mixed.rootLength().max() <= TreeProfiles.NATIVE.rootLength().max());
    }

    @Test
    void fourNativeCardinalsSelectNativeExactly() {
        assertSame(TreeProfiles.NATIVE,
                TreeProfiles.resolveCardinals(List.of("native", "native", "native", "native")));
    }
}
