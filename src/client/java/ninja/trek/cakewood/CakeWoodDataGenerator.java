package ninja.trek.cakewood;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.MultipartModelConditionBuilder;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.item.BlockItem;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiConsumer;

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

        private static AxisRotation getYRotation(Direction facing) {
            // Base model has bite on SOUTH side (+Z). Rotate to align bite with target facing.
            return switch (facing) {
                case SOUTH -> AxisRotation.R0;    // bite already on south, no rotation
                case WEST -> AxisRotation.R90;    // rotate 90° CW: south→west
                case NORTH -> AxisRotation.R180;  // rotate 180°: south→north
                case EAST -> AxisRotation.R270;   // rotate 270° CW: south→east
                default -> AxisRotation.R0;
            };
        }

        /** Resolve the full texture reference string for a given textureId. */
        private static String resolveTextureRef(String textureId) {
            if (textureId.startsWith("cake_wood")) {
                return CakeWood.MOD_ID + ":block/" + textureId;
            }
            return "minecraft:block/" + textureId;
        }

        /**
         * Write a custom JSON model directly to the model collector.
         * This bypasses Model.upload() whose private JSON generation method cannot be
         * overridden in MC 1.21.10 — anonymous Model subclass createJson() is never called.
         */
        private static Identifier uploadCustomModel(
                Identifier modelId,
                JsonObject json,
                BiConsumer<Identifier, ModelSupplier> modelCollector) {
            modelCollector.accept(modelId, () -> json);
            return modelId;
        }

        // ========================
        // Block state models
        // ========================

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator generator) {
            // Generate base CakeWood variants
            generateVariantModel(generator, CakeWoodRegistry.CAKE_WOOD_BLOCK,
                    "cake_wood", "cake_wood");
            generateVariantModel(generator, CakeWoodRegistry.CAKE_WOOD_PLANKS_BLOCK,
                    "cake_wood_planks", "cake_wood_planks");

            // Generate base stripped CakeWood
            generateVariantModel(generator, CakeWoodRegistry.STRIPPED_CAKE_WOOD_BLOCK,
                    "stripped_cake_wood", "cake_wood_stripped");

            // Generate base CornerCakeWood variants
            generateCornerVariantModel(generator, CakeWoodRegistry.CORNER_CAKE_WOOD_BLOCK,
                    "corner_cake_wood", "cake_wood");
            generateCornerVariantModel(generator, CakeWoodRegistry.CORNER_CAKE_WOOD_PLANKS_BLOCK,
                    "corner_cake_wood_planks", "cake_wood_planks");

            // Generate base stripped corner CakeWood
            generateCornerVariantModel(generator, CakeWoodRegistry.STRIPPED_CORNER_CAKE_WOOD_BLOCK,
                    "stripped_corner_cake_wood", "cake_wood_stripped");

            // Generate veneered variants for each wood type
            for (Map.Entry<String, CakeWoodBlock> entry : CakeWoodRegistry.getAllWoodVariantBlocks().entrySet()) {
                String woodType = entry.getKey();
                String textureName;
                String strippedTextureName;

                if (woodType.equals("crimson") || woodType.equals("warped")) {
                    textureName = woodType + "_stem";
                    strippedTextureName = "stripped_" + woodType + "_stem";
                } else if (woodType.equals("bamboo")) {
                    textureName = "bamboo_block";
                    strippedTextureName = "stripped_bamboo_block";
                } else {
                    textureName = woodType + "_log";
                    strippedTextureName = "stripped_" + woodType + "_log";
                }

                // Regular CakeWood variants
                generateVariantModel(generator, entry.getValue(),
                        woodType + "_veneered_cake_wood", textureName);
                generateVariantModel(generator, CakeWoodRegistry.getPlankVariantBlock(woodType),
                        woodType + "_veneered_cake_wood_planks", woodType + "_planks");

                // Stripped CakeWood variants
                generateVariantModel(generator, CakeWoodRegistry.getStrippedWoodVariantBlock(woodType),
                        "stripped_" + woodType + "_veneered_cake_wood", strippedTextureName);

                // Corner CakeWood variants
                generateCornerVariantModel(generator, CakeWoodRegistry.getCornerWoodVariantBlock(woodType),
                        woodType + "_veneered_corner_cake_wood", textureName);
                generateCornerVariantModel(generator, CakeWoodRegistry.getCornerPlankVariantBlock(woodType),
                        woodType + "_veneered_corner_cake_wood_planks", woodType + "_planks");

                // Stripped Corner CakeWood variants
                generateCornerVariantModel(generator, CakeWoodRegistry.getStrippedCornerWoodVariantBlock(woodType),
                        "stripped_" + woodType + "_veneered_corner_cake_wood", strippedTextureName);
            }
        }

        // ========================
        // Item models
        // ========================

        @Override
        public void generateItemModels(ItemModelGenerator generator) {
            // Base variants
            generateItemModel(generator, CakeWoodRegistry.CAKE_WOOD_ITEM, "cake_wood");
            generateItemModel(generator, CakeWoodRegistry.CAKE_WOOD_PLANKS_ITEM, "cake_wood_planks");
            generateItemModel(generator, CakeWoodRegistry.STRIPPED_CAKE_WOOD_ITEM, "cake_wood_stripped");

            generateCornerItemModel(generator, CakeWoodRegistry.CORNER_CAKE_WOOD_ITEM, "cake_wood");
            generateCornerItemModel(generator, CakeWoodRegistry.CORNER_CAKE_WOOD_PLANKS_ITEM, "cake_wood_planks");
            generateCornerItemModel(generator, CakeWoodRegistry.STRIPPED_CORNER_CAKE_WOOD_ITEM, "cake_wood_stripped");

            // Wood variants
            for (Map.Entry<String, BlockItem> entry : CakeWoodRegistry.getAllWoodVariantItems().entrySet()) {
                String woodType = entry.getKey();
                String woodTexture;
                String strippedWoodTexture;

                if (woodType.equals("crimson") || woodType.equals("warped")) {
                    woodTexture = woodType + "_stem";
                    strippedWoodTexture = "stripped_" + woodType + "_stem";
                } else if (woodType.equals("bamboo")) {
                    woodTexture = "bamboo_block";
                    strippedWoodTexture = "stripped_bamboo_block";
                } else {
                    woodTexture = woodType + "_log";
                    strippedWoodTexture = "stripped_" + woodType + "_log";
                }

                // Regular CakeWood items
                generateItemModel(generator, entry.getValue(), woodTexture);
                generateItemModel(generator, CakeWoodRegistry.getPlankVariantItem(woodType),
                        woodType + "_planks");

                // Stripped CakeWood items
                generateItemModel(generator, CakeWoodRegistry.getStrippedWoodVariantItem(woodType),
                        strippedWoodTexture);

                // Corner CakeWood items
                generateCornerItemModel(generator, CakeWoodRegistry.getCornerWoodVariantItem(woodType),
                        woodTexture);
                generateCornerItemModel(generator, CakeWoodRegistry.getCornerPlankVariantItem(woodType),
                        woodType + "_planks");

                // Stripped Corner CakeWood items
                generateCornerItemModel(generator, CakeWoodRegistry.getStrippedCornerWoodVariantItem(woodType),
                        strippedWoodTexture);
            }
        }

        // ========================
        // Item model builders (write JSON directly to modelCollector)
        // ========================

        private void generateItemModel(ItemModelGenerator generator, BlockItem item, String textureId) {
            String textureRef = resolveTextureRef(textureId);

            JsonObject json = new JsonObject();
            json.addProperty("parent", "minecraft:block/block");

            JsonObject textures = new JsonObject();
            textures.addProperty("all", textureRef);
            textures.addProperty("particle", textureRef);
            json.add("textures", textures);

            JsonArray elements = new JsonArray();
            // Top half - with bite on south side
            elements.add(createCuboid(1, 8, 1, 15, 16, 13, "#all"));
            // Bottom half - with one bite
            elements.add(createCuboid(1, 0, 3, 15, 8, 15, "#all"));
            json.add("elements", elements);

            Identifier modelId = ModelIds.getItemModelId(item);
            uploadCustomModel(modelId, json, generator.modelCollector);
            generator.output.accept(item, ItemModels.basic(modelId));
        }

        private void generateCornerItemModel(ItemModelGenerator generator, BlockItem item, String textureId) {
            String textureRef = resolveTextureRef(textureId);

            JsonObject json = new JsonObject();
            json.addProperty("parent", "minecraft:block/block");

            JsonObject textures = new JsonObject();
            textures.addProperty("all", textureRef);
            textures.addProperty("particle", textureRef);
            json.add("textures", textures);

            JsonArray elements = new JsonArray();
            // Bottom half - full size southwest corner
            elements.add(createCornerElement(0, 8, true));
            // Top half - half size southwest corner (showing 4 bites taken)
            elements.add(createCornerElement(8, 16, false));
            json.add("elements", elements);

            Identifier modelId = ModelIds.getItemModelId(item);
            uploadCustomModel(modelId, json, generator.modelCollector);
            generator.output.accept(item, ItemModels.basic(modelId));
        }

        // ========================
        // Block model builders (write JSON directly to modelCollector)
        // ========================

        private void generateVariantModel(BlockStateModelGenerator generator,
                                          CakeWoodBlock block,
                                          String variantName,
                                          String textureId) {
            MultipartBlockModelDefinitionCreator creator = MultipartBlockModelDefinitionCreator.create(block);
            String textureRef = resolveTextureRef(textureId);

            for (int bites = 0; bites < CakeWoodBlock.MAX_BITES; bites++) {
                for (boolean isTop : Arrays.asList(true, false)) {
                    String modelName = String.format("block/%s_%s_%d",
                            variantName,
                            isTop ? "top" : "bottom",
                            bites);

                    // Build the model JSON directly
                    JsonObject json = new JsonObject();
                    json.addProperty("parent", "minecraft:block/block");

                    JsonObject textures = new JsonObject();
                    textures.addProperty("wood_texture", textureRef);
                    textures.addProperty("particle", textureRef);
                    json.add("textures", textures);

                    // Calculate dimensions
                    int biteSize = bites * 2;       // each bite is 2 pixels
                    int depth = 16 - biteSize;       // remaining depth after bites
                    int yOffset = isTop ? 8 : 0;     // vertical position (top/bottom half)
                    int height = 8;                   // height of each half

                    JsonArray elements = new JsonArray();
                    JsonObject element = new JsonObject();

                    JsonArray from = new JsonArray();
                    from.add(0); from.add(yOffset); from.add(0);
                    element.add("from", from);

                    JsonArray to = new JsonArray();
                    to.add(16); to.add(yOffset + height); to.add(depth);
                    element.add("to", to);

                    JsonObject faces = new JsonObject();

                    // North face (bitten face)
                    JsonObject northFace = new JsonObject();
                    northFace.addProperty("texture", "#wood_texture");
                    northFace.addProperty("cullface", "north");
                    northFace.add("uv", createUV(0, isTop ? 8 : 0, 16, isTop ? 16 : 8));
                    faces.add("north", northFace);

                    // South face
                    JsonObject southFace = new JsonObject();
                    southFace.addProperty("texture", "#wood_texture");
                    southFace.add("uv", createUV(0, isTop ? 8 : 0, 16, isTop ? 16 : 8));
                    faces.add("south", southFace);

                    // East face
                    JsonObject eastFace = new JsonObject();
                    eastFace.addProperty("texture", "#wood_texture");
                    eastFace.add("uv", createUV(16 - depth, isTop ? 8 : 0, 16, isTop ? 16 : 8));
                    faces.add("east", eastFace);

                    // West face
                    JsonObject westFace = new JsonObject();
                    westFace.addProperty("texture", "#wood_texture");
                    westFace.add("uv", createUV(0, isTop ? 8 : 0, depth, isTop ? 16 : 8));
                    faces.add("west", westFace);

                    // Top face
                    JsonObject topFace = new JsonObject();
                    topFace.addProperty("texture", "#wood_texture");
                    if (isTop) topFace.addProperty("cullface", "up");
                    topFace.add("uv", createUV(0, 0, 16, depth));
                    faces.add("up", topFace);

                    // Bottom face
                    JsonObject bottomFace = new JsonObject();
                    bottomFace.addProperty("texture", "#wood_texture");
                    if (!isTop) bottomFace.addProperty("cullface", "down");
                    bottomFace.add("uv", createUV(0, 0, 16, depth));
                    faces.add("down", bottomFace);

                    element.add("faces", faces);
                    elements.add(element);
                    json.add("elements", elements);

                    Identifier modelId = uploadCustomModel(
                            Identifier.of(CakeWood.MOD_ID, modelName),
                            json,
                            generator.modelCollector);

                    // Create variants for every horizontal facing
                    for (Direction facing : Direction.Type.HORIZONTAL) {
                        Property<Integer> bitesProperty = isTop ? CakeWoodBlock.TOP_BITES : CakeWoodBlock.BOTTOM_BITES;
                        Property<Direction> facingProperty = isTop ? CakeWoodBlock.TOP_FACING : CakeWoodBlock.BOTTOM_FACING;

                        MultipartModelConditionBuilder conditionBuilder = BlockStateModelGenerator.createMultipartConditionBuilder()
                                .put(bitesProperty, bites)
                                .put(facingProperty, facing);

                        AxisRotation yRotation = getYRotation(facing);
                        ModelVariant.ModelState modelState = ModelVariant.ModelState.DEFAULT.setRotationY(yRotation);
                        ModelVariant variant = new ModelVariant(modelId, modelState);
                        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(variant);

                        creator.with(conditionBuilder.build(), weightedVariant);
                    }
                }
            }

            generator.blockStateCollector.accept(creator);
        }

        private void generateCornerVariantModel(BlockStateModelGenerator generator,
                                                CakeWoodCornerBlock block,
                                                String variantName,
                                                String textureId) {
            CakeWood.LOGGER.info("Generating corner variant model: " + variantName);
            MultipartBlockModelDefinitionCreator creator = MultipartBlockModelDefinitionCreator.create(block);
            String textureRef = resolveTextureRef(textureId);

            for (int bites = 0; bites < CakeWoodCornerBlock.MAX_BITES; bites++) {
                for (boolean isTop : Arrays.asList(true, false)) {
                    for (CakeWoodCornerBlock.DiagonalDirection facing : CakeWoodCornerBlock.DiagonalDirection.values()) {
                        String modelName = String.format("block/%s_%s_%d_%s",
                                variantName,
                                isTop ? "top" : "bottom",
                                bites,
                                facing.asString());

                        // Build model JSON directly
                        JsonObject json = new JsonObject();
                        json.addProperty("parent", "minecraft:block/block");

                        JsonObject textures = new JsonObject();
                        textures.addProperty("particle", textureRef);
                        textures.addProperty("texture", textureRef);
                        json.add("textures", textures);

                        float size = 16.0f - (bites * (16.0f / CakeWoodCornerBlock.MAX_BITES));
                        float yMin = isTop ? 8.0f : 0.0f;
                        float yMax = isTop ? 16.0f : 8.0f;
                        float xFrom, xTo, zFrom, zTo;

                        switch (facing) {
                            case NORTHWEST:
                                xFrom = 0; xTo = size; zFrom = 0; zTo = size; break;
                            case NORTHEAST:
                                xFrom = 16 - size; xTo = 16; zFrom = 0; zTo = size; break;
                            case SOUTHEAST:
                                xFrom = 16 - size; xTo = 16; zFrom = 16 - size; zTo = 16; break;
                            case SOUTHWEST:
                                xFrom = 0; xTo = size; zFrom = 16 - size; zTo = 16; break;
                            default:
                                xFrom = 0; xTo = size; zFrom = 0; zTo = size; break;
                        }

                        JsonArray elements = new JsonArray();
                        JsonObject element = new JsonObject();
                        JsonArray from = new JsonArray();
                        from.add(xFrom); from.add(yMin); from.add(zFrom);
                        element.add("from", from);
                        JsonArray to = new JsonArray();
                        to.add(xTo); to.add(yMax); to.add(zTo);
                        element.add("to", to);

                        JsonObject faces = new JsonObject();
                        for (String face : new String[]{"north", "south", "east", "west", "up", "down"}) {
                            JsonObject faceObj = new JsonObject();
                            faceObj.addProperty("texture", "#texture");
                            JsonArray uv = new JsonArray();
                            if (face.equals("up") || face.equals("down")) {
                                uv.add(xFrom); uv.add(zFrom); uv.add(xTo); uv.add(zTo);
                            } else {
                                uv.add(xFrom); uv.add(yMin); uv.add(xTo); uv.add(yMax);
                            }
                            faceObj.add("uv", uv);
                            faces.add(face, faceObj);
                        }
                        element.add("faces", faces);
                        elements.add(element);
                        json.add("elements", elements);

                        Identifier modelId = uploadCustomModel(
                                Identifier.of(CakeWood.MOD_ID, modelName),
                                json,
                                generator.modelCollector);

                        // Build condition for this combination
                        Property<Integer> bitesProperty = isTop ? CakeWoodCornerBlock.TOP_BITES : CakeWoodCornerBlock.BOTTOM_BITES;
                        Property<CakeWoodCornerBlock.DiagonalDirection> facingProperty = isTop ? CakeWoodCornerBlock.TOP_FACING : CakeWoodCornerBlock.BOTTOM_FACING;

                        MultipartModelConditionBuilder conditionBuilder = BlockStateModelGenerator.createMultipartConditionBuilder()
                                .put(bitesProperty, bites)
                                .put(facingProperty, facing);

                        ModelVariant variant = new ModelVariant(modelId, ModelVariant.ModelState.DEFAULT);
                        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(variant);

                        creator.with(conditionBuilder.build(), weightedVariant);
                    }
                }
            }

            generator.blockStateCollector.accept(creator);
        }

        // ========================
        // JSON helper methods
        // ========================

        private static JsonArray createUV(float u1, float v1, float u2, float v2) {
            JsonArray uv = new JsonArray();
            uv.add(u1); uv.add(v1); uv.add(u2); uv.add(v2);
            return uv;
        }

        /** Create a simple cuboid element with all faces using the given texture key. */
        private static JsonObject createCuboid(int fromX, int fromY, int fromZ,
                                               int toX, int toY, int toZ,
                                               String textureKey) {
            JsonObject element = new JsonObject();
            JsonArray from = new JsonArray();
            from.add(fromX); from.add(fromY); from.add(fromZ);
            element.add("from", from);
            JsonArray to = new JsonArray();
            to.add(toX); to.add(toY); to.add(toZ);
            element.add("to", to);
            JsonObject faces = new JsonObject();
            for (String face : new String[]{"north", "south", "east", "west", "up", "down"}) {
                JsonObject faceObj = new JsonObject();
                faceObj.addProperty("texture", textureKey);
                faces.add(face, faceObj);
            }
            element.add("faces", faces);
            return element;
        }

        /** Create a corner element for the corner item model. */
        private static JsonObject createCornerElement(int yMin, int yMax, boolean isFullSize) {
            JsonObject element = new JsonObject();
            int size = isFullSize ? 16 : 8;
            JsonArray from = new JsonArray();
            from.add(0); from.add(yMin); from.add(16 - size);
            element.add("from", from);
            JsonArray to = new JsonArray();
            to.add(size); to.add(yMax); to.add(16);
            element.add("to", to);
            JsonObject faces = new JsonObject();
            for (String face : new String[]{"north", "south", "east", "west", "up", "down"}) {
                JsonObject faceObj = new JsonObject();
                faceObj.addProperty("texture", "#all");
                JsonArray uv = new JsonArray();
                switch (face) {
                    case "up", "down" -> {
                        uv.add(0); uv.add(16 - size); uv.add(size); uv.add(16);
                    }
                    case "north", "south" -> {
                        uv.add(0); uv.add(yMin); uv.add(size); uv.add(yMax);
                    }
                    case "east", "west" -> {
                        uv.add(16 - size); uv.add(yMin); uv.add(16); uv.add(yMax);
                    }
                }
                faceObj.add("uv", uv);
                faces.add(face, faceObj);
            }
            element.add("faces", faces);
            return element;
        }
    }
}
