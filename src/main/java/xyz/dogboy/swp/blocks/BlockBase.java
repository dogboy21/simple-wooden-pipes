package xyz.dogboy.swp.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;

import xyz.dogboy.swp.Reference;

public class BlockBase extends Block {

    public BlockBase(String name, Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
        this.setUnlocalizedName(Reference.modid + "." + name);
        this.setRegistryName(new ResourceLocation(Reference.modid, name));
        this.setCreativeTab(CreativeTabs.REDSTONE);
    }

}
