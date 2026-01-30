package ninja.trek.cakewood;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CakeWoodRegistry {
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

    private static AbstractBlock.Settings createBlockSettings(Identifier id) {
        return AbstractBlock.Settings.create()
                .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id))
                .mapColor(MapColor.BROWN)
                .strength(0.5f)
                .sounds(BlockSoundGroup.WOOD)
                .nonOpaque()
                .pistonBehavior(PistonBehavior.DESTROY)
                .breakInstantly();
    }

    private static BlockItem createBlockItem(Block block, Identifier id) {
        return new BlockItem(block, new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, id))
                .useBlockPrefixedTranslationKey());
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
        Identifier cakeWoodId = CakeWood.id("cake_wood");
        CAKE_WOOD_BLOCK = new CakeWoodBlock(createBlockSettings(cakeWoodId));
        CAKE_WOOD_ITEM = createBlockItem(CAKE_WOOD_BLOCK, cakeWoodId);
        Registry.register(Registries.BLOCK, cakeWoodId, CAKE_WOOD_BLOCK);
        Registry.register(Registries.ITEM, cakeWoodId, CAKE_WOOD_ITEM);

        Identifier cakeWoodPlanksId = CakeWood.id("cake_wood_planks");
        CAKE_WOOD_PLANKS_BLOCK = new CakeWoodBlock(createBlockSettings(cakeWoodPlanksId));
        CAKE_WOOD_PLANKS_ITEM = createBlockItem(CAKE_WOOD_PLANKS_BLOCK, cakeWoodPlanksId);
        Registry.register(Registries.BLOCK, cakeWoodPlanksId, CAKE_WOOD_PLANKS_BLOCK);
        Registry.register(Registries.ITEM, cakeWoodPlanksId, CAKE_WOOD_PLANKS_ITEM);

        // Register base Stripped CakeWood
        Identifier strippedCakeWoodId = CakeWood.id("stripped_cake_wood");
        STRIPPED_CAKE_WOOD_BLOCK = new CakeWoodBlock(createBlockSettings(strippedCakeWoodId));
        STRIPPED_CAKE_WOOD_ITEM = createBlockItem(STRIPPED_CAKE_WOOD_BLOCK, strippedCakeWoodId);
        Registry.register(Registries.BLOCK, strippedCakeWoodId, STRIPPED_CAKE_WOOD_BLOCK);
        Registry.register(Registries.ITEM, strippedCakeWoodId, STRIPPED_CAKE_WOOD_ITEM);

        // Register base CornerCakeWood and CornerCakeWood Planks
        Identifier cornerCakeWoodId = CakeWood.id("corner_cake_wood");
        CORNER_CAKE_WOOD_BLOCK = new CakeWoodCornerBlock(createBlockSettings(cornerCakeWoodId));
        CORNER_CAKE_WOOD_ITEM = createBlockItem(CORNER_CAKE_WOOD_BLOCK, cornerCakeWoodId);
        Registry.register(Registries.BLOCK, cornerCakeWoodId, CORNER_CAKE_WOOD_BLOCK);
        Registry.register(Registries.ITEM, cornerCakeWoodId, CORNER_CAKE_WOOD_ITEM);

        Identifier cornerCakeWoodPlanksId = CakeWood.id("corner_cake_wood_planks");
        CORNER_CAKE_WOOD_PLANKS_BLOCK = new CakeWoodCornerBlock(createBlockSettings(cornerCakeWoodPlanksId));
        CORNER_CAKE_WOOD_PLANKS_ITEM = createBlockItem(CORNER_CAKE_WOOD_PLANKS_BLOCK, cornerCakeWoodPlanksId);
        Registry.register(Registries.BLOCK, cornerCakeWoodPlanksId, CORNER_CAKE_WOOD_PLANKS_BLOCK);
        Registry.register(Registries.ITEM, cornerCakeWoodPlanksId, CORNER_CAKE_WOOD_PLANKS_ITEM);

        // Register base Stripped Corner CakeWood
        Identifier strippedCornerCakeWoodId = CakeWood.id("stripped_corner_cake_wood");
        STRIPPED_CORNER_CAKE_WOOD_BLOCK = new CakeWoodCornerBlock(createBlockSettings(strippedCornerCakeWoodId));
        STRIPPED_CORNER_CAKE_WOOD_ITEM = createBlockItem(STRIPPED_CORNER_CAKE_WOOD_BLOCK, strippedCornerCakeWoodId);
        Registry.register(Registries.BLOCK, strippedCornerCakeWoodId, STRIPPED_CORNER_CAKE_WOOD_BLOCK);
        Registry.register(Registries.ITEM, strippedCornerCakeWoodId, STRIPPED_CORNER_CAKE_WOOD_ITEM);

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
        Identifier woodId = CakeWood.id(woodType + "_veneered_cake_wood");
        Identifier plankId = CakeWood.id(woodType + "_veneered_cake_wood_planks");
        CakeWoodBlock woodBlock = new CakeWoodBlock(createBlockSettings(woodId));
        CakeWoodBlock plankBlock = new CakeWoodBlock(createBlockSettings(plankId));
        BlockItem woodItem = createBlockItem(woodBlock, woodId);
        BlockItem plankItem = createBlockItem(plankBlock, plankId);

        // Create and register Stripped CakeWood variant
        Identifier strippedWoodId = CakeWood.id("stripped_" + woodType + "_veneered_cake_wood");
        CakeWoodBlock strippedWoodBlock = new CakeWoodBlock(createBlockSettings(strippedWoodId));
        BlockItem strippedWoodItem = createBlockItem(strippedWoodBlock, strippedWoodId);

        // Create and register CornerCakeWood variant
        Identifier cornerWoodId = CakeWood.id(woodType + "_veneered_corner_cake_wood");
        Identifier cornerPlankId = CakeWood.id(woodType + "_veneered_corner_cake_wood_planks");
        CakeWoodCornerBlock cornerWoodBlock = new CakeWoodCornerBlock(createBlockSettings(cornerWoodId));
        CakeWoodCornerBlock cornerPlankBlock = new CakeWoodCornerBlock(createBlockSettings(cornerPlankId));
        BlockItem cornerWoodItem = createBlockItem(cornerWoodBlock, cornerWoodId);
        BlockItem cornerPlankItem = createBlockItem(cornerPlankBlock, cornerPlankId);

        // Create and register Stripped CornerCakeWood variant
        Identifier strippedCornerWoodId = CakeWood.id("stripped_" + woodType + "_veneered_corner_cake_wood");
        CakeWoodCornerBlock strippedCornerWoodBlock = new CakeWoodCornerBlock(createBlockSettings(strippedCornerWoodId));
        BlockItem strippedCornerWoodItem = createBlockItem(strippedCornerWoodBlock, strippedCornerWoodId);

        // Register CakeWood blocks and items
        Registry.register(Registries.BLOCK, woodId, woodBlock);
        Registry.register(Registries.ITEM, woodId, woodItem);
        Registry.register(Registries.BLOCK, plankId, plankBlock);
        Registry.register(Registries.ITEM, plankId, plankItem);

        // Register Stripped CakeWood blocks and items
        Registry.register(Registries.BLOCK, strippedWoodId, strippedWoodBlock);
        Registry.register(Registries.ITEM, strippedWoodId, strippedWoodItem);

        // Register CornerCakeWood blocks and items
        Registry.register(Registries.BLOCK, cornerWoodId, cornerWoodBlock);
        Registry.register(Registries.ITEM, cornerWoodId, cornerWoodItem);
        Registry.register(Registries.BLOCK, cornerPlankId, cornerPlankBlock);
        Registry.register(Registries.ITEM, cornerPlankId, cornerPlankItem);

        // Register Stripped CornerCakeWood blocks and items
        Registry.register(Registries.BLOCK, strippedCornerWoodId, strippedCornerWoodBlock);
        Registry.register(Registries.ITEM, strippedCornerWoodId, strippedCornerWoodItem);

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
