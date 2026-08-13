package ninja.trek.cakewood.tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ninja.trek.cakewood.CakeWoodRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;

/** Immutable capture of one complete, flat 3x3 CakeWood sapling pattern. */
public record SaplingPattern(
        BlockPos center,
        Map<RingDirection, TreeInputDefinition> inputs,
        TreeProfile structuralProfile,
        Map<BlockPos, Block> consumedSaplings
) {
    public SaplingPattern {
        center = center.immutable();
        inputs = Map.copyOf(inputs);
        consumedSaplings = Map.copyOf(consumedSaplings);
        if (inputs.size() != 8 || consumedSaplings.size() != 9) {
            throw new IllegalArgumentException("A CakeWood tree requires exactly eight inputs and one center");
        }
    }

    public static Optional<SaplingPattern> capture(LevelReader level, BlockPos center) {
        return capture(level, center, true);
    }

    /** Used by the configured feature after TreeGrower has temporarily replaced the center with fluid/air. */
    public static Optional<SaplingPattern> captureForFeature(LevelReader level, BlockPos center) {
        return capture(level, center, false);
    }

    private static Optional<SaplingPattern> capture(LevelReader level, BlockPos center, boolean requireCenter) {
        if (CakeWoodRegistry.CAKE_WOOD_SAPLING == null) return Optional.empty();
        Block centerBlock = level.getBlockState(center).getBlock();
        if (requireCenter && centerBlock != CakeWoodRegistry.CAKE_WOOD_SAPLING) return Optional.empty();
        if (!requireCenter && centerBlock != CakeWoodRegistry.CAKE_WOOD_SAPLING
                && !level.getBlockState(center).canBeReplaced()) return Optional.empty();

        EnumMap<RingDirection, TreeInputDefinition> inputs = new EnumMap<>(RingDirection.class);
        Map<BlockPos, Block> consumed = new java.util.LinkedHashMap<>();
        consumed.put(center.immutable(), CakeWoodRegistry.CAKE_WOOD_SAPLING);
        for (RingDirection direction : RingDirection.values()) {
            BlockPos inputPos = center.offset(direction.x(), 0, direction.z());
            Block block = level.getBlockState(inputPos).getBlock();
            Optional<TreeInputDefinition> definition = TreeInputManager.get(block);
            if (definition.isEmpty()) return Optional.empty();
            inputs.put(direction, definition.get());
            consumed.put(inputPos.immutable(), block);
        }

        List<String> cardinals = List.of(
                inputs.get(RingDirection.NORTH).profileId(), inputs.get(RingDirection.EAST).profileId(),
                inputs.get(RingDirection.SOUTH).profileId(), inputs.get(RingDirection.WEST).profileId());
        return Optional.of(new SaplingPattern(center, inputs, TreeProfiles.resolveCardinals(cardinals), consumed));
    }

    /** Finds all possible centers containing a ring member and resolves ambiguity in block-position order. */
    public static Optional<SaplingPattern> findContaining(LevelReader level, BlockPos member) {
        List<SaplingPattern> candidates = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                capture(level, member.offset(x, 0, z)).ifPresent(candidates::add);
            }
        }
        return candidates.stream().min(Comparator
                .comparingInt((SaplingPattern pattern) -> pattern.center().getY())
                .thenComparingInt(pattern -> pattern.center().getX())
                .thenComparingInt(pattern -> pattern.center().getZ()));
    }
}
