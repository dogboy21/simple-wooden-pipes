package xyz.dogboy.swp.blocks;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import xyz.dogboy.swp.registry.BlockRegistry;
import xyz.dogboy.swp.tiles.PumpTileEntity;
import xyz.dogboy.swp.utils.SupportedWoodType;

public class PumpBlock extends DynamicShapeBlock {

    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public static final VoxelShape PUMP_SHAPE = Block.box(1, 0, 1, 15, 5, 15);
    public static final VoxelShape PIPE_SHAPE = Block.box(5, 5, 5, 11, 16, 11);

    public PumpBlock(AbstractBlock.Properties properties) {
        super(properties);

        BlockState defaultState = getStateDefinition().any();
        defaultState = defaultState.setValue(CONNECTED, false);
        this.registerDefaultState(defaultState);
    }
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        if(!world.isClientSide()) {
            BlockState oldState = world.getBlockState(pos);
            BlockState newState = updateState(world.getBlockState(pos), world, pos);
            if(!areStatesEqual(oldState, newState)) {
                world.setBlock(pos, updateState(world.getBlockState(pos), world, pos), (Constants.BlockFlags.NOTIFY_NEIGHBORS + Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.UPDATE_NEIGHBORS));
            }
        }
        super.neighborChanged(state, world, pos, p_220069_4_, p_220069_5_, p_220069_6_);
    }
    private static boolean areStatesEqual(BlockState state1, BlockState state2) {
        return state1.getBlock() == state2.getBlock();
    }
    @Override
    protected VoxelShape getVoxelShape(BlockState state) {
        VoxelShape shape = PUMP_SHAPE;
        if(state.getValue(CONNECTED)) shape = mergeVoxelShapes(shape, PIPE_SHAPE);
        return shape;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PumpTileEntity();
    }


    public boolean canConnectTo(IBlockReader world, BlockPos pipePos, Direction direction, boolean excludePipe) {

        BlockState state = world.getBlockState(pipePos.relative(direction));
        if (excludePipe) {
            if(state.getBlock() instanceof PipeBlock || state.getBlock() instanceof PumpBlock) {
                return false;
            }
        } else {
            if(state.getBlock() instanceof PipeBlock || (state.getBlock() instanceof PumpBlock && direction == Direction.DOWN)) {
                return true;
            }
        }
        TileEntity tileEntity = world.getBlockEntity(pipePos.relative(direction));
        return tileEntity != null && tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).isPresent();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = defaultBlockState();
        return updateState(state, context.getLevel(), context.getClickedPos());
    }

    public BlockState updateState(BlockState state, IBlockReader world, BlockPos pos) {

        Block block = world.getBlockState(pos.relative(Direction.UP)).getBlock();
        if(block instanceof PipeBlock) {
            PumpBlock pump = BlockRegistry.getPumpByWoodType(BlockRegistry.getTypeByPipe((PipeBlock)block)).get();
            return pump.defaultBlockState().setValue(CONNECTED, true);
        }
        if(canConnectTo(world, pos, Direction.UP, true)) {
            Block pump = BlockRegistry.WOOD_TYPE_PUMPS.get(SupportedWoodType.OAK).get();
            return pump.defaultBlockState().setValue(CONNECTED, true);
        }

        return BlockRegistry.PUMP.get().defaultBlockState();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CONNECTED);
    }
}
