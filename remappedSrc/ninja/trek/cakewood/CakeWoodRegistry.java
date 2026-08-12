package ninja.trek.cakewood;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class CakeWoodRegistry {
    // Stripping map: maps non-stripped blocks to their stripped counterparts
    private static final Map<Block, Block> STRIPPING_MAP = new HashMap<>();

    public static Block getStrippedBlock(Block block) {
        return STRIPPING_MAP.get(block);
    }

    // Base CakeWood blocks and items (initialized in register())
    public static CakeWoodBlock CAKE_WOOD_BLOCK;
    public static CakeWoodBlock CAKE_WOOD_PLANKS_BLOCK;
    public static BlockItem CAKE_WOOD_ITEM;
    public static BlockItem CAKE_WOOD_PLANKS_ITEM;

    // Base Stripped CakeWood blocks and items
    public static CakeWoodBlock STRIPPED_CAKE_WOOD_BLOCK;
    public static BlockItem STRIPPED_CAKE_WOOD_ITEM;

    // Base CornerCakeWood blocks and items
    public static CakeWoodCornerBlock CORNER_CAKE_WOOD_BLOCK;
    public static CakeWoodCornerBlock CORNER_CAKE_WOOD_PLANKS_BLOCK;
    public static BlockItem CORNER_CAKE_WOOD_ITEM;
    public static BlockItem CORNER_CAKE_WOOD_PLANKS_ITEM;

    // Base Stripped Corner CakeWood blocks and items
    public static CakeWoodCornerBlock STRIPPED_CORNER_CAKE_WOOD_BLOCK;
    public static BlockItem STRIPPED_CORNER_CAKE_WOOD_ITEM;

    // Maps to store variants for CakeWood
    private static final Map<String, CakeWoodBlock> CAKE_WOOD_VARIANTS = new HashMap<>();
    private static final Map<String, BlockItem> CAKE_WOOD_VARIANT_ITEMS = new HashMap<>();
    private static final Map<String, CakeWoodBlock> CAKE_WOOD_PLANK_VARIANTS = new HashMap<>();
    private static final Map<String, BlockItem> CAKE_WOOD_PLANK_VARIANT_ITEMS = new HashMap<>();

    // Maps to store stripped variants for CakeWood
    private static final Map<String, CakeWoodBlock> STRIPPED_CAKE_WOOD_VARIANTS = new HashMap<>();
    private static final Map<String, BlockItem> STRIPPED_CAKE_WOOD_VARIANT_ITEMS = new HashMap<>();

    // Maps to store variants for CornerCakeWood
    private static final Map<String, CakeWoodCornerBlock> CORNER_CAKE_WOOD_VARIANTS = new HashMap<>();
    private static final Map<String, BlockItem> CORNER_CAKE_WOOD_VARIANT_ITEMS = new HashMap<>();
    private static final Map<String, CakeWoodCornerBlock> CORNER_CAKE_WOOD_PLANK_VARIANTS = new HashMap<>();
    private static final Map<String, BlockItem> CORNER_CAKE_WOOD_PLANK_VARIANT_ITEMS = new HashMap<>();

    // Maps to store stripped variants for CornerCakeWood
    private static final Map<String, CakeWoodCornerBlock> STRIPPED_CORNER_CAKE_WOOD_VARIANTS = new HashMap<>();
    private static final Map<String, BlockItem> STRIPPED_CORNER_CAKE_WOOD_VARIANT_ITEMS = new HashMap<>();

    private static BlockBehaviour.Properties createBlockSettings(ResourceLocation id) {
        return BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .mapColor(MapColor.COLOR_BROWN)
                .strength(0.5f)
                .sound(SoundType.WOOD)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY)
                .instabreak();
    }

    private static BlockItem createBlockItem(Block block, ResourceLocation id) {
        return new BlockItem(block, new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .useBlockDescriptionPrefix());
    }

    // Getter methods for CakeWood variants
    public static Map<String, CakeWoodBlock> getAllWoodVariantBlocks() {
        return CAKE_WOOD_VARIANTS;
    }

    public static Map<String, BlockItem> getAllWoodVariantItems() {
        return CAKE_WOOD_VARIANT_ITEMS;
    }

    public static Map<String, CakeWoodBlock> getAllPlankVariantBlocks() {
        return CAKE_WOOD_PLANK_VARIANTS;
    }

    public static Map<String, BlockItem> getAllPlankVariantItems() {
        return CAKE_WOOD_PLANK_VARIANT_ITEMS;
    }

    // Getter methods for Stripped CakeWood variants
    public static Map<String, CakeWoodBlock> getAllStrippedWoodVariantBlocks() {
        return STRIPPED_CAKE_WOOD_VARIANTS;
    }

    public static Map<String, BlockItem> getAllStrippedWoodVariantItems() {
        return STRIPPED_CAKE_WOOD_VARIANT_ITEMS;
    }

    // Getter methods for CornerCakeWood variants
    public static Map<String, CakeWoodCornerBlock> getAllCornerWoodVariantBlocks() {
        return CORNER_CAKE_WOOD_VARIANTS;
    }

    public static Map<String, BlockItem> getAllCornerWoodVariantItems() {
        return CORNER_CAKE_WOOD_VARIANT_ITEMS;
    }

    public static Map<String, CakeWoodCornerBlock> getAllCornerPlankVariantBlocks() {
        return CORNER_CAKE_WOOD_PLANK_VARIANTS;
    }

    public static Map<String, BlockItem> getAllCornerPlankVariantItems() {
        return CORNER_CAKE_WOOD_PLANK_VARIANT_ITEMS;
    }

    // Getter methods for Stripped CornerCakeWood variants
    public static Map<String, CakeWoodCornerBlock> getAllStrippedCornerWoodVariantBlocks() {
        return STRIPPED_CORNER_CAKE_WOOD_VARIANTS;
    }

    public static Map<String, BlockItem> getAllStrippedCornerWoodVariantItems() {
        return STRIPPED_CORNER_CAKE_WOOD_VARIANT_ITEMS;
    }

    public static void register() {
        // Register base CakeWood and CakeWood Planks
        ResourceLocation cakeWoodId = CakeWood.id("cake_wood");
        CAKE_WOOD_BLOCK = new CakeWoodBlock(createBlockSettings(cakeWoodId));
        CAKE_WOOD_ITEM = createBlockItem(CAKE_WOOD_BLOCK, cakeWoodId);
        Registry.register(BuiltInRegistries.BLOCK, cakeWoodId, CAKE_WOOD_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, cakeWoodId, CAKE_WOOD_ITEM);

        ResourceLocation cakeWoodPlanksId = CakeWood.id("cake_wood_planks");
        CAKE_WOOD_PLANKS_BLOCK = new CakeWoodBlock(createBlockSettings(cakeWoodPlanksId));
        CAKE_WOOD_PLANKS_ITEM = createBlockItem(CAKE_WOOD_PLANKS_BLOCK, cakeWoodPlanksId);
        Registry.register(BuiltInRegistries.BLOCK, cakeWoodPlanksId, CAKE_WOOD_PLANKS_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, cakeWoodPlanksId, CAKE_WOOD_PLANKS_ITEM);

        // Register base Stripped CakeWood
        ResourceLocation strippedCakeWoodId = CakeWood.id("stripped_cake_wood");
        STRIPPED_CAKE_WOOD_BLOCK = new CakeWoodBlock(createBlockSettings(strippedCakeWoodId));
        STRIPPED_CAKE_WOOD_ITEM = createBlockItem(STRIPPED_CAKE_WOOD_BLOCK, strippedCakeWoodId);
        Registry.register(BuiltInRegistries.BLOCK, strippedCakeWoodId, STRIPPED_CAKE_WOOD_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, strippedCakeWoodId, STRIPPED_CAKE_WOOD_ITEM);

        // Register base CornerCakeWood and CornerCakeWood Planks
        ResourceLocation cornerCakeWoodId = CakeWood.id("corner_cake_wood");
        CORNER_CAKE_WOOD_BLOCK = new CakeWoodCornerBlock(createBlockSettings(cornerCakeWoodId));
        CORNER_CAKE_WOOD_ITEM = createBlockItem(CORNER_CAKE_WOOD_BLOCK, cornerCakeWoodId);
        Registry.register(BuiltInRegistries.BLOCK, cornerCakeWoodId, CORNER_CAKE_WOOD_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, cornerCakeWoodId, CORNER_CAKE_WOOD_ITEM);

        ResourceLocation cornerCakeWoodPlanksId = CakeWood.id("corner_cake_wood_planks");
        CORNER_CAKE_WOOD_PLANKS_BLOCK = new CakeWoodCornerBlock(createBlockSettings(cornerCakeWoodPlanksId));
        CORNER_CAKE_WOOD_PLANKS_ITEM = createBlockItem(CORNER_CAKE_WOOD_PLANKS_BLOCK, cornerCakeWoodPlanksId);
        Registry.register(BuiltInRegistries.BLOCK, cornerCakeWoodPlanksId, CORNER_CAKE_WOOD_PLANKS_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, cornerCakeWoodPlanksId, CORNER_CAKE_WOOD_PLANKS_ITEM);

        // Register base Stripped Corner CakeWood
        ResourceLocation strippedCornerCakeWoodId = CakeWood.id("stripped_corner_cake_wood");
        STRIPPED_CORNER_CAKE_WOOD_BLOCK = new CakeWoodCornerBlock(createBlockSettings(strippedCornerCakeWoodId));
        STRIPPED_CORNER_CAKE_WOOD_ITEM = createBlockItem(STRIPPED_CORNER_CAKE_WOOD_BLOCK, strippedCornerCakeWoodId);
        Registry.register(BuiltInRegistries.BLOCK, strippedCornerCakeWoodId, STRIPPED_CORNER_CAKE_WOOD_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, strippedCornerCakeWoodId, STRIPPED_CORNER_CAKE_WOOD_ITEM);

        // Register stripping mappings for base blocks
        STRIPPING_MAP.put(CAKE_WOOD_BLOCK, STRIPPED_CAKE_WOOD_BLOCK);
        STRIPPING_MAP.put(CORNER_CAKE_WOOD_BLOCK, STRIPPED_CORNER_CAKE_WOOD_BLOCK);

        // Register all wood variants
        registerVariant("oak", "Oak-Veneered");
        registerVariant("spruce", "Spruce-Veneered");
        registerVariant("birch", "Birch-Veneered");
        registerVariant("jungle", "Jungle-Veneered");
        registerVariant("acacia", "Acacia-Veneered");
        registerVariant("dark_oak", "Dark Oak-Veneered");
        registerVariant("mangrove", "Mangrove-Veneered");
        registerVariant("cherry", "Cherry-Veneered");
        registerVariant("bamboo", "Bamboo-Veneered");
        registerVariant("crimson", "Crimson-Veneered");
        registerVariant("warped", "Warped-Veneered");
    }

    private static void registerVariant(String woodType, String displayName) {
        // Create and register CakeWood variant
        ResourceLocation woodId = CakeWood.id(woodType + "_veneered_cake_wood");
        ResourceLocation plankId = CakeWood.id(woodType + "_veneered_cake_wood_planks");
        CakeWoodBlock woodBlock = new CakeWoodBlock(createBlockSettings(woodId));
        CakeWoodBlock plankBlock = new CakeWoodBlock(createBlockSettings(plankId));
        BlockItem woodItem = createBlockItem(woodBlock, woodId);
        BlockItem plankItem = createBlockItem(plankBlock, plankId);

        // Create and register Stripped CakeWood variant
        ResourceLocation strippedWoodId = CakeWood.id("stripped_" + woodType + "_veneered_cake_wood");
        CakeWoodBlock strippedWoodBlock = new CakeWoodBlock(createBlockSettings(strippedWoodId));
        BlockItem strippedWoodItem = createBlockItem(strippedWoodBlock, strippedWoodId);

        // Create and register CornerCakeWood variant
        ResourceLocation cornerWoodId = CakeWood.id(woodType + "_veneered_corner_cake_wood");
        ResourceLocation cornerPlankId = CakeWood.id(woodType + "_veneered_corner_cake_wood_planks");
        CakeWoodCornerBlock cornerWoodBlock = new CakeWoodCornerBlock(createBlockSettings(cornerWoodId));
        CakeWoodCornerBlock cornerPlankBlock = new CakeWoodCornerBlock(createBlockSettings(cornerPlankId));
        BlockItem cornerWoodItem = createBlockItem(cornerWoodBlock, cornerWoodId);
        BlockItem cornerPlankItem = createBlockItem(cornerPlankBlock, cornerPlankId);

        // Create and register Stripped CornerCakeWood variant
        ResourceLocation strippedCornerWoodId = CakeWood.id("stripped_" + woodType + "_veneered_corner_cake_wood");
        CakeWoodCornerBlock strippedCornerWoodBlock = new CakeWoodCornerBlock(createBlockSettings(strippedCornerWoodId));
        BlockItem strippedCornerWoodItem = createBlockItem(strippedCornerWoodBlock, strippedCornerWoodId);

        // Register CakeWood blocks and items
        Registry.register(BuiltInRegistries.BLOCK, woodId, woodBlock);
        Registry.register(BuiltInRegistries.ITEM, woodId, woodItem);
        Registry.register(BuiltInRegistries.BLOCK, plankId, plankBlock);
        Registry.register(BuiltInRegistries.ITEM, plankId, plankItem);

        // Register Stripped CakeWood blocks and items
        Registry.register(BuiltInRegistries.BLOCK, strippedWoodId, strippedWoodBlock);
        Registry.register(BuiltInRegistries.ITEM, strippedWoodId, strippedWoodItem);

        // Register CornerCakeWood blocks and items
        Registry.register(BuiltInRegistries.BLOCK, cornerWoodId, cornerWoodBlock);
        Registry.register(BuiltInRegistries.ITEM, cornerWoodId, cornerWoodItem);
        Registry.register(BuiltInRegistries.BLOCK, cornerPlankId, cornerPlankBlock);
        Registry.register(BuiltInRegistries.ITEM, cornerPlankId, cornerPlankItem);

        // Register Stripped CornerCakeWood blocks and items
        Registry.register(BuiltInRegistries.BLOCK, strippedCornerWoodId, strippedCornerWoodBlock);
        Registry.register(BuiltInRegistries.ITEM, strippedCornerWoodId, strippedCornerWoodItem);

        // Store in maps
        CAKE_WOOD_VARIANTS.put(woodType, woodBlock);
        CAKE_WOOD_VARIANT_ITEMS.put(woodType, woodItem);
        CAKE_WOOD_PLANK_VARIANTS.put(woodType, plankBlock);
        CAKE_WOOD_PLANK_VARIANT_ITEMS.put(woodType, plankItem);

        // Store stripped variants in maps
        STRIPPED_CAKE_WOOD_VARIANTS.put(woodType, strippedWoodBlock);
        STRIPPED_CAKE_WOOD_VARIANT_ITEMS.put(woodType, strippedWoodItem);

        CORNER_CAKE_WOOD_VARIANTS.put(woodType, cornerWoodBlock);
        CORNER_CAKE_WOOD_VARIANT_ITEMS.put(woodType, cornerWoodItem);
        CORNER_CAKE_WOOD_PLANK_VARIANTS.put(woodType, cornerPlankBlock);
        CORNER_CAKE_WOOD_PLANK_VARIANT_ITEMS.put(woodType, cornerPlankItem);

        // Store stripped corner variants in maps
        STRIPPED_CORNER_CAKE_WOOD_VARIANTS.put(woodType, strippedCornerWoodBlock);
        STRIPPED_CORNER_CAKE_WOOD_VARIANT_ITEMS.put(woodType, strippedCornerWoodItem);

        // Register stripping mappings
        STRIPPING_MAP.put(woodBlock, strippedWoodBlock);
        STRIPPING_MAP.put(cornerWoodBlock, strippedCornerWoodBlock);
    }

    // Extended getter methods
    public static CakeWoodBlock getWoodVariantBlock(String woodType) {
        return CAKE_WOOD_VARIANTS.get(woodType);
    }

    public static BlockItem getWoodVariantItem(String woodType) {
        return CAKE_WOOD_VARIANT_ITEMS.get(woodType);
    }

    public static CakeWoodBlock getPlankVariantBlock(String woodType) {
        return CAKE_WOOD_PLANK_VARIANTS.get(woodType);
    }

    public static BlockItem getPlankVariantItem(String woodType) {
        return CAKE_WOOD_PLANK_VARIANT_ITEMS.get(woodType);
    }

    // Getter methods for stripped variants
    public static CakeWoodBlock getStrippedWoodVariantBlock(String woodType) {
        return STRIPPED_CAKE_WOOD_VARIANTS.get(woodType);
    }

    public static BlockItem getStrippedWoodVariantItem(String woodType) {
        return STRIPPED_CAKE_WOOD_VARIANT_ITEMS.get(woodType);
    }

    public static CakeWoodCornerBlock getCornerWoodVariantBlock(String woodType) {
        return CORNER_CAKE_WOOD_VARIANTS.get(woodType);
    }

    public static BlockItem getCornerWoodVariantItem(String woodType) {
        return CORNER_CAKE_WOOD_VARIANT_ITEMS.get(woodType);
    }

    public static CakeWoodCornerBlock getCornerPlankVariantBlock(String woodType) {
        return CORNER_CAKE_WOOD_PLANK_VARIANTS.get(woodType);
    }

    public static BlockItem getCornerPlankVariantItem(String woodType) {
        return CORNER_CAKE_WOOD_PLANK_VARIANT_ITEMS.get(woodType);
    }

    // Getter methods for stripped corner variants
    public static CakeWoodCornerBlock getStrippedCornerWoodVariantBlock(String woodType) {
        return STRIPPED_CORNER_CAKE_WOOD_VARIANTS.get(woodType);
    }

    public static BlockItem getStrippedCornerWoodVariantItem(String woodType) {
        return STRIPPED_CORNER_CAKE_WOOD_VARIANT_ITEMS.get(woodType);
    }
}
