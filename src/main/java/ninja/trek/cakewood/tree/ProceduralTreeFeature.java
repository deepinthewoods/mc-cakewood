package ninja.trek.cakewood.tree;

import com.mojang.serialization.Codec;
import java.util.Optional;
import ninja.trek.cakewood.CakeWoodTree;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Configured-feature endpoint. Sapling growth normally supplies a fully preflighted plan. */
public final class ProceduralTreeFeature extends Feature<NoneFeatureConfiguration> {
    public ProceduralTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        ServerLevel level = context.level().getLevel();
        Optional<SplineTreePlan> plan = CakeWoodTree.takePreparedPlan(context.origin());
        if (plan.isEmpty()) {
            plan = SaplingPattern.capture(level, context.origin())
                    .flatMap(pattern -> SplineTreePlanner.create(level, pattern, context.random().nextLong()));
        }
        return plan.filter(value -> value.apply(level)).isPresent();
    }
}
