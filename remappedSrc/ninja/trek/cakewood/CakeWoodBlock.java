package ninja.trek.cakewood;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
 * An edible block that can be bitten from the top and bottom halves independently.
 *
 * <h3>Block State Properties</h3>
 * <ul>
 *   <li>{@code top_bites} (0–8): Number of bites taken from the top half. 8 = fully eaten.</li>
 *   <li>{@code bottom_bites} (0–8): Number of bites taken from the bottom half. 8 = fully eaten.</li>
 *   <li>{@code top_facing} (north|east|south|west): Cardinal direction the top bite approaches FROM.
 *       Set on first bite based on player yaw. The block geometry shrinks inward from this side.</li>
 *   <li>{@code bottom_facing}: Same as top_facing but for the bottom half.</li>
 *   <li>{@code waxed} (true|false): When waxed with honeycomb, the block cannot be eaten.
 *       Use an axe to remove wax.</li>
 * </ul>
 *
 * <h3>Model Rotation (Data Generator)</h3>
 * The base block model is built with the bite opening facing SOUTH (geometry extends from z=0 to
 * z=depth). The data generator maps facing→Y rotation as follows:
 * <ul>
 *   <li>SOUTH → R0 (y=0, no rotation needed — bite already on south)</li>
 *   <li>WEST  → R90 (y=90, rotates bite from south to west)</li>
 *   <li>NORTH → R180 (y=180, rotates bite from south to north)</li>
 *   <li>EAST  → R270 (y=270, rotates bite from south to east)</li>
 * </ul>
 */
public class CakeWoodBlock extends Block {
    public static final int MAX_BITES = 8;
    public static final IntegerProperty TOP_BITES = IntegerProperty.create("top_bites", 0, MAX_BITES);
    public static final IntegerProperty BOTTOM_BITES = IntegerProperty.create("bottom_bites", 0, MAX_BITES);
    public static final EnumProperty<Direction> TOP_FACING = EnumProperty.create("top_facing", Direction.class,
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    public static final EnumProperty<Direction> BOTTOM_FACING = EnumProperty.create("bottom_facing", Direction.class,
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    public static final BooleanProperty WAXED = BooleanProperty.create("waxed");

    public CakeWoodBlock(Properties settings) {
        super(settings);
        registerDefaultState(getStateDefinition().any()
                .setValue(TOP_BITES, 0)
                .setValue(BOTTOM_BITES, 0)
                .setValue(TOP_FACING, Direction.NORTH)
                .setValue(BOTTOM_FACING, Direction.NORTH)
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
        Direction topFacing = state.getValue(TOP_FACING);
        Direction bottomFacing = state.getValue(BOTTOM_FACING);

        VoxelShape topShape = topBites >= MAX_BITES ? null : getHalfShape(topBites, true, topFacing);
        VoxelShape bottomShape = bottomBites >= MAX_BITES ? null : getHalfShape(bottomBites, false, bottomFacing);

        if (topShape == null && bottomShape == null) {
            return Shapes.empty();
        } else if (topShape == null) {
            return bottomShape;
        } else if (bottomShape == null) {
            return topShape;
        }
        return Shapes.or(topShape, bottomShape);
    }

    private VoxelShape getHalfShape(int bites, boolean isTop, Direction facing) {
        if (bites >= MAX_BITES) {
            return Shapes.empty();
        }

        float biteSize = bites * 2.0f; // Each bite is 2 pixels deep
        float yMin = isTop ? 0.5f : 0f;
        float yMax = isTop ? 1.0f : 0.5f;

        return switch (facing) {
            case NORTH -> Shapes.box(
                    0.0f,                       // xMin
                    yMin,                       // yMin
                    0.0f + biteSize/16.0f,      // zMin (adjusted by bites)
                    1.0f,                       // xMax
                    yMax,                       // yMax
                    1.0f                        // zMax
            );
            case SOUTH -> Shapes.box(
                    0.0f,                       // xMin
                    yMin,                       // yMin
                    0.0f,                       // zMin
                    1.0f,                       // xMax
                    yMax,                       // yMax
                    1.0f - biteSize/16.0f       // zMax (adjusted by bites)
            );
            case WEST -> Shapes.box(
                    0.0f + biteSize/16.0f,      // xMin (adjusted by bites)
                    yMin,                       // yMin
                    0.0f,                       // zMin
                    1.0f,                       // xMax
                    yMax,                       // yMax
                    1.0f                        // zMax
            );
            case EAST -> Shapes.box(
                    0.0f,                       // xMin
                    yMin,                       // yMin
                    0.0f,                       // zMin
                    1.0f - biteSize/16.0f,      // xMax (adjusted by bites)
                    yMax,                       // yMax
                    1.0f                        // zMax
            );
            default -> Shapes.box(
                    0.0f, yMin, 0.0f,
                    1.0f, yMax, 1.0f
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
            }
            return InteractionResult.SUCCESS;
        }

        // Handle unwaxing with axe
        if (stack.getItem() instanceof AxeItem && state.getValue(WAXED)) {
            if (!world.isClientSide()) {
                world.setBlockAndUpdate(pos, state.setValue(WAXED, false));
                world.playSound(null, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0f, 1.0f);
                stack.setDamageValue(stack.getDamageValue() + 1);
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
                    stack.setDamageValue(stack.getDamageValue() + 1);
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
        Direction topFacing = state.getValue(TOP_FACING);
        Direction bottomFacing = state.getValue(BOTTOM_FACING);

        if (isTopHalf) {
            if (!doesPointIntersectHalf(hitPos, topBites, true, topFacing)) {
                if (doesPointIntersectHalf(hitPos, bottomBites, false, bottomFacing)) {
                    isTopHalf = false;
                } else {
                    return false;
                }
            }
        } else {
            if (!doesPointIntersectHalf(hitPos, bottomBites, false, bottomFacing)) {
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
        Direction topFacing = state.getValue(TOP_FACING);
        Direction bottomFacing = state.getValue(BOTTOM_FACING);

        if (isTopHalf) {
            if (!doesPointIntersectHalf(hitPos, topBites, true, topFacing)) {
                if (doesPointIntersectHalf(hitPos, bottomBites, false, bottomFacing)) {
                    isTopHalf = false;
                } else {
                    return InteractionResult.PASS;
                }
            }
        } else {
            if (!doesPointIntersectHalf(hitPos, bottomBites, false, bottomFacing)) {
                return InteractionResult.PASS;
            }
        }

        IntegerProperty bitesProp = isTopHalf ? TOP_BITES : BOTTOM_BITES;
        EnumProperty<Direction> facingProp = isTopHalf ? TOP_FACING : BOTTOM_FACING;
        int bites = state.getValue(bitesProp);

        if (bites >= MAX_BITES) {
            return InteractionResult.PASS;
        }

        Direction facing = bites == 0
                ? Direction.from2DDataValue((int)((player.getYRot() * 4.0f / 360.0f) + 2.5f) & 3)
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
        world.playSound(null, pos,
                SoundEvents.GENERIC_EAT.value(),
                SoundSource.BLOCKS,
                0.5f,
                world.random.nextFloat() * 0.1f + 0.9f
        );
        world.playSound(null, pos,
                SoundEvents.WOOD_BREAK,
                SoundSource.BLOCKS,
                0.5f,
                world.random.nextFloat() * 0.1f + 0.9f
        );

        return InteractionResult.SUCCESS;
    }

    private boolean doesPointIntersectHalf(Vec3 point, int bites, boolean isTop, Direction facing) {
        if (bites >= MAX_BITES) {
            return false;
        }

        double yMin = isTop ? 0.5 : 0.0;
        double yMax = isTop ? 1.0 : 0.5;

        if (point.y < yMin || point.y > yMax) {
            return false;
        }

        double biteDepth = bites * (2.0/16.0);
        return switch (facing) {
            case NORTH -> point.z >= biteDepth;
            case SOUTH -> point.z <= (1.0 - biteDepth);
            case WEST -> point.x >= biteDepth;
            case EAST -> point.x <= (1.0 - biteDepth);
            default -> true;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState()
                .setValue(TOP_FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(BOTTOM_FACING, ctx.getHorizontalDirection().getOpposite());
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