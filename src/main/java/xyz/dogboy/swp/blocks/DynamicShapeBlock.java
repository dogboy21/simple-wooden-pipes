package xyz.dogboy.swp.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.util.Constants;

public abstract class DynamicShapeBlock extends Block {
    protected static final int blockStateFlags = (Constants.BlockFlags.NOTIFY_NEIGHBORS + Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.UPDATE_NEIGHBORS);

    public DynamicShapeBlock(Properties properties) {
        super(properties);
    }
    abstract protected VoxelShape getVoxelShape(BlockState state);

    protected static VoxelShape mergeVoxelShapes(VoxelShape shape1, VoxelShape shape2) {
        return VoxelShapes.or(shape1, shape2);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader blockReader, BlockPos pos, ISelectionContext context) {
        return getVoxelShape(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader blockReader, BlockPos pos, ISelectionContext context) {
        return getVoxelShape(state);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, IBlockReader blockReader, BlockPos pos) {
        return getVoxelShape(state);
    }
}
