package ninja.trek.cakewood;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A corner variant of the edible cake-wood block with diagonal bite directions.
 *
 * <h3>Block State Properties</h3>
 * <ul>
 *   <li>{@code top_bites} (0–8): Number of bites taken from the top half. 8 = fully eaten.</li>
 *   <li>{@code bottom_bites} (0–8): Number of bites taken from the bottom half.</li>
 *   <li>{@code top_facing} (northwest|northeast|southeast|southwest): Diagonal direction the top
 *       bite occupies. The remaining geometry is anchored to this corner and shrinks as bites
 *       are taken. Set on first bite based on player yaw.</li>
 *   <li>{@code bottom_facing}: Same as top_facing but for the bottom half.</li>
 *   <li>{@code waxed} (true|false): When waxed with honeycomb, the block cannot be eaten.</li>
 * </ul>
 *
 * <h3>Model Generation</h3>
 * Unlike {@link CakeWoodBlock}, corner models do not use Y-rotation. Instead, the diagonal
 * facing direction is baked directly into each model's geometry coordinates. Each combination
 * of (bites, half, diagonal direction) has its own dedicated model file.
 */
public class CakeWoodCornerBlock extends Block {
    public static final int MAX_BITES = 8;
    public static final IntegerProperty TOP_BITES = IntegerProperty.create("top_bites", 0, MAX_BITES);
    public static final IntegerProperty BOTTOM_BITES = IntegerProperty.create("bottom_bites", 0, MAX_BITES);
//    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final BooleanProperty WAXED = BooleanProperty.create("waxed");

    public enum DiagonalDirection implements StringRepresentable {
        NORTHWEST("northwest"),
        NORTHEAST("northeast"),
        SOUTHWEST("southwest"),
        SOUTHEAST("southeast");

        private final String name;

        DiagonalDirection(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public static DiagonalDirection fromPlayerView(Player player, Vec3 hitPos) {

            float yaw = (player.getYRot() % 360 + 360 + 180 - 45) % 360;


            if (yaw >= 315 || yaw < 45) {
                // Player facing NE
                return DiagonalDirection.NORTHEAST;
            } else if (yaw >= 45 && yaw < 135) {
                // Player facing SE
                return DiagonalDirection.SOUTHEAST;
            } else if (yaw >= 135 && yaw < 225) {
                // Player facing SW
                return DiagonalDirection.SOUTHWEST;
            } else {
                // Player facing NW (yaw >= 225 && yaw < 315)
                return DiagonalDirection.NORTHWEST;
            }
        }
    }

    public static final EnumProperty<DiagonalDirection> TOP_FACING =
            EnumProperty.create("top_facing", DiagonalDirection.class);
    public static final EnumProperty<DiagonalDirection> BOTTOM_FACING =
            EnumProperty.create("bottom_facing", DiagonalDirection.class);

    public CakeWoodCornerBlock(Properties settings) {
        super(settings);
        registerDefaultState(getStateDefinition().any()
                .setValue(TOP_BITES, 0)
                .setValue(BOTTOM_BITES, 0)
                .setValue(TOP_FACING, DiagonalDirection.NORTHWEST)
                .setValue(BOTTOM_FACING, DiagonalDirection.NORTHWEST)
                .setValue(WAXED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOP_BITES, BOTTOM_BITES, TOP_FACING, BOTTOM_FACING, WAXED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        int topBites = state.getValue(TOP_BITES);
        int bottomBites = state.getValue(BOTTOM_BITES);
        DiagonalDirection topFacing = state.getValue(TOP_FACING);
        DiagonalDirection bottomFacing = state.getValue(BOTTOM_FACING);

        VoxelShape topShape = topBites >= MAX_BITES ? Shapes.empty() :
                getHalfShape(topBites, true, topFacing);
        VoxelShape bottomShape = bottomBites >= MAX_BITES ? Shapes.empty() :
                getHalfShape(bottomBites, false, bottomFacing);

        return Shapes.or(topShape, bottomShape);
    }

    private VoxelShape getHalfShape(int bites, boolean isTop, DiagonalDirection facing) {
        if (bites >= MAX_BITES) {
            return Shapes.empty();
        }
        // Compute how much of the block remains (using a 16‐pixel “grid”)
        float biteSize = bites * (16.0f / MAX_BITES);
        float size = 16.0f - biteSize; // in “pixels”
        float fraction = size / 16.0f; // normalized [0,1]
        float yMin = isTop ? 0.5f : 0.0f;
        float yMax = isTop ? 1.0f : 0.5f;

        return switch (facing) {
            case NORTHWEST -> Shapes.box(
                    0.0f, yMin, 0.0f,
                    fraction, yMax, fraction
            );
            case NORTHEAST -> Shapes.box(
                    1.0f - fraction, yMin, 0.0f,
                    1.0f, yMax, fraction
            );
            case SOUTHEAST -> Shapes.box(
                    1.0f - fraction, yMin, 1.0f - fraction,
                    1.0f, yMax, 1.0f
            );
            case SOUTHWEST -> Shapes.box(
                    0.0f, yMin, 1.0f - fraction,
                    fraction, yMax, 1.0f
            );
        };
    }


    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        ItemStack stack = player.getMainHandItem();

        // Handle waxing with honeycomb
        if (stack.getItem() instanceof HoneycombItem && !state.getValue(WAXED)) {
            if (!world.isClientSide()) {
                world.setBlockAndUpdate(pos, state.setValue(WAXED, true));
                world.playSound(null, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            }
            return InteractionResult.SUCCESS;
        }

        // Handle unwaxing with axe
        if (stack.getItem() instanceof AxeItem && state.getValue(WAXED)) {
            if (!world.isClientSide()) {
                world.setBlockAndUpdate(pos, state.setValue(WAXED, false));
                world.playSound(null, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (!player.isCreative()) {
                    stack.setDamageValue(stack.getDamageValue() + 1);
                }
                player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            }
            return InteractionResult.SUCCESS;
        }

        // Handle stripping with axe (unwaxed block)
        if (stack.getItem() instanceof AxeItem && !state.getValue(WAXED)) {
            Block strippedBlock = CakeWoodRegistry.getStrippedBlock(state.getBlock());
            if (strippedBlock != null) {
                if (!world.isClientSide()) {
                    BlockState strippedState = strippedBlock.defaultBlockState()
                            .setValue(TOP_BITES, state.getValue(TOP_BITES))
                            .setValue(BOTTOM_BITES, state.getValue(BOTTOM_BITES))
                            .setValue(TOP_FACING, state.getValue(TOP_FACING))
                            .setValue(BOTTOM_FACING, state.getValue(BOTTOM_FACING))
                            .setValue(WAXED, false);
                    world.setBlockAndUpdate(pos, strippedState);
                    world.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);
                    if (!player.isCreative()) {
                        stack.setDamageValue(stack.getDamageValue() + 1);
                    }
                    player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                }
                return InteractionResult.SUCCESS;
            }
        }

        // If waxed, prevent eating
        if (state.getValue(WAXED)) {
            return InteractionResult.PASS;
        }

        // Original eating logic
        if (world.isClientSide()) {
            if (canEatCakeWood(state, player, hit, pos)) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        return eatCakeWood(world, pos, state, player, hit);
    }

    private boolean canEatCakeWood(BlockState state, Player player, BlockHitResult hit, BlockPos pos) {
        if (!player.canEat(true)) {
            return false;
        }

        Vec3 hitPos = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        boolean isTopHalf = hitPos.y >= 0.5;

        int topBites = state.getValue(TOP_BITES);
        int bottomBites = state.getValue(BOTTOM_BITES);
        DiagonalDirection topFacing = state.getValue(TOP_FACING);
        DiagonalDirection bottomFacing = state.getValue(BOTTOM_FACING);

        if (isTopHalf && !doesPointIntersectHalf(hitPos, topBites, true, topFacing)) {
            if (doesPointIntersectHalf(hitPos, bottomBites, false, bottomFacing)) {
                isTopHalf = false;
            } else {
                return false;
            }
        }

        IntegerProperty bitesProp = isTopHalf ? TOP_BITES : BOTTOM_BITES;
        int bites = state.getValue(bitesProp);
        return bites < MAX_BITES;
    }

    private InteractionResult eatCakeWood(Level world, BlockPos pos, BlockState state, Player player, BlockHitResult hit) {
        if (!player.canEat(true)) {
            return InteractionResult.PASS;
        }

        Vec3 hitPos = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        boolean isTopHalf = hitPos.y >= 0.5;

        int topBites = state.getValue(TOP_BITES);
        int bottomBites = state.getValue(BOTTOM_BITES);
        DiagonalDirection topFacing = state.getValue(TOP_FACING);
        DiagonalDirection bottomFacing = state.getValue(BOTTOM_FACING);

        // Check if the hit position intersects with the remaining cake shape
        if (isTopHalf && !doesPointIntersectHalf(hitPos, topBites, true, topFacing)) {
            if (doesPointIntersectHalf(hitPos, bottomBites, false, bottomFacing)) {
                isTopHalf = false;
            } else {
                return InteractionResult.PASS;
            }
        }

        IntegerProperty bitesProp = isTopHalf ? TOP_BITES : BOTTOM_BITES;
        EnumProperty<DiagonalDirection> facingProp = isTopHalf ? TOP_FACING : BOTTOM_FACING;
        int bites = state.getValue(bitesProp);

        if (bites >= MAX_BITES) {
            return InteractionResult.PASS;
        }

        // Use the new direction logic for the first bite
        DiagonalDirection facing = bites == 0
                ? DiagonalDirection.fromPlayerView(player, hitPos)
                : state.getValue(facingProp);

        BlockState newState = state.setValue(bitesProp, bites + 1)
                .setValue(facingProp, facing);

        world.setBlock(pos, newState,
                Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE | Block.UPDATE_KNOWN_SHAPE);

        if (newState.getValue(TOP_BITES) >= MAX_BITES &&
                newState.getValue(BOTTOM_BITES) >= MAX_BITES) {
            world.removeBlock(pos, false);
            world.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        } else {
            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }

        player.getFoodData().eat(2, 0.1F);

        // Play eating and breaking sounds
        world.playSound(null, pos,
                SoundEvents.GENERIC_EAT.value(),
                SoundSource.BLOCKS,
                0.5f,
                world.getRandom().nextFloat() * 0.1f + 0.9f
        );
        world.playSound(null, pos,
                SoundEvents.WOOD_BREAK,
                SoundSource.BLOCKS,
                0.5f,
                world.getRandom().nextFloat() * 0.1f + 0.9f
        );

        return InteractionResult.SUCCESS;
    }

    private boolean doesPointIntersectHalf(Vec3 point, int bites, boolean isTop, DiagonalDirection facing) {
        if (bites >= MAX_BITES) {
            return false;
        }
        double yMin = isTop ? 0.5 : 0.0;
        double yMax = isTop ? 1.0 : 0.5;
        if (point.y < yMin || point.y > yMax) {
            return false;
        }
        double fraction = 1.0 - ((double) bites / MAX_BITES);
        return switch (facing) {
            case NORTHWEST -> (point.x >= 0.0 && point.x <= fraction) &&
                    (point.z >= 0.0 && point.z <= fraction);
            case NORTHEAST -> (point.x >= 1.0 - fraction && point.x <= 1.0) &&
                    (point.z >= 0.0 && point.z <= fraction);
            case SOUTHEAST -> (point.x >= 1.0 - fraction && point.x <= 1.0) &&
                    (point.z >= 1.0 - fraction && point.z <= 1.0);
            case SOUTHWEST -> (point.x >= 0.0 && point.x <= fraction) &&
                    (point.z >= 1.0 - fraction && point.z <= 1.0);
        };
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        // Just return default state - direction will be set on first bite
        return defaultBlockState();
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return Math.max(MAX_BITES - state.getValue(TOP_BITES), MAX_BITES - state.getValue(BOTTOM_BITES));
    }
}
