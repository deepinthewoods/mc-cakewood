package ninja.trek.cakewood.tree;

import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;

/** Data-facing mapping from a sapling to its structural profile and weighted foliage palette. */
public record TreeInputDefinition(Block sapling, List<FoliageChoice> foliagePool, String profileId) {
    public TreeInputDefinition {
        foliagePool = List.copyOf(foliagePool);
        if (foliagePool.isEmpty()) throw new IllegalArgumentException("Foliage pool cannot be empty");
        if (foliagePool.stream().anyMatch(choice -> choice.weight() <= 0)) {
            throw new IllegalArgumentException("Foliage weights must be positive");
        }
        if (TreeProfiles.get(profileId).isEmpty()) throw new IllegalArgumentException("Unknown profile " + profileId);
    }

    public Block chooseFoliage(RandomSource random) {
        int total = foliagePool.stream().mapToInt(FoliageChoice::weight).sum();
        int selected = random.nextInt(total);
        for (FoliageChoice choice : foliagePool) {
            selected -= choice.weight();
            if (selected < 0) return choice.block();
        }
        return foliagePool.getLast().block();
    }

    public record FoliageChoice(Block block, int weight) {}
}
