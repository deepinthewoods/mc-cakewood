package ninja.trek.cakewood;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;

public class CakeWoodBlock extends Block {
    public static final int MAX_BITES = 8;
    public static final IntProperty TOP_BITES = IntProperty.of("top_bites", 0, MAX_BITES);
    public static final IntProperty BOTTOM_BITES = IntProperty.of("bottom_bites", 0, MAX_BITES);
    public static final DirectionProperty TOP_FACING = DirectionProperty.of("top_facing",
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    public static final DirectionProperty BOTTOM_FACING = DirectionProperty.of("bottom_facing",
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    public static final BooleanProperty WAXED = BooleanProperty.of("waxed");

    public CakeWoodBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(TOP_BITES, 0)
                .with(BOTTOM_BITES, 0)
                .with(TOP_FACING, Direction.NORTH)
                .with(BOTTOM_FACING, Direction.NORTH)
                .with(WAXED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TOP_BITES, BOTTOM_BITES, TOP_FACING, BOTTOM_FACING, WAXED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int topBites = state.get(TOP_BITES);
        int bottomBites = state.get(BOTTOM_BITES);
        Direction topFacing = state.get(TOP_FACING);
        Direction bottomFacing = state.get(BOTTOM_FACING);

        VoxelShape topShape = topBites >= MAX_BITES ? null : getHalfShape(topBites, true, topFacing);
        VoxelShape bottomShape = bottomBites >= MAX_BITES ? null : getHalfShape(bottomBites, false, bottomFacing);

        if (topShape == null && bottomShape == null) {
            return VoxelShapes.empty();
        } else if (topShape == null) {
            return bottomShape;
        } else if (bottomShape == null) {
            return topShape;
        }
        return VoxelShapes.union(topShape, bottomShape);
    }

    private VoxelShape getHalfShape(int bites, boolean isTop, Direction facing) {
        if (bites >= MAX_BITES) {
            return VoxelShapes.empty();
        }

        float biteSize = bites * 2.0f; // Each bite is 2 pixels deep
        float yMin = isTop ? 0.5f : 0f;
        float yMax = isTop ? 1.0f : 0.5f;

        return switch (facing) {
            case NORTH -> VoxelShapes.cuboid(
                    0.0f,                       // xMin
                    yMin,                       // yMin
                    0.0f + biteSize/16.0f,      // zMin (adjusted by bites)
                    1.0f,                       // xMax
                    yMax,                       // yMax
                    1.0f                        // zMax
            );
            case SOUTH -> VoxelShapes.cuboid(
                    0.0f,                       // xMin
                    yMin,                       // yMin
                    0.0f,                       // zMin
                    1.0f,                       // xMax
                    yMax,                       // yMax
                    1.0f - biteSize/16.0f       // zMax (adjusted by bites)
            );
            case WEST -> VoxelShapes.cuboid(
                    0.0f + biteSize/16.0f,      // xMin (adjusted by bites)
                    yMin,                       // yMin
                    0.0f,                       // zMin
                    1.0f,                       // xMax
                    yMax,                       // yMax
                    1.0f                        // zMax
            );
            case EAST -> VoxelShapes.cuboid(
                    0.0f,                       // xMin
                    yMin,                       // yMin
                    0.0f,                       // zMin
                    1.0f - biteSize/16.0f,      // xMax (adjusted by bites)
                    yMax,                       // yMax
                    1.0f                        // zMax
            );
            default -> VoxelShapes.cuboid(
                    0.0f, yMin, 0.0f,
                    1.0f, yMax, 1.0f
            );
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(player.preferredHand);

        // Handle waxing with honeycomb
        if (stack.getItem() instanceof HoneycombItem && !state.get(WAXED)) {
            if (!world.isClient) {
                world.setBlockState(pos, state.with(WAXED, true));
                world.playSound(null, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
            }
            return ActionResult.success(world.isClient);
        }

        // Handle unwaxing with axe
        if (stack.getItem() instanceof AxeItem && state.get(WAXED)) {
            if (!world.isClient) {
                world.setBlockState(pos, state.with(WAXED, false));
                world.playSound(null, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f);
                stack.setDamage(stack.getDamage() + 1);
                player.swingHand(player.preferredHand);
            }
            return ActionResult.success(world.isClient);
        }

        // If waxed, prevent eating
        if (state.get(WAXED)) {
            return ActionResult.PASS;
        }

        // Original eating logic
        if (world.isClient) {
            if (eatCakeWood(world, pos, state, player, hit).isAccepted()) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.CONSUME;
        }
        return eatCakeWood(world, pos, state, player, hit);
    }

    private ActionResult eatCakeWood(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockHitResult hit) {
        if (!player.canConsume(true)) {
            return ActionResult.PASS;
        }

        Vec3d hitPos = hit.getPos().subtract(pos.getX(), pos.getY(), pos.getZ());
        boolean isTopHalf = hitPos.y >= 0.5;

        int topBites = state.get(TOP_BITES);
        int bottomBites = state.get(BOTTOM_BITES);
        Direction topFacing = state.get(TOP_FACING);
        Direction bottomFacing = state.get(BOTTOM_FACING);

        if (isTopHalf) {
            if (!doesPointIntersectHalf(hitPos, topBites, true, topFacing)) {
                if (doesPointIntersectHalf(hitPos, bottomBites, false, bottomFacing)) {
                    isTopHalf = false;
                } else {
                    return ActionResult.PASS;
                }
            }
        } else {
            if (!doesPointIntersectHalf(hitPos, bottomBites, false, bottomFacing)) {
                return ActionResult.PASS;
            }
        }

        IntProperty bitesProp = isTopHalf ? TOP_BITES : BOTTOM_BITES;
        DirectionProperty facingProp = isTopHalf ? TOP_FACING : BOTTOM_FACING;
        int bites = state.get(bitesProp);

        if (bites >= MAX_BITES) {
            return ActionResult.PASS;
        }

        Direction facing = bites == 0
                ? Direction.fromHorizontal((int)((player.getYaw() * 4.0f / 360.0f) + 2.5f) & 3)
                : state.get(facingProp);

        BlockState newState = state.with(bitesProp, bites + 1)
                .with(facingProp, facing);

        world.setBlockState(pos, newState,
                Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD | Block.FORCE_STATE);

        if (newState.get(TOP_BITES) >= MAX_BITES &&
                newState.get(BOTTOM_BITES) >= MAX_BITES) {
            world.removeBlock(pos, false);
            world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        } else {
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }

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

    private boolean doesPointIntersectHalf(Vec3d point, int bites, boolean isTop, Direction facing) {
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
        return Math.max(MAX_BITES - state.get(TOP_BITES), MAX_BITES - state.get(BOTTOM_BITES));
    }
}