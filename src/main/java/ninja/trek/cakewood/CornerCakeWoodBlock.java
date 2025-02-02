package ninja.trek.cakewood;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
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
import net.minecraft.util.StringIdentifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ninja.trek.cakewood.CakeWood.MOD_ID;

public class CornerCakeWoodBlock extends Block {
    public static final int MAX_BITES = 8;
    public static final IntProperty TOP_BITES = IntProperty.of("top_bites", 0, MAX_BITES);
    public static final IntProperty BOTTOM_BITES = IntProperty.of("bottom_bites", 0, MAX_BITES);
//    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public enum DiagonalDirection implements StringIdentifiable {
        NORTHWEST("northwest"),
        NORTHEAST("northeast"),
        SOUTHWEST("southwest"),
        SOUTHEAST("southeast");

        private final String name;

        DiagonalDirection(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public static DiagonalDirection fromPlayerView(PlayerEntity player, Vec3d hitPos) {

            float yaw = (player.getYaw() % 360 + 360 + 180 - 45) % 360;


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
            EnumProperty.of("top_facing", DiagonalDirection.class);
    public static final EnumProperty<DiagonalDirection> BOTTOM_FACING =
            EnumProperty.of("bottom_facing", DiagonalDirection.class);

    public CornerCakeWoodBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(TOP_BITES, 0)
                .with(BOTTOM_BITES, 0)
                .with(TOP_FACING, DiagonalDirection.NORTHWEST)
                .with(BOTTOM_FACING, DiagonalDirection.NORTHWEST));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TOP_BITES, BOTTOM_BITES, TOP_FACING, BOTTOM_FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int topBites = state.get(TOP_BITES);
        int bottomBites = state.get(BOTTOM_BITES);
        DiagonalDirection topFacing = state.get(TOP_FACING);
        DiagonalDirection bottomFacing = state.get(BOTTOM_FACING);

        VoxelShape topShape = topBites >= MAX_BITES ? VoxelShapes.empty() :
                getHalfShape(topBites, true, topFacing);
        VoxelShape bottomShape = bottomBites >= MAX_BITES ? VoxelShapes.empty() :
                getHalfShape(bottomBites, false, bottomFacing);

        return VoxelShapes.union(topShape, bottomShape);
    }

    private VoxelShape getHalfShape(int bites, boolean isTop, DiagonalDirection facing) {
        if (bites >= MAX_BITES) {
            return VoxelShapes.empty();
        }
        // Compute how much of the block remains (using a 16‐pixel “grid”)
        float biteSize = bites * (16.0f / MAX_BITES);
        float size = 16.0f - biteSize; // in “pixels”
        float fraction = size / 16.0f; // normalized [0,1]
        float yMin = isTop ? 0.5f : 0.0f;
        float yMax = isTop ? 1.0f : 0.5f;

        return switch (facing) {
            case NORTHWEST -> VoxelShapes.cuboid(
                    0.0f, yMin, 0.0f,
                    fraction, yMax, fraction
            );
            case NORTHEAST -> VoxelShapes.cuboid(
                    1.0f - fraction, yMin, 0.0f,
                    1.0f, yMax, fraction
            );
            case SOUTHEAST -> VoxelShapes.cuboid(
                    1.0f - fraction, yMin, 1.0f - fraction,
                    1.0f, yMax, 1.0f
            );
            case SOUTHWEST -> VoxelShapes.cuboid(
                    0.0f, yMin, 1.0f - fraction,
                    fraction, yMax, 1.0f
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
        if (!player.canConsume(true)) {
            return ActionResult.PASS;
        }

        Vec3d hitPos = hit.getPos().subtract(pos.getX(), pos.getY(), pos.getZ());
        boolean isTopHalf = hitPos.y >= 0.5;

        int topBites = state.get(TOP_BITES);
        int bottomBites = state.get(BOTTOM_BITES);
        DiagonalDirection topFacing = state.get(TOP_FACING);
        DiagonalDirection bottomFacing = state.get(BOTTOM_FACING);

        // Check if the hit position intersects with the remaining cake shape
        if (isTopHalf && !doesPointIntersectHalf(hitPos, topBites, true, topFacing)) {
            if (doesPointIntersectHalf(hitPos, bottomBites, false, bottomFacing)) {
                isTopHalf = false;
            } else {
                return ActionResult.PASS;
            }
        }

        IntProperty bitesProp = isTopHalf ? TOP_BITES : BOTTOM_BITES;
        EnumProperty<DiagonalDirection> facingProp = isTopHalf ? TOP_FACING : BOTTOM_FACING;
        int bites = state.get(bitesProp);

        if (bites >= MAX_BITES) {
            return ActionResult.PASS;
        }

        // Use the new direction logic for the first bite
        DiagonalDirection facing = bites == 0
                ? DiagonalDirection.fromPlayerView(player, hitPos)
                : state.get(facingProp);
//        LOGGER.info("facing " + facing);
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

        // Play eating and breaking sounds
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

    private boolean doesPointIntersectHalf(Vec3d point, int bites, boolean isTop, DiagonalDirection facing) {
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
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // Just return default state - direction will be set on first bite
        return getDefaultState();
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