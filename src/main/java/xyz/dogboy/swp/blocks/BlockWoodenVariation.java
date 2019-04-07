package xyz.dogboy.swp.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import xyz.dogboy.swp.blocks.property.PropertyString;
import xyz.dogboy.swp.tiles.WoodenVariationProvider;

public class BlockWoodenVariation extends BlockBase {

    public static final PropertyString TEXTURE = new PropertyString("texture");

    public BlockWoodenVariation(String name, Material blockMaterialIn, MapColor blockMapColorIn) {
        super(name, blockMaterialIn, blockMapColorIn);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        if (!stack.hasTagCompound()) {
            return;
        }

        NBTTagCompound baseBlock = stack.getTagCompound().getCompoundTag("BaseBlock");
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity != null) {
            tileEntity.getTileData().setTag("BaseBlock", baseBlock);
        }
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof WoodenVariationProvider) {
            return ((WoodenVariationProvider) tileEntity).writeExtendedState(extendedBlockState);
        }

        return state;
    }

}
