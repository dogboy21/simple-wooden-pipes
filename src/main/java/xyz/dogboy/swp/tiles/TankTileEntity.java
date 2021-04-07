package xyz.dogboy.swp.tiles;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import xyz.dogboy.swp.registry.TileRegistry;

import javax.annotation.Nonnull;

public class TankTileEntity extends FluidProvidingTileEntity{
    public TankTileEntity() {
        super(TileRegistry.TANK.get());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction direction) {
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() ->fluidTank).cast();
        }
        return super.getCapability(cap);
    }
}
