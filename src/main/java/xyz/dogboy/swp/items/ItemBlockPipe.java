package xyz.dogboy.swp.items;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import xyz.dogboy.swp.Registry;
import xyz.dogboy.swp.SimpleWoodenPipes;

public class ItemBlockPipe extends ItemBlock {

    public ItemBlockPipe() {
        super(Registry.PIPE);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (ItemStack plank : SimpleWoodenPipes.getAllPlanks()) {
                items.add(this.getWithBaseBlock(plank));
            }
        }
    }

    public ItemStack getWithBaseBlock(ItemStack baseBlock) {
        ItemStack item = new ItemStack(Registry.PIPE_ITEM);

        if (!item.hasTagCompound()) {
            item.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound baseBlockNbt = new NBTTagCompound();
        baseBlock.writeToNBT(baseBlockNbt);
        item.getTagCompound().setTag("BaseBlock", baseBlockNbt);
        return item;
    }

    public ItemStack getBaseBlock(ItemStack item) {
        if (item.hasTagCompound()) {
            NBTTagCompound baseBlockNbt = item.getTagCompound().getCompoundTag("BaseBlock");
            ItemStack baseBlock = new ItemStack(baseBlockNbt);

            if (!baseBlock.isEmpty() && Block.getBlockFromItem(baseBlock.getItem()) != Blocks.AIR) {
                return baseBlock;
            }
        }

        return new ItemStack(Blocks.PLANKS);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(this.getBaseBlock(stack).getDisplayName());
    }

}
