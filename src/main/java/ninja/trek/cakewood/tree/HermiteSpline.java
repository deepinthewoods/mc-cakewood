package ninja.trek.cakewood.tree;

import net.minecraft.world.phys.Vec3;

/** Cubic Hermite tube centerline with organically tapered endpoint radii. */
public record HermiteSpline(
        Vec3 start,
        Vec3 end,
        Vec3 startTangent,
        Vec3 endTangent,
        double startRadius,
        double endRadius,
        SegmentRole role,
        boolean major
) {
    public enum SegmentRole { TRUNK, PRIMARY_BRANCH, SECONDARY_BRANCH, ROOT }

    public HermiteSpline {
        if (startRadius <= 0 || endRadius <= 0) throw new IllegalArgumentException("Spline radii must be positive");
    }

    public Vec3 evaluate(double t) {
        double u = clamp(t);
        double u2 = u * u;
        double u3 = u2 * u;
        double h00 = 2 * u3 - 3 * u2 + 1;
        double h10 = u3 - 2 * u2 + u;
        double h01 = -2 * u3 + 3 * u2;
        double h11 = u3 - u2;
        return start.scale(h00).add(startTangent.scale(h10)).add(end.scale(h01)).add(endTangent.scale(h11));
    }

    public Vec3 derivative(double t) {
        double u = clamp(t);
        double u2 = u * u;
        double h00 = 6 * u2 - 6 * u;
        double h10 = 3 * u2 - 4 * u + 1;
        double h01 = -6 * u2 + 6 * u;
        double h11 = 3 * u2 - 2 * u;
        return start.scale(h00).add(startTangent.scale(h10)).add(end.scale(h01)).add(endTangent.scale(h11));
    }

    public double radius(double t) {
        double u = clamp(t);
        // Smooth tapering keeps more weight near an attachment before narrowing faster
        // through the outer half. A role-sized, endpoint-safe wave prevents otherwise
        // ruler-straight tubes without changing the specified endpoint radii.
        double eased = u * u * (3.0 - 2.0 * u);
        double tapered = startRadius + (endRadius - startRadius) * eased;
        double envelope = 4.0 * u * (1.0 - u);
        double variation = switch (role) {
            case TRUNK -> .20;
            case PRIMARY_BRANCH -> .26;
            case SECONDARY_BRANCH -> .20;
            case ROOT -> .12;
        };
        double wave = Math.sin(Math.PI * 2.0 * u) + Math.sin(Math.PI * 4.0 * u) * .18;
        return Math.max(.08, tapered * (1.0 + variation * envelope * wave));
    }

    public double approximateLength() {
        double length = 0.0;
        Vec3 previous = start;
        for (int i = 1; i <= 16; i++) {
            Vec3 point = evaluate(i / 16.0);
            length += point.distanceTo(previous);
            previous = point;
        }
        return length;
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
