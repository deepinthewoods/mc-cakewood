package ninja.trek.cakewood.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SplineTreeSkeletonGeneratorTest {
    @Test
    void fixedSeedOutputIsDeterministicAndBoundedForEveryProfile() {
        for (TreeProfile profile : TreeProfiles.all()) {
            SplineTreeSkeleton first = SplineTreeSkeletonGenerator.generateFlat(profile, 0xCA4E_700DL);
            SplineTreeSkeleton second = SplineTreeSkeletonGenerator.generateFlat(profile, 0xCA4E_700DL);
            assertEquals(first, second, profile.id());
            assertTrue(first.height() >= 24 && first.height() <= 32, profile.id());
            assertFalse(first.splines().isEmpty(), profile.id());
            assertFalse(first.foliageBlobs().isEmpty(), profile.id());
            assertTrue(first.splines().size() <= 256, profile.id());
            double maxRadius = first.splines().stream()
                    .flatMapToDouble(spline -> java.util.stream.DoubleStream.of(
                            Math.hypot(spline.end().x - .5, spline.end().z - .5),
                            Math.hypot(spline.start().x - .5, spline.start().z - .5)))
                    .max().orElseThrow();
            assertTrue(maxRadius <= 16.0, profile.id() + " radius=" + maxRadius);
        }
    }

    @Test
    void differentSeedsChangeTheSkeleton() {
        assertFalse(SplineTreeSkeletonGenerator.generateFlat(TreeProfiles.NATIVE, 1)
                .equals(SplineTreeSkeletonGenerator.generateFlat(TreeProfiles.NATIVE, 2)));
    }

    @Test
    void branchesLeaveTheirAttachmentsUpwardBeforeWeightedOuterDroop() {
        SplineTreeSkeleton skeleton = SplineTreeSkeletonGenerator.generateFlat(
                TreeProfiles.get("dark_oak").orElseThrow(), 0xD4A4_0A4L);
        for (HermiteSpline branch : skeleton.splines()) {
            if (branch.role() != HermiteSpline.SegmentRole.PRIMARY_BRANCH
                    && branch.role() != HermiteSpline.SegmentRole.SECONDARY_BRANCH) continue;

            assertTrue(branch.startTangent().y > 0.0, branch.role() + " tangent=" + branch.startTangent());
        }

        assertTrue(skeleton.splines().stream()
                .filter(spline -> spline.role() == HermiteSpline.SegmentRole.PRIMARY_BRANCH)
                .anyMatch(spline -> spline.endTangent().y < 0.0));
    }
}
