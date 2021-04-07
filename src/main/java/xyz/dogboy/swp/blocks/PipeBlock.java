package xyz.dogboy.swp.blocks;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;

import net.minecraft.block.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xyz.dogboy.swp.tiles.PipeTileEntity;
import xyz.dogboy.swp.utils.PipeNetworkHelper;
import xyz.dogboy.swp.utils.SupportedWoodType;

public class PipeBlock extends DynamicShapeBlock {


    public static final Map<Direction, BooleanProperty> DIRECTION_PROPERTY_MAP = new EnumMap<>(Direction.class);
    public static final Map<Direction, BooleanProperty> DIRECTION_EXTRACTION_PROPERTY_MAP = new EnumMap<>(Direction.class);

    private SupportedWoodType woodType;

    static {
        for(Direction direction: Direction.values()) {
            DIRECTION_PROPERTY_MAP.put(direction, BooleanProperty.create(direction.getName().toLowerCase()));
            DIRECTION_EXTRACTION_PROPERTY_MAP.put(direction, BooleanProperty.create("extract_" + direction.getName().toLowerCase()));
        }
    }
    private static final VoxelShape MIDDLE_SHAPE = Block.box(4, 4, 4, 12, 12, 12);
    private static final Map<Direction, VoxelShape> PIPE_EXTENSION_SHAPE_MAP = new EnumMap<>(Direction.class);
    private static final Map<BlockState, VoxelShape> SHAPE_CACHE = new HashMap<>();
    static {
        PIPE_EXTENSION_SHAPE_MAP.put(Direction.NORTH,Block.box(5, 5, 0, 11, 11, 4));
        PIPE_EXTENSION_SHAPE_MAP.put(Direction.EAST,Block.box(12, 5, 5, 16, 11, 11));
        PIPE_EXTENSION_SHAPE_MAP.put(Direction.SOUTH,Block.box(5, 5, 12, 11, 11, 16));
        PIPE_EXTENSION_SHAPE_MAP.put(Direction.WEST,Block.box(0, 5, 5, 4, 11, 11));
        PIPE_EXTENSION_SHAPE_MAP.put(Direction.UP, Block.box(5, 12, 5, 11, 16, 11));
        PIPE_EXTENSION_SHAPE_MAP.put(Direction.DOWN, Block.box(5, 0, 5, 11, 4, 11));
    }
    public PipeBlock(AbstractBlock.Properties properties, SupportedWoodType woodType) {
        super(properties);
        this.woodType = woodType;
        BlockState defaultState = getStateDefinition().any();
        for(Direction direction: Direction.values()) {
            defaultState = defaultState.setValue(DIRECTION_PROPERTY_MAP.get(direction), false);
            defaultState = defaultState.setValue(DIRECTION_EXTRACTION_PROPERTY_MAP.get(direction), false);
        }
        this.registerDefaultState(defaultState);
    }

    protected VoxelShape getVoxelShape(BlockState state) {
        if(SHAPE_CACHE.containsKey(state)) return SHAPE_CACHE.get(state);
        VoxelShape shape = MIDDLE_SHAPE;

        for(Direction direction: Direction.values()) {
            if(state.getValue(DIRECTION_PROPERTY_MAP.get(direction))) shape = mergeVoxelShapes(shape, PIPE_EXTENSION_SHAPE_MAP.get(direction));
        }
        SHAPE_CACHE.put(state, shape);
        return shape;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if(!(tileEntity instanceof PipeTileEntity)) return ActionResultType.PASS;
        Direction direction = getDirectionOfClick(pos, rayTraceResult);
        if(direction != null) {
            if (player.isCrouching()) {
                return this.shiftRightClick(state, world, pos, player, direction);
            }
            if (player.getItemInHand(hand).getItem() == Blocks.PISTON.asItem()) {
                return this.rightClickPiston(state, world, pos, player, hand, direction);
            }
        }
        return super.use(state, world, pos, player, hand, rayTraceResult);
    }
    private static Direction getDirectionOfClick(BlockPos pos, BlockRayTraceResult ray) {
        Vector3d halfBlock = new Vector3d(0.5,0.5, 0.5);
        Vector3d smallerPosition = ray.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ()).scale(0.99).add(halfBlock.scale(0.01));
        for(Direction direction: Direction.values()) {
            VoxelShape shape = PIPE_EXTENSION_SHAPE_MAP.get(direction);
            if(shape.bounds().contains(smallerPosition)) return direction;
        }
        return null;
    }
    private ActionResultType shiftRightClick(BlockState state, World world, BlockPos pos, PlayerEntity player, Direction direction) {
        if(!state.getValue(DIRECTION_EXTRACTION_PROPERTY_MAP.get(direction))) {
            return ActionResultType.PASS;
        }

        world.setBlock(pos, state.setValue(DIRECTION_EXTRACTION_PROPERTY_MAP.get(direction), false), blockStateFlags);
        if (!player.isCreative() &&!player.addItem(new ItemStack(Blocks.PISTON))) {
            player.drop(new ItemStack(Blocks.PISTON), false);
        }
        player.playSound(SoundEvents.ANVIL_PLACE, 1, 1);
        return ActionResultType.SUCCESS;
    }
    private ActionResultType rightClickPiston(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction) {
        if (state.getValue(DIRECTION_EXTRACTION_PROPERTY_MAP.get(direction))) {
            return ActionResultType.PASS;
        }
        if(!PipeNetworkHelper.canConnectTo(state, world, pos, direction, true)) return ActionResultType.PASS;
        world.setBlock(pos, state.setValue(DIRECTION_EXTRACTION_PROPERTY_MAP.get(direction), true), blockStateFlags);
        if (!player.isCreative()) {
            player.getItemInHand(hand).shrink(1);
        }

        player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1, 1);
        return ActionResultType.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos pos2, boolean flag) {
        if(!world.isClientSide()) {
            BlockState oldState = world.getBlockState(pos);
            BlockState newState =updateState(world.getBlockState(pos), world, pos);
            if(!areStatesEqual(oldState, newState)) {
                world.setBlock(pos, updateState(world.getBlockState(pos), world, pos), blockStateFlags);
            }
            PipeNetworkHelper.generateNetwork(world, pos);
        }
        super.neighborChanged(state, world, pos, block, pos2, flag);
    }

    private boolean areStatesEqual(BlockState state1, BlockState state2) {
        final AtomicBoolean wrong = new AtomicBoolean(false);
        DIRECTION_PROPERTY_MAP.values().forEach(property -> {
            if(!state1.getValue(property).equals(state2.getValue(property))) wrong.set(true);
        });
        if(wrong.get()) return false;
        DIRECTION_EXTRACTION_PROPERTY_MAP.values().forEach(property -> {
            if(!state1.getValue(property).equals(state2.getValue(property))) wrong.set(true);
        });
        return !wrong.get();
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PipeTileEntity();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = defaultBlockState();
        return updateState(state, context.getLevel(), context.getClickedPos());
    }

    public BlockState updateState(BlockState state, World worldIn, BlockPos pos) {
        Map<Direction, Boolean> map = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            map.put(direction, state.getValue(DIRECTION_EXTRACTION_PROPERTY_MAP.get(direction)));
        }
        for (Direction direction: Direction.values()) {
            state = state.setValue(DIRECTION_PROPERTY_MAP.get(direction), PipeNetworkHelper.canConnectTo(state, worldIn, pos, direction, false));
            state = state.setValue(DIRECTION_EXTRACTION_PROPERTY_MAP.get(direction), state.getValue(DIRECTION_EXTRACTION_PROPERTY_MAP.get(direction)) && PipeNetworkHelper.canConnectTo(state, worldIn, pos, direction, true));
        }
        if(worldIn.isClientSide()) return state;
        for(Direction direction: Direction.values()) {
            if(map.get(direction) && !state.getValue(DIRECTION_EXTRACTION_PROPERTY_MAP.get(direction))) {
                worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.PISTON)));
            }
        }
        return state;
    }
    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder lootContextBuilder) {
        List<ItemStack> drops = super.getDrops(state, lootContextBuilder);
        for(Direction direction: Direction.values()) {
            if(state.getValue(DIRECTION_EXTRACTION_PROPERTY_MAP.get(direction))) {
                drops.add(new ItemStack(Blocks.PISTON));
            }
        }
        return drops;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {

        DIRECTION_PROPERTY_MAP.values().forEach(builder::add);
        DIRECTION_EXTRACTION_PROPERTY_MAP.values().forEach(builder::add);
    }

    public SupportedWoodType getWoodType() {
        return woodType;
    }
}
