package xyz.dogboy.swp.blocks;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import xyz.dogboy.swp.Registry;
import xyz.dogboy.swp.tiles.TilePipe;

public class BlockPipe extends BlockWoodenVariation {


    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");

    public static final AxisAlignedBB MIDDLE_BB = new AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);
    public static final AxisAlignedBB NORTH_BB = new AxisAlignedBB(0.3125, 0.3125, 0, 0.6875, 0.6875, 0.25);
    public static final AxisAlignedBB EAST_BB = new AxisAlignedBB(1, 0.3125, 0.3125, 0.75, 0.6875, 0.6875);
    public static final AxisAlignedBB SOUTH_BB = new AxisAlignedBB(0.3125, 0.3125, 1, 0.6875, 0.6875, 0.75);
    public static final AxisAlignedBB WEST_BB = new AxisAlignedBB(0, 0.3125, 0.3125, 0.25, 0.6875, 0.6875);
    public static final AxisAlignedBB UP_BB = new AxisAlignedBB(0.3125, 1, 0.3125, 0.6875, 0.75, 0.6875);
    public static final AxisAlignedBB DOWN_BB = new AxisAlignedBB(0.3125, 0, 0.3125, 0.6875, 0.25, 0.6875);

    public BlockPipe() {
        super("pipe", Material.WOOD, MapColor.WOOD);
        this.setHardness(1.0F);
        this.setResistance(2.0F);
        this.setSoundType(SoundType.WOOD);

        this.setDefaultState(this.getBlockState().getBaseState()
                .withProperty(NORTH, false)
                .withProperty(EAST, false)
                .withProperty(SOUTH, false)
                .withProperty(WEST, false)
                .withProperty(UP, false)
                .withProperty(DOWN, false));
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        if (!isActualState) {
            state = state.getActualState(worldIn, pos);
        }

        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, MIDDLE_BB);

        if (state.getValue(NORTH))
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_BB);

        if (state.getValue(EAST))
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_BB);

        if (state.getValue(SOUTH))
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_BB);

        if (state.getValue(WEST))
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_BB);

        if (state.getValue(UP))
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, UP_BB);

        if (state.getValue(DOWN))
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, DOWN_BB);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        state = this.getActualState(state, source, pos);

        AxisAlignedBB boundingBox = MIDDLE_BB;

        if (state.getValue(NORTH))
            boundingBox = boundingBox.union(NORTH_BB);

        if (state.getValue(EAST))
            boundingBox = boundingBox.union(EAST_BB);

        if (state.getValue(SOUTH))
            boundingBox = boundingBox.union(SOUTH_BB);

        if (state.getValue(WEST))
            boundingBox = boundingBox.union(WEST_BB);

        if (state.getValue(UP))
            boundingBox = boundingBox.union(UP_BB);

        if (state.getValue(DOWN))
            boundingBox = boundingBox.union(DOWN_BB);

        return boundingBox;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IFluidHandlerItem fluidHandler = playerIn.getHeldItem(hand).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (fluidHandler == null) {
            return false;
        }

        TilePipe pipe = (TilePipe) worldIn.getTileEntity(pos);
        if (pipe == null) {
            return false;
        }

        IFluidTankProperties tankProperties = pipe.getTankProperties()[0];
        int maxDrain = tankProperties.getCapacity() - (tankProperties.getContents() == null ? 0 : tankProperties.getContents().amount);
        if (maxDrain <= 0) {
            return false;
        }

        FluidStack drained = fluidHandler.drain(maxDrain, false);
        if (drained == null) {
            return false;
        }

        maxDrain = pipe.fill(drained, false);
        if (maxDrain <= 0) {
            return false;
        }

        drained = fluidHandler.drain(maxDrain, true);
        if (drained == null) {
            return false;
        }

        pipe.fill(drained, true);
        playerIn.setHeldItem(hand, fluidHandler.getContainer());

        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        if (!stack.hasTagCompound()) {
            return;
        }

        NBTTagCompound baseBlock = stack.getTagCompound().getCompoundTag("BaseBlock");
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TilePipe) {
            tileEntity.getTileData().setTag("BaseBlock", baseBlock);
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TilePipe();
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
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{ NORTH, EAST, SOUTH, WEST, UP, DOWN },
                new IUnlistedProperty[]{ BlockWoodenVariation.TEXTURE });
    }

    public boolean canConnectTo(IBlockAccess world, BlockPos pipePos, EnumFacing direction) {
        TileEntity tileEntity = world.getTileEntity(pipePos.offset(direction));
        return tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
    }

    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(NORTH, this.canConnectTo(worldIn, pos, EnumFacing.NORTH))
                .withProperty(EAST, this.canConnectTo(worldIn, pos, EnumFacing.EAST))
                .withProperty(SOUTH, this.canConnectTo(worldIn, pos, EnumFacing.SOUTH))
                .withProperty(WEST, this.canConnectTo(worldIn, pos, EnumFacing.WEST))
                .withProperty(UP, this.canConnectTo(worldIn, pos, EnumFacing.UP))
                .withProperty(DOWN, this.canConnectTo(worldIn, pos, EnumFacing.DOWN));
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getExtendedState(state, world, pos)
                .withProperty(NORTH, this.canConnectTo(world, pos, EnumFacing.NORTH))
                .withProperty(EAST, this.canConnectTo(world, pos, EnumFacing.EAST))
                .withProperty(SOUTH, this.canConnectTo(world, pos, EnumFacing.SOUTH))
                .withProperty(WEST, this.canConnectTo(world, pos, EnumFacing.WEST))
                .withProperty(UP, this.canConnectTo(world, pos, EnumFacing.UP))
                .withProperty(DOWN, this.canConnectTo(world, pos, EnumFacing.DOWN));
    }

    public ItemStack getItem(IBlockAccess world, BlockPos pos) {
        ItemStack itemStack = new ItemStack(Registry.PIPE_ITEM);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TilePipe) {
            itemStack.setTagCompound(new NBTTagCompound());
            itemStack.getTagCompound().setTag("BaseBlock", tileEntity.getTileData().getCompoundTag("BaseBlock").copy());
        }
        return itemStack;
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return this.getItem(worldIn, pos);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) {
            return true;
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool) {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(this.getItem(world, pos));
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.CENTER;
    }

}
