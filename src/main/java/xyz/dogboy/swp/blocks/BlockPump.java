package xyz.dogboy.swp.blocks;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import xyz.dogboy.swp.tiles.TilePump;

public class BlockPump extends BlockWoodenVariation {

    public static final PropertyBool CONNECTED = PropertyBool.create("connected");

    public static final AxisAlignedBB BB = new AxisAlignedBB(0.0625, 0, 0.0625, 0.9375, 0.3125, 0.9375);
    public static final AxisAlignedBB PIPE_BB = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 1, 0.6875);

    public BlockPump() {
        super("pump", Material.IRON, MapColor.IRON);

        this.setDefaultState(this.getBlockState().getBaseState().withProperty(CONNECTED, false));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TilePump();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        if (!isActualState) {
            state = state.getActualState(worldIn, pos);
        }

        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BB);

        if (state.getValue(CONNECTED))
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, PIPE_BB);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        state = this.getActualState(state, source, pos);

        AxisAlignedBB boundingBox = BB;

        if (state.getValue(CONNECTED))
            boundingBox = boundingBox.union(PIPE_BB);

        return boundingBox;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{ CONNECTED }, new IUnlistedProperty[]{ BlockWoodenVariation.TEXTURE });
    }

    public boolean canConnectTo(IBlockAccess world, BlockPos pipePos, EnumFacing direction) {
        TileEntity tileEntity = world.getTileEntity(pipePos.offset(direction));
        return tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(CONNECTED, this.canConnectTo(worldIn, pos, EnumFacing.UP));
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getExtendedState(state, world, pos).withProperty(CONNECTED, this.canConnectTo(world, pos, EnumFacing.UP));
    }

}
