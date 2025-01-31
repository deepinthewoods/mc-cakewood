package ninja.trek.cakewood;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
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

    // Maps to store variants
    private static final Map<String, CakeWoodBlock> CAKE_WOOD_VARIANTS = new HashMap<>();
    private static final Map<String, BlockItem> CAKE_WOOD_VARIANT_ITEMS = new HashMap<>();
    private static final Map<String, CakeWoodBlock> CAKE_WOOD_PLANK_VARIANTS = new HashMap<>();
    private static final Map<String, BlockItem> CAKE_WOOD_PLANK_VARIANT_ITEMS = new HashMap<>();

    private static FabricBlockSettings createBlockSettings() {
        return FabricBlockSettings.create()
                .mapColor(MapColor.BROWN)
                .strength(0.5f)
                .sounds(BlockSoundGroup.WOOD)
                .nonOpaque()
                .pistonBehavior(PistonBehavior.DESTROY)
                .breakInstantly();
    }

    private static BlockItem createBlockItem(CakeWoodBlock block, String translationKey) {
        return new BlockItem(block, new Item.Settings()) {
            @Override
            public Text getName() {
                return Text.translatable(translationKey);
            }
        };
    }

    // Getter methods for all variants
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

    public static void register() {
        // Register base CakeWood and CakeWood Planks
        Registry.register(Registries.BLOCK, CakeWood.id("cake_wood"), CAKE_WOOD_BLOCK);
        Registry.register(Registries.ITEM, CakeWood.id("cake_wood"), CAKE_WOOD_ITEM);
        Registry.register(Registries.BLOCK, CakeWood.id("cake_wood_planks"), CAKE_WOOD_PLANKS_BLOCK);
        Registry.register(Registries.ITEM, CakeWood.id("cake_wood_planks"), CAKE_WOOD_PLANKS_ITEM);

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
        // Create and register wood variant
        CakeWoodBlock woodBlock = new CakeWoodBlock(createBlockSettings());
        CakeWoodBlock plankBlock = new CakeWoodBlock(createBlockSettings());

        String woodId = woodType + "_veneered_cake_wood";
        String plankId = woodType + "_veneered_cake_wood_planks";

        BlockItem woodItem = createBlockItem(woodBlock, "block.cakewood." + woodId);
        BlockItem plankItem = createBlockItem(plankBlock, "block.cakewood." + plankId);

        // Register blocks and items
        Registry.register(Registries.BLOCK, CakeWood.id(woodId), woodBlock);
        Registry.register(Registries.ITEM, CakeWood.id(woodId), woodItem);
        Registry.register(Registries.BLOCK, CakeWood.id(plankId), plankBlock);
        Registry.register(Registries.ITEM, CakeWood.id(plankId), plankItem);

        // Store in maps
        CAKE_WOOD_VARIANTS.put(woodType, woodBlock);
        CAKE_WOOD_VARIANT_ITEMS.put(woodType, woodItem);
        CAKE_WOOD_PLANK_VARIANTS.put(woodType, plankBlock);
        CAKE_WOOD_PLANK_VARIANT_ITEMS.put(woodType, plankItem);
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
}