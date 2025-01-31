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
            TextureKey CAKE_TEXTURE = TextureKey.of("cake_texture");

            for (int bites = 0; bites <= 7; bites++) {
                final int bitesValue = bites;
                for (boolean isTop : Arrays.asList(true, false)) {
                    String modelName = String.format("block/%scake_wood_%s_%d",
                            prefix, isTop ? "top" : "bottom", bites);

                    Model model = new Model(
                            Optional.of(Identifier.of(CakeWood.MOD_ID, "block/cake_wood_base")),
                            Optional.empty(),
                            CAKE_TEXTURE
                    );

                    TextureMap textureMap = new TextureMap()
                            .put(CAKE_TEXTURE, Identifier.of("minecraft", "block/" + textureName));

                    Identifier modelId = model.upload(
                            Identifier.of(CakeWood.MOD_ID, modelName),
                            textureMap,
                            generator.modelCollector,
                            (id, existingTextures) -> createBiteModelJson(bitesValue, isTop)
                    );

                    for (Direction facing : Direction.Type.HORIZONTAL) {
                        When condition = When.create()
                                .set(isTop ? CakeWoodBlock.TOP_BITES : CakeWoodBlock.BOTTOM_BITES, bites)
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

        private JsonObject createBiteModelJson(int bites, boolean isTop) {
            JsonObject modelData = new JsonObject();
            modelData.addProperty("parent", "block/block");

            JsonObject textures = new JsonObject();
            textures.addProperty("particle", "#cake_texture");
            textures.addProperty("cake_texture", "#cake_texture");
            modelData.add("textures", textures);

            JsonObject element = new JsonObject();
            int biteDepth = bites * 2;
            float yMin = isTop ? 8.0f : 0.0f;
            float yMax = isTop ? 16.0f : 8.0f;

            JsonArray from = new JsonArray();

            from.add(1);
            from.add( yMin);
            from.add( 1 + biteDepth);

            JsonArray to = new JsonArray();
            to.add(15);
            to.add(yMax);
            to.add(15);

            element.add("from", from);
            element.add("to", to);

            JsonObject faces = new JsonObject();
            addFace(faces, "north", 1, yMin, 15, yMax);
            addFace(faces, "east", 1 + biteDepth, yMin, 15, yMax);
            addFace(faces, "south", 1, yMin, 15, yMax);
            addFace(faces, "west", 1 + biteDepth, yMin, 15, yMax);
            addFace(faces, "up", 1, 1 + biteDepth, 15, 15);
            addFace(faces, "down", 1, 1 + biteDepth, 15, 15);

            element.add("faces", faces);

            JsonArray elements = new JsonArray();
            elements.add(element);
            modelData.add("elements", elements);

            return modelData;
        }

        private void addFace(JsonObject faces, String face, float uMin, float vMin, float uMax, float vMax) {
            JsonObject faceData = new JsonObject();

            JsonArray uv = new JsonArray();

            uv.add(uMin);
            uv.add(vMin);
            uv.add(uMax);
            uv.add(vMax);

            faceData.add("uv", uv);
            faceData.addProperty("texture", "#cake_texture");
            faces.add(face, faceData);
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
                    TextureMap.layer0(Identifier.of("minecraft", "block/" + textureName)),
                    generator.writer
            );
        }
    }
}