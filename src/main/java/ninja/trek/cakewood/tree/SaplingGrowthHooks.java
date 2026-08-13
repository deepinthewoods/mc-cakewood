package ninja.trek.cakewood.tree;

import ninja.trek.cakewood.CakeWoodRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Shared narrowly-scoped reservation behavior used by vanilla sapling mixins. */
public final class SaplingGrowthHooks {
    private SaplingGrowthHooks() {}

    public static boolean shouldReserve(LevelReader level, BlockPos pos) {
        if (level.getBlockState(pos).getBlock() == CakeWoodRegistry.CAKE_WOOD_SAPLING
                && SaplingPattern.capture(level, pos).isPresent()) return false;
        return SaplingPattern.findContaining(level, pos).isPresent();
    }

    public static boolean redirectBonemeal(ServerLevel level, RandomSource random, BlockPos pos) {
        if (level.getBlockState(pos).getBlock() == CakeWoodRegistry.CAKE_WOOD_SAPLING
                && SaplingPattern.capture(level, pos).isPresent()) return false;
        return SaplingPattern.findContaining(level, pos).map(pattern -> {
            BlockState centerState = level.getBlockState(pattern.center());
            ((SaplingBlock) centerState.getBlock()).performBonemeal(level, random, pattern.center(), centerState);
            return true;
        }).orElse(false);
    }
}
