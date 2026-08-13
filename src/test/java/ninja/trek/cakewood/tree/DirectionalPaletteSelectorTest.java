package ninja.trek.cakewood.tree;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DirectionalPaletteSelectorTest {
    @Test
    void octantWeightingAllowsOnlyNearestAndAdjacentPalettes() {
        Set<RingDirection> allowed = Set.of(RingDirection.NORTHWEST, RingDirection.NORTH, RingDirection.NORTHEAST);
        for (int seed = 0; seed < 2_000; seed++) {
            assertTrue(allowed.contains(DirectionalPaletteSelector.select(RingDirection.NORTH.bearing(), new Random(seed))));
        }
    }
}
