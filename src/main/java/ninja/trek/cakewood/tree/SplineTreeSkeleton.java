package ninja.trek.cakewood.tree;

import java.util.List;

/** Immutable, placement-independent output of the topology pass. */
public record SplineTreeSkeleton(List<HermiteSpline> splines, List<FoliageBlob> foliageBlobs, int height) {
    public SplineTreeSkeleton {
        splines = List.copyOf(splines);
        foliageBlobs = List.copyOf(foliageBlobs);
    }
}
