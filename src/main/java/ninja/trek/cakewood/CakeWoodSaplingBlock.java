// File: src/main/java/ninja/trek/cakewood/CakeWoodSaplingBlock.java
package ninja.trek.cakewood;

import ninja.trek.cakewood.tree.SaplingPattern;
import ninja.trek.cakewood.tree.SplineTreePlanner;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class CakeWoodSaplingBlock extends SaplingBlock {
    public CakeWoodSaplingBlock(TreeGrower generator, BlockBehaviour.Properties settings) {
        super(generator, settings);
    }

    @Override
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(STAGE) == 0) {
            level.setBlock(pos, state.setValue(STAGE, 1), 4);
            return;
        }
        SaplingPattern.capture(level, pos).flatMap(pattern ->
                SplineTreePlanner.create(level, pattern, random.nextLong())).ifPresent(plan ->
                CakeWoodTree.growWithPreparedPlan(pos, plan,
                        () -> CakeWoodSaplingBlock.super.advanceTree(level, pos, state, random)));
    }
}
