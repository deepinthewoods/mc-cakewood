// File: src/main/java/ninja/trek/cakewood/CakeWoodSaplingBlock.java
package ninja.trek.cakewood;

import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CakeWoodSaplingBlock extends SaplingBlock {
    public CakeWoodSaplingBlock(TreeGrower generator, BlockBehaviour.Properties settings) {
        super(generator, settings);
    }
}
