package ninja.trek.cakewood;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;

public class CakeWoodBlock extends Block {
    public static final int MAX_BITES = 7;
    public static final IntProperty TOP_BITES = IntProperty.of("top_bites", 0, MAX_BITES);
    public static final IntProperty BOTTOM_BITES = IntProperty.of("bottom_bites", 0, MAX_BITES);
    public static final DirectionProperty TOP_FACING = DirectionProperty.of("top_facing",
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    public static final DirectionProperty BOTTOM_FACING = DirectionProperty.of("bottom_facing",
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    public CakeWoodBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(TOP_BITES, 0)
                .with(BOTTOM_BITES, 0)
                .with(TOP_FACING, Direction.NORTH)
                .with(BOTTOM_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TOP_BITES, BOTTOM_BITES, TOP_FACING, BOTTOM_FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int topBites = state.get(TOP_BITES);
        int bottomBites = state.get(BOTTOM_BITES);
        Direction topFacing = state.get(TOP_FACING);
        Direction bottomFacing = state.get(BOTTOM_FACING);

        return VoxelShapes.union(
                getHalfShape(topBites, true, topFacing),
                getHalfShape(bottomBites, false, bottomFacing)
        );
    }

    private VoxelShape getHalfShape(int bites, boolean isTop, Direction facing) {
        if (bites >= MAX_BITES) {
            return VoxelShapes.empty();
        }

        float biteSize = bites * 2.0f; // Each bite is 2 pixels deep
        float yMin = isTop ? 0.5f : 0f;
        float yMax = isTop ? 1.0f : 0.5f;

        // Create the base shape based on the direction of bites
        return switch (facing) {
            case NORTH -> VoxelShapes.cuboid(
                    0.0625f,                    // xMin (1/16)
                    yMin,                       // yMin
                    0.0625f + biteSize/16.0f,   // zMin (adjusted by bites)
                    0.9375f,                    // xMax (15/16)
                    yMax,                       // yMax
                    0.9375f                     // zMax (15/16)
            );
            case SOUTH -> VoxelShapes.cuboid(
                    0.0625f,                    // xMin
                    yMin,                       // yMin
                    0.0625f,                    // zMin
                    0.9375f,                    // xMax
                    yMax,                       // yMax
                    0.9375f - biteSize/16.0f    // zMax (adjusted by bites)
            );
            case WEST -> VoxelShapes.cuboid(
                    0.0625f + biteSize/16.0f,   // xMin (adjusted by bites)
                    yMin,                       // yMin
                    0.0625f,                    // zMin
                    0.9375f,                    // xMax
                    yMax,                       // yMax
                    0.9375f                     // zMax
            );
            case EAST -> VoxelShapes.cuboid(
                    0.0625f,                    // xMin
                    yMin,                       // yMin
                    0.0625f,                    // zMin
                    0.9375f - biteSize/16.0f,   // xMax (adjusted by bites)
                    yMax,                       // yMax
                    0.9375f                     // zMax
            );
            default -> VoxelShapes.cuboid(
                    0.0625f, yMin, 0.0625f,
                    0.9375f, yMax, 0.9375f
            );
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            if (eatCakeWood(world, pos, state, player, hit).isAccepted()) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.CONSUME;
        }

        return eatCakeWood(world, pos, state, player, hit);
    }

    private ActionResult eatCakeWood(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockHitResult hit) {
        if (!player.canConsume(true)) {  // Allow eating even when not hungry
            return ActionResult.PASS;
        }

        // Get which half was clicked
        boolean isTopHalf = hit.getPos().y - pos.getY() >= 0.5;
        IntProperty bitesProp = isTopHalf ? TOP_BITES : BOTTOM_BITES;
        DirectionProperty facingProp = isTopHalf ? TOP_FACING : BOTTOM_FACING;
        int bites = state.get(bitesProp);

        if (bites >= MAX_BITES) {
            return ActionResult.PASS;
        }

        // Update facing based on player position for first bite
        Direction facing = bites == 0
                ? Direction.fromHorizontal((int)((player.getYaw() * 4.0f / 360.0f) + 0.5f) & 3).getOpposite()
                : state.get(facingProp);

        // Create new state with updated bites and facing
        BlockState newState = state.with(bitesProp, bites + 1)
                .with(facingProp, facing);

        // Apply the new state
        world.setBlockState(pos, newState,
                Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD | Block.FORCE_STATE);

        // Check if block should be removed
        if (newState.get(TOP_BITES) >= MAX_BITES &&
                newState.get(BOTTOM_BITES) >= MAX_BITES) {
            world.removeBlock(pos, false);
            world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        } else {
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }

        // Apply effects
        player.getHungerManager().add(2, 0.1F);
        world.playSound(null, pos,
                SoundEvents.ENTITY_GENERIC_EAT,
                SoundCategory.BLOCKS,
                0.5f,
                world.random.nextFloat() * 0.1f + 0.9f
        );
        world.playSound(null, pos,
                SoundEvents.BLOCK_WOOD_BREAK,
                SoundCategory.BLOCKS,
                0.5f,
                world.random.nextFloat() * 0.1f + 0.9f
        );

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
                .with(TOP_FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(BOTTOM_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return Math.max(7 - state.get(TOP_BITES), 7 - state.get(BOTTOM_BITES));
    }
}