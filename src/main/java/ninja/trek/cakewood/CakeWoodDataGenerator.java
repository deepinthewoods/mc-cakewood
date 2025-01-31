package ninja.trek.cakewood;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.function.BiFunction;

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
        public void generateBlockStateModels(BlockStateModelGenerator generator) {
            generateCakeWoodVariant(generator, "", "oak");
            for (Map.Entry<String, CakeWoodBlock> entry : CakeWoodRegistry.getAllVariantBlocks().entrySet()) {
                generateCakeWoodVariant(generator, entry.getKey(), entry.getKey());
            }
        }

        private void generateCakeWoodVariant(BlockStateModelGenerator generator, String variant, String textureName) {
            String prefix = variant.isEmpty() ? "" : variant + "_";
            CakeWoodBlock block = variant.isEmpty() ?
                    CakeWoodRegistry.CAKE_WOOD_BLOCK :
                    CakeWoodRegistry.getVariantBlock(variant);

            MultipartBlockStateSupplier stateSupplier = MultipartBlockStateSupplier.create(block);
            TextureKey WOOD_TEXTURE = TextureKey.of("wood_texture");

            for (int bites = 0; bites <= 7; bites++) {
                final int bitesValue = bites;
                for (boolean isTop : Arrays.asList(true, false)) {
                    String modelName = String.format("block/%scake_wood_%s_%d",
                            prefix, isTop ? "top" : "bottom", bites);

                    // Create a custom model for this bite state
                    Model model = new Model(
                            Optional.empty(),
                            Optional.empty(),
                            WOOD_TEXTURE
                    ) {
                        @Override
                        public JsonObject createJson(Identifier id, Map<TextureKey, Identifier> textures) {
                            JsonObject json = new JsonObject();

                            // Add textures
                            JsonObject texturesJson = new JsonObject();
                            textures.forEach((key, value) -> {
                                texturesJson.addProperty(key.getName(), value.toString());
                            });
                            texturesJson.addProperty("particle", textures.get(WOOD_TEXTURE).toString());
                            json.add("textures", texturesJson);

                            // Calculate dimensions based on bites
                            float biteSize = bitesValue * 2.0f; // Each bite is 2 pixels
                            float depth = 14 - biteSize;    // Start at 14 pixels wide, subtract bite size
                            int yOffset = isTop ? 8 : 0;
                            int height = isTop ? 8 : 8;

                            // Create elements array
                            JsonArray elements = new JsonArray();
                            JsonObject element = new JsonObject();

                            // Set element coordinates
                            JsonArray from = new JsonArray();
                            from.add(1);  // x
                            from.add(yOffset);  // y
                            from.add(1);  // z
                            element.add("from", from);

                            JsonArray to = new JsonArray();
                            to.add(15);  // x
                            to.add(yOffset + height);  // y
                            to.add(1 + depth);  // z
                            element.add("to", to);

                            // Add faces
                            JsonObject faces = new JsonObject();
                            String textureRef = "#" + WOOD_TEXTURE.getName();

                            // Helper function to create face
                            BiFunction<Integer, Integer, JsonObject> createFace = (startV, endV) -> {
                                JsonObject face = new JsonObject();
                                face.addProperty("texture", textureRef);
                                JsonArray uv = new JsonArray();
                                uv.add(1);  // u1
                                uv.add(startV);  // v1
                                uv.add(15);  // u2
                                uv.add(endV);  // v2
                                face.add("uv", uv);
                                return face;
                            };

                            faces.add("north", createFace.apply(16-height, 16));
                            faces.add("south", createFace.apply(16-height, 16));
                            faces.add("east", createFace.apply(16-height, 16));
                            faces.add("west", createFace.apply(16-height, 16));
                            faces.add("up", createFace.apply(1, (int)depth));
                            faces.add("down", createFace.apply(1, (int)depth));

                            element.add("faces", faces);
                            elements.add(element);
                            json.add("elements", elements);

                            return json;
                        }
                    };

                    // Create the texture mapping
                    TextureMap textureMap = new TextureMap()
                            .put(WOOD_TEXTURE, Identifier.of("minecraft", "block/" + textureName + "_planks"));

                    // Upload the model
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