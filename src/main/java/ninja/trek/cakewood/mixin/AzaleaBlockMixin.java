package ninja.trek.cakewood.mixin;

import ninja.trek.cakewood.tree.SaplingGrowthHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AzaleaBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AzaleaBlock.class)
abstract class AzaleaBlockMixin {
    @Inject(method = "performBonemeal", at = @At("HEAD"), cancellable = true)
    private void cakewood$redirectBonemeal(ServerLevel level, RandomSource random, BlockPos pos,
            BlockState state, CallbackInfo callback) {
        if (SaplingGrowthHooks.redirectBonemeal(level, random, pos)) callback.cancel();
    }
}
