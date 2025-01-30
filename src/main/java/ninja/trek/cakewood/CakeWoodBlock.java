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
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
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
    protected static final VoxelShape[] TOP_SHAPES = new VoxelShape[8];
    protected static final VoxelShape[] BOTTOM_SHAPES = new VoxelShape[8];

    static {
        // Define shapes for top half
        for (int i = 0; i < 8; i++) {
            float height = 8.0f - i;
            TOP_SHAPES[i] = VoxelShapes.cuboid(0.0625f, 0.5f, 0.0625f,
                    0.9375f, 0.5f + (height/16.0f), 0.9375f);
        }
        // Define shapes for bottom half
        for (int i = 0; i < 8; i++) {
            float height = 8.0f - i;
            BOTTOM_SHAPES[i] = VoxelShapes.cuboid(0.0625f, 0.0f, 0.0625f,
                    0.9375f, height/16.0f, 0.9375f);
        }
    }

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
        VoxelShape topShape = TOP_SHAPES[state.get(TOP_BITES)];
        VoxelShape bottomShape = BOTTOM_SHAPES[state.get(BOTTOM_BITES)];
        return VoxelShapes.union(topShape, bottomShape);
    }

    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world == null || player == null) {
            return ActionResult.PASS;
        }

        if (world.isClient) {
            if (tryEat(world, pos, state, player, hit).isAccepted()) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.CONSUME;
        }

        return tryEat(world, pos, state, player, hit);
    }

    private ActionResult tryEat(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockHitResult hit) {
        if (!player.canConsume(false)) {
            return ActionResult.PASS;
        }

        // Determine which layer was clicked
        boolean isTopLayer = hit.getPos().y - pos.getY() >= 0.5;
        int bites = isTopLayer ? state.get(TOP_BITES) : state.get(BOTTOM_BITES);
        Direction facing = isTopLayer ? state.get(TOP_FACING) : state.get(BOTTOM_FACING);

        // Check if layer is fully eaten
        if (bites >= MAX_BITES) {
            return ActionResult.PASS;
        }

        // Set initial facing based on player position if this is first bite
        if (bites == 0) {
            facing = Direction.fromHorizontal(
                    (int)((player.getYaw() * 4.0f / 360.0f) + 0.5f) & 3
            ).getOpposite();
        }

        // Update the block state
        BlockState newState = state.with(
                isTopLayer ? TOP_BITES : BOTTOM_BITES, bites + 1
        ).with(
                isTopLayer ? TOP_FACING : BOTTOM_FACING, facing
        );

        // Check if both layers are fully eaten
        if (newState.get(TOP_BITES) >= MAX_BITES &&
                newState.get(BOTTOM_BITES) >= MAX_BITES) {
            world.removeBlock(pos, false);
        } else {
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
        }

        // Apply eating effects
        player.getHungerManager().add(2, 0.1F);
        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EAT,
                SoundCategory.BLOCKS, 1.0F, 1.0F);

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
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