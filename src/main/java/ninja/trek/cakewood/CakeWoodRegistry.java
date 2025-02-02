package ninja.trek.cakewood;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import java.util.HashMap;
import java.util.Map;

public class CakeWoodRegistry {
    // Base CakeWood blocks and items
    public static final CakeWoodBlock CAKE_WOOD_BLOCK = new CakeWoodBlock(createBlockSettings());
    public static final CakeWoodBlock CAKE_WOOD_PLANKS_BLOCK = new CakeWoodBlock(createBlockSettings());
    public static final BlockItem CAKE_WOOD_ITEM = createBlockItem(CAKE_WOOD_BLOCK, "block.cakewood.cake_wood");
    public static final BlockItem CAKE_WOOD_PLANKS_ITEM = createBlockItem(CAKE_WOOD_PLANKS_BLOCK, "block.cakewood.cake_wood_planks");

    // Base CornerCakeWood blocks and items
    public static final CornerCakeWoodBlock CORNER_CAKE_WOOD_BLOCK = new CornerCakeWoodBlock(createBlockSettings());
    public static final CornerCakeWoodBlock CORNER_CAKE_WOOD_PLANKS_BLOCK = new CornerCakeWoodBlock(createBlockSettings());
    public static final BlockItem CORNER_CAKE_WOOD_ITEM = createBlockItem(CORNER_CAKE_WOOD_BLOCK, "block.cakewood.corner_cake_wood");
    public static final BlockItem CORNER_CAKE_WOOD_PLANKS_ITEM = createBlockItem(CORNER_CAKE_WOOD_PLANKS_BLOCK, "block.cakewood.corner_cake_wood_planks");

    // Maps to store variants for CakeWood
    private static final Map<String, CakeWoodBlock> CAKE_WOOD_VARIANTS = new HashMap<>();
    private static final Map<String, BlockItem> CAKE_WOOD_VARIANT_ITEMS = new HashMap<>();
    private static final Map<String, CakeWoodBlock> CAKE_WOOD_PLANK_VARIANTS = new HashMap<>();
    private static final Map<String, BlockItem> CAKE_WOOD_PLANK_VARIANT_ITEMS = new HashMap<>();

    // Maps to store variants for CornerCakeWood
    private static final Map<String, CornerCakeWoodBlock> CORNER_CAKE_WOOD_VARIANTS = new HashMap<>();
    private static final Map<String, BlockItem> CORNER_CAKE_WOOD_VARIANT_ITEMS = new HashMap<>();
    private static final Map<String, CornerCakeWoodBlock> CORNER_CAKE_WOOD_PLANK_VARIANTS = new HashMap<>();
    private static final Map<String, BlockItem> CORNER_CAKE_WOOD_PLANK_VARIANT_ITEMS = new HashMap<>();

    private static FabricBlockSettings createBlockSettings() {
        return FabricBlockSettings.create()
                .mapColor(MapColor.BROWN)
                .strength(0.5f)
                .sounds(BlockSoundGroup.WOOD)
                .nonOpaque()
                .pistonBehavior(PistonBehavior.DESTROY)
                .breakInstantly();
    }

    private static BlockItem createBlockItem(Block block, String translationKey) {
        return new BlockItem(block, new Item.Settings()) {
            @Override
            public Text getName() {
                return Text.translatable(translationKey);
            }
        };
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

    // Getter methods for CornerCakeWood variants
    public static Map<String, CornerCakeWoodBlock> getAllCornerWoodVariantBlocks() {
        return CORNER_CAKE_WOOD_VARIANTS;
    }

    public static Map<String, BlockItem> getAllCornerWoodVariantItems() {
        return CORNER_CAKE_WOOD_VARIANT_ITEMS;
    }

    public static Map<String, CornerCakeWoodBlock> getAllCornerPlankVariantBlocks() {
        return CORNER_CAKE_WOOD_PLANK_VARIANTS;
    }

    public static Map<String, BlockItem> getAllCornerPlankVariantItems() {
        return CORNER_CAKE_WOOD_PLANK_VARIANT_ITEMS;
    }

    public static void register() {
        // Register base CakeWood and CakeWood Planks
        Registry.register(Registries.BLOCK, CakeWood.id("cake_wood"), CAKE_WOOD_BLOCK);
        Registry.register(Registries.ITEM, CakeWood.id("cake_wood"), CAKE_WOOD_ITEM);
        Registry.register(Registries.BLOCK, CakeWood.id("cake_wood_planks"), CAKE_WOOD_PLANKS_BLOCK);
        Registry.register(Registries.ITEM, CakeWood.id("cake_wood_planks"), CAKE_WOOD_PLANKS_ITEM);

        // Register base CornerCakeWood and CornerCakeWood Planks
        Registry.register(Registries.BLOCK, CakeWood.id("corner_cake_wood"), CORNER_CAKE_WOOD_BLOCK);
        Registry.register(Registries.ITEM, CakeWood.id("corner_cake_wood"), CORNER_CAKE_WOOD_ITEM);
        Registry.register(Registries.BLOCK, CakeWood.id("corner_cake_wood_planks"), CORNER_CAKE_WOOD_PLANKS_BLOCK);
        Registry.register(Registries.ITEM, CakeWood.id("corner_cake_wood_planks"), CORNER_CAKE_WOOD_PLANKS_ITEM);

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
        CakeWoodBlock woodBlock = new CakeWoodBlock(createBlockSettings());
        CakeWoodBlock plankBlock = new CakeWoodBlock(createBlockSettings());
        String woodId = woodType + "_veneered_cake_wood";
        String plankId = woodType + "_veneered_cake_wood_planks";
        BlockItem woodItem = createBlockItem(woodBlock, "block.cakewood." + woodId);
        BlockItem plankItem = createBlockItem(plankBlock, "block.cakewood." + plankId);

        // Create and register CornerCakeWood variant
        CornerCakeWoodBlock cornerWoodBlock = new CornerCakeWoodBlock(createBlockSettings());
        CornerCakeWoodBlock cornerPlankBlock = new CornerCakeWoodBlock(createBlockSettings());
        String cornerWoodId = woodType + "_veneered_corner_cake_wood";
        String cornerPlankId = woodType + "_veneered_corner_cake_wood_planks";
        BlockItem cornerWoodItem = createBlockItem(cornerWoodBlock, "block.cakewood." + cornerWoodId);
        BlockItem cornerPlankItem = createBlockItem(cornerPlankBlock, "block.cakewood." + cornerPlankId);

        // Register CakeWood blocks and items
        Registry.register(Registries.BLOCK, CakeWood.id(woodId), woodBlock);
        Registry.register(Registries.ITEM, CakeWood.id(woodId), woodItem);
        Registry.register(Registries.BLOCK, CakeWood.id(plankId), plankBlock);
        Registry.register(Registries.ITEM, CakeWood.id(plankId), plankItem);

        // Register CornerCakeWood blocks and items
        Registry.register(Registries.BLOCK, CakeWood.id(cornerWoodId), cornerWoodBlock);
        Registry.register(Registries.ITEM, CakeWood.id(cornerWoodId), cornerWoodItem);
        Registry.register(Registries.BLOCK, CakeWood.id(cornerPlankId), cornerPlankBlock);
        Registry.register(Registries.ITEM, CakeWood.id(cornerPlankId), cornerPlankItem);

        // Store in maps
        CAKE_WOOD_VARIANTS.put(woodType, woodBlock);
        CAKE_WOOD_VARIANT_ITEMS.put(woodType, woodItem);
        CAKE_WOOD_PLANK_VARIANTS.put(woodType, plankBlock);
        CAKE_WOOD_PLANK_VARIANT_ITEMS.put(woodType, plankItem);

        CORNER_CAKE_WOOD_VARIANTS.put(woodType, cornerWoodBlock);
        CORNER_CAKE_WOOD_VARIANT_ITEMS.put(woodType, cornerWoodItem);
        CORNER_CAKE_WOOD_PLANK_VARIANTS.put(woodType, cornerPlankBlock);
        CORNER_CAKE_WOOD_PLANK_VARIANT_ITEMS.put(woodType, cornerPlankItem);
    }

    // Getter methods for specific variants
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

    public static CornerCakeWoodBlock getCornerWoodVariantBlock(String woodType) {
        return CORNER_CAKE_WOOD_VARIANTS.get(woodType);
    }

    public static BlockItem getCornerWoodVariantItem(String woodType) {
        return CORNER_CAKE_WOOD_VARIANT_ITEMS.get(woodType);
    }

    public static CornerCakeWoodBlock getCornerPlankVariantBlock(String woodType) {
        return CORNER_CAKE_WOOD_PLANK_VARIANTS.get(woodType);
    }

    public static BlockItem getCornerPlankVariantItem(String woodType) {
        return CORNER_CAKE_WOOD_PLANK_VARIANT_ITEMS.get(woodType);
    }
}