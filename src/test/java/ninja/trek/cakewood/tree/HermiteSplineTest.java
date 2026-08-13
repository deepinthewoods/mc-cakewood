package ninja.trek.cakewood.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ninja.trek.cakewood.tree.HermiteSpline.SegmentRole;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

class HermiteSplineTest {
    @Test
    void evaluatesEndpointsAndInterpolatesRadius() {
        HermiteSpline spline = new HermiteSpline(new Vec3(0, 0, 0), new Vec3(4, 6, 2),
                new Vec3(2, 3, 0), new Vec3(2, 3, 0), 1.5, .25, SegmentRole.TRUNK, true);
        assertEquals(new Vec3(0, 0, 0), spline.evaluate(0));
        assertEquals(new Vec3(4, 6, 2), spline.evaluate(1));
        assertEquals(1.5, spline.radius(0), 1.0e-9);
        assertEquals(.875, spline.radius(.5), 1.0e-9);
        assertEquals(.25, spline.radius(1), 1.0e-9);
    }

    @Test
    void radiusVariationPreservesEndpointsAndChangesTheLinearSilhouette() {
        HermiteSpline spline = new HermiteSpline(new Vec3(0, 0, 0), new Vec3(0, 12, 0),
                new Vec3(0, 8, 0), new Vec3(0, 8, 0), 1.6, .4, SegmentRole.TRUNK, true);

        assertEquals(1.6, spline.radius(0), 1.0e-9);
        assertEquals(.4, spline.radius(1), 1.0e-9);
        assertTrue(spline.radius(.25) > 1.3);
        assertTrue(spline.radius(.75) < .7);
    }
}
