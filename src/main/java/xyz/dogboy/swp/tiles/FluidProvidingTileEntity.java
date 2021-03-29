package xyz.dogboy.swp.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import xyz.dogboy.swp.config.SWPConfig;

public abstract class FluidProvidingTileEntity extends TileEntity {

    public FluidTank fluidTank = new FluidTank(SWPConfig.GENERAL.internalVolume.get());
    protected FluidProvidingTileEntity(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        return fluidTank.writeToNBT(nbt);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        fluidTank.readFromNBT(nbt);
    }
}