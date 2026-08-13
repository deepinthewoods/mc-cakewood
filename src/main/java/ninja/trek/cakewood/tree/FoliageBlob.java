package ninja.trek.cakewood.tree;

import net.minecraft.world.phys.Vec3;

/** Direction-bearing ellipsoid attached to a branch or crown point. */
public record FoliageBlob(Vec3 center, double radiusX, double radiusY, double radiusZ, double bearing, long seed) {}
