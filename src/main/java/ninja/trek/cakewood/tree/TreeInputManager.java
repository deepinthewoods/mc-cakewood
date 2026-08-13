package ninja.trek.cakewood.tree;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ninja.trek.cakewood.CakeWood;
import ninja.trek.cakewood.CakeWoodRegistry;
import ninja.trek.cakewood.tree.TreeInputDefinition.FoliageChoice;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;

/** Loads the {@code tree_inputs} server-data directory; datapacks may replace or add mappings. */
public final class TreeInputManager implements SimpleSynchronousResourceReloadListener {
    private static volatile Map<Block, TreeInputDefinition> definitions = Map.of();

    private TreeInputManager() {}

    public static void register() {
        bootstrapDefaults();
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new TreeInputManager());
    }

    public static Optional<TreeInputDefinition> get(Block sapling) {
        return Optional.ofNullable(definitions.get(sapling));
    }

    public static Map<Block, TreeInputDefinition> all() {
        return definitions;
    }

    public static void bootstrapDefaults() {
        Map<Block, TreeInputDefinition> next = new HashMap<>();
        put(next, Blocks.OAK_SAPLING, "oak", Blocks.OAK_LEAVES);
        put(next, Blocks.SPRUCE_SAPLING, "spruce", Blocks.SPRUCE_LEAVES);
        put(next, Blocks.BIRCH_SAPLING, "birch", Blocks.BIRCH_LEAVES);
        put(next, Blocks.JUNGLE_SAPLING, "jungle", Blocks.JUNGLE_LEAVES);
        put(next, Blocks.ACACIA_SAPLING, "acacia", Blocks.ACACIA_LEAVES);
        put(next, Blocks.DARK_OAK_SAPLING, "dark_oak", Blocks.DARK_OAK_LEAVES);
        put(next, Blocks.PALE_OAK_SAPLING, "pale_oak", Blocks.PALE_OAK_LEAVES);
        put(next, Blocks.MANGROVE_PROPAGULE, "mangrove", Blocks.MANGROVE_LEAVES);
        put(next, Blocks.CHERRY_SAPLING, "cherry", Blocks.CHERRY_LEAVES);
        put(next, Blocks.AZALEA, "azalea", Blocks.AZALEA_LEAVES);
        put(next, Blocks.FLOWERING_AZALEA, "flowering_azalea", Blocks.FLOWERING_AZALEA_LEAVES);
        if (CakeWoodRegistry.CAKE_WOOD_SAPLING != null) {
            next.put(CakeWoodRegistry.CAKE_WOOD_SAPLING,
                    new TreeInputDefinition(CakeWoodRegistry.CAKE_WOOD_SAPLING, vanillaLeaves(), "native"));
        }
        definitions = Map.copyOf(next);
    }

    private static void put(Map<Block, TreeInputDefinition> target, Block sapling, String profile, Block leaves) {
        target.put(sapling, new TreeInputDefinition(sapling, List.of(new FoliageChoice(leaves, 1)), profile));
    }

    private static List<FoliageChoice> vanillaLeaves() {
        List<FoliageChoice> leaves = new ArrayList<>();
        try {
            for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(BlockTags.LEAVES)) {
                Block block = holder.value();
                Identifier id = BuiltInRegistries.BLOCK.getKey(block);
                if (id != null && "minecraft".equals(id.getNamespace()) && isLeavesLike(block)) {
                    leaves.add(new FoliageChoice(block, 1));
                }
            }
        } catch (IllegalStateException ignored) {
            // Tags are not bound during the earliest bootstrap pass; the reload pass retries dynamically.
        }
        if (leaves.isEmpty()) {
            leaves.addAll(List.of(
                    new FoliageChoice(Blocks.OAK_LEAVES, 1), new FoliageChoice(Blocks.SPRUCE_LEAVES, 1),
                    new FoliageChoice(Blocks.BIRCH_LEAVES, 1), new FoliageChoice(Blocks.JUNGLE_LEAVES, 1),
                    new FoliageChoice(Blocks.ACACIA_LEAVES, 1), new FoliageChoice(Blocks.DARK_OAK_LEAVES, 1),
                    new FoliageChoice(Blocks.MANGROVE_LEAVES, 1), new FoliageChoice(Blocks.CHERRY_LEAVES, 1),
                    new FoliageChoice(Blocks.PALE_OAK_LEAVES, 1), new FoliageChoice(Blocks.AZALEA_LEAVES, 1),
                    new FoliageChoice(Blocks.FLOWERING_AZALEA_LEAVES, 1)));
        }
        return List.copyOf(leaves);
    }

    @Override
    public Identifier getFabricId() {
        return CakeWood.id("tree_inputs");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        Map<Block, TreeInputDefinition> loaded = new HashMap<>();
        Map<Identifier, Resource> resources = manager.listResources("tree_inputs", id -> id.getPath().endsWith(".json"));
        resources.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                TreeInputDefinition definition = parse(JsonParser.parseReader(reader).getAsJsonObject());
                loaded.put(definition.sapling(), definition);
            } catch (Exception exception) {
                CakeWood.LOGGER.warn("Ignoring invalid CakeWood tree input {}: {}", entry.getKey(), exception.getMessage());
            }
        });
        if (loaded.isEmpty()) {
            bootstrapDefaults();
            CakeWood.LOGGER.warn("No valid tree input data was loaded; using built-in CakeWood mappings");
        } else {
            definitions = Map.copyOf(loaded);
            CakeWood.LOGGER.info("Loaded {} CakeWood tree input mappings", loaded.size());
        }
    }

    private static TreeInputDefinition parse(JsonObject json) {
        Identifier saplingId = Identifier.parse(json.get("sapling").getAsString());
        Block sapling = BuiltInRegistries.BLOCK.getOptional(saplingId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown sapling " + saplingId));
        String profile = json.get("profile").getAsString();
        List<FoliageChoice> foliage = new ArrayList<>();
        if (json.has("foliage_tag")) {
            Identifier tagId = Identifier.parse(json.get("foliage_tag").getAsString());
            TagKey<Block> tag = TagKey.create(Registries.BLOCK, tagId);
            try {
                for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(tag)) {
                    Block block = holder.value();
                    Identifier id = BuiltInRegistries.BLOCK.getKey(block);
                    if (isLeavesLike(block) && (!json.has("vanilla_only") || !json.get("vanilla_only").getAsBoolean()
                            || "minecraft".equals(id.getNamespace()))) {
                        foliage.add(new FoliageChoice(block, 1));
                    }
                }
            } catch (IllegalStateException ignored) {
                // Tag binding may complete after this listener; the vanilla leaves fallback remains valid.
            }
            if (foliage.isEmpty() && tagId.equals(BlockTags.LEAVES.location())) {
                foliage.addAll(vanillaLeaves());
            }
        }
        if (json.has("foliage")) {
            JsonArray entries = json.getAsJsonArray("foliage");
            for (JsonElement element : entries) {
                JsonObject choice = element.isJsonPrimitive()
                        ? primitiveChoice(element.getAsString()) : element.getAsJsonObject();
                Identifier blockId = Identifier.parse(choice.get("block").getAsString());
                Block block = BuiltInRegistries.BLOCK.getOptional(blockId)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown foliage block " + blockId));
                if (!isLeavesLike(block)) {
                    throw new IllegalArgumentException(blockId + " is not a vanilla-compatible leaves block");
                }
                foliage.add(new FoliageChoice(block, choice.has("weight") ? choice.get("weight").getAsInt() : 1));
            }
        }
        return new TreeInputDefinition(sapling, foliage, profile);
    }

    private static JsonObject primitiveChoice(String block) {
        JsonObject result = new JsonObject();
        result.addProperty("block", block);
        return result;
    }

    private static boolean isLeavesLike(Block block) {
        return block.defaultBlockState().hasProperty(LeavesBlock.PERSISTENT)
                && block.defaultBlockState().hasProperty(LeavesBlock.DISTANCE);
    }
}
