package ninja.trek.cakewood;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import java.util.*;

public class CakeWoodDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(CakeWoodModelGenerator::new);
    }

    private static class CakeWoodModelGenerator extends FabricModelProvider {
        public CakeWoodModelGenerator(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            generateCakeWoodVariant(blockStateModelGenerator, "", "oak");
            for (Map.Entry<String, CakeWoodBlock> entry : CakeWoodRegistry.getAllVariantBlocks().entrySet()) {
                generateCakeWoodVariant(blockStateModelGenerator, entry.getKey(), entry.getKey());
            }
        }

        private void generateCakeWoodVariant(BlockStateModelGenerator generator, String variant, String textureName) {
            String prefix = variant.isEmpty() ? "" : variant + "_";
            CakeWoodBlock block = variant.isEmpty() ?
                    CakeWoodRegistry.CAKE_WOOD_BLOCK :
                    CakeWoodRegistry.getVariantBlock(variant);

            MultipartBlockStateSupplier stateSupplier = MultipartBlockStateSupplier.create(block);

            // Define our texture key
            TextureKey WOOD_TEXTURE = TextureKey.of("wood_texture");

            for (int bites = 0; bites <= 7; bites++) {
                final int bitesValue = bites;
                for (boolean isTop : Arrays.asList(true, false)) {
                    String modelName = String.format("block/%scake_wood_%s_%d",
                            prefix, isTop ? "top" : "bottom", bites);

                    // Create model with base and texture reference
                    Model model = new Model(
                            Optional.of(Identifier.of(CakeWood.MOD_ID, "block/cake_wood_base")),
                            Optional.empty(),
                            WOOD_TEXTURE
                    );

                    // Create the texture mapping
                    TextureMap textureMap = new TextureMap()
                            .put(WOOD_TEXTURE, Identifier.of("minecraft", "block/" + textureName + "_planks"));

                    // Upload the model with texture mapping
                    Identifier modelId = model.upload(
                            Identifier.of(CakeWood.MOD_ID, modelName),
                            textureMap,
                            generator.modelCollector
                    );

                    // Add variants for each direction
                    for (Direction facing : Direction.Type.HORIZONTAL) {
                        When condition = When.create()
                                .set(isTop ? CakeWoodBlock.TOP_BITES : CakeWoodBlock.BOTTOM_BITES, bitesValue)
                                .set(isTop ? CakeWoodBlock.TOP_FACING : CakeWoodBlock.BOTTOM_FACING, facing);

                        stateSupplier.with(condition, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, modelId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.values()
                                        [(int) (facing.asRotation() / 90.0f)])
                        );
                    }
                }
            }
            generator.blockStateCollector.accept(stateSupplier);
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            generateItemModel(itemModelGenerator, "", CakeWoodRegistry.CAKE_WOOD_ITEM);
            for (Map.Entry<String, BlockItem> entry : CakeWoodRegistry.getAllVariantItems().entrySet()) {
                generateItemModel(itemModelGenerator, entry.getKey(), entry.getValue());
            }
        }

        private void generateItemModel(ItemModelGenerator generator, String variant, BlockItem item) {
            String textureName = variant.isEmpty() ? "oak" : variant;
            Models.GENERATED.upload(
                    ModelIds.getItemModelId(item),
                    TextureMap.layer0(Identifier.of("minecraft", "block/" + textureName + "_planks")),
                    generator.writer
            );
        }
    }
}