package xyz.dogboy.swp.tiles;
import java.lang.reflect.Method;

import net.minecraft.fluid.*;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;

import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import xyz.dogboy.swp.SimpleWoodenPipes;
import xyz.dogboy.swp.blocks.PipeBlock;
import xyz.dogboy.swp.blocks.PumpBlock;
import xyz.dogboy.swp.config.SWPConfig;
import xyz.dogboy.swp.registry.TileRegistry;

import javax.annotation.Nonnull;

public class PumpTileEntity extends FluidProvidingTileEntity implements ITickableTileEntity {

    public PumpTileEntity() {
        super(TileRegistry.PUMP.get());
    }
    private static final Method isInfinite = ObfuscationReflectionHelper.findMethod(FlowingFluid.class, "func_205579_d");

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction direction) {

        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && direction == Direction.UP &&
            getBlockState().getValue(PumpBlock.CONNECTED)) {
            return LazyOptional.of(() -> fluidTank).cast();
        }
        return super.getCapability(cap, direction);
    }
    @Override
    public void tick() {
        if (level.isClientSide()) return;
        fillPump();
        pushPump();
    }

    private void pushPump() {
        if (!getBlockState().getValue(PumpBlock.CONNECTED) || level.getBlockState(worldPosition.above()).getBlock() instanceof PipeBlock || fluidTank.getFluid().isEmpty()) return;
        TileEntity tileEntity = level.getBlockEntity(worldPosition.above());
        LazyOptional<IFluidHandler> pushInto = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.DOWN);

        pushInto.ifPresent(iFluidHandler -> {
            FluidStack toRemove = fluidTank.getFluid().copy();
            toRemove.setAmount(Math.min(toRemove.getAmount(), SWPConfig.GENERAL.transferRate.get()));
            int pushed = iFluidHandler.fill(toRemove, IFluidHandler.FluidAction.EXECUTE);
            toRemove.setAmount(pushed);
            fluidTank.drain(toRemove, IFluidHandler.FluidAction.EXECUTE);
        });
    }

    private void fillPump() {
        if (shouldFill() && canFill()) {
            fluidTank.fill(new FluidStack(level.getFluidState(worldPosition.below()).getType(), SWPConfig.GENERAL.pumpRate.get()), IFluidHandler.FluidAction.EXECUTE);
        }
    }
    private boolean shouldFill() {
        if (fluidTank.getSpace() == 0) return false; //Tank is Full
        if (fluidTank.isEmpty()) return true;

        Fluid tankFluid = fluidTank.getFluid().getFluid();
        return tankFluid == level.getFluidState(worldPosition.below()).getType();

    }
    private boolean canFill() {
        BlockPos pos = getBlockPos().below();
        int fluidSources = 0;
        FluidState fluid = level.getFluidState(pos);
        if(!(fluid.getType() instanceof FlowingFluid)) return false;
        try {
            boolean isFluidInfinite = (Boolean) isInfinite.invoke(fluid.getType());

            if(!isFluidInfinite || !fluid.isSource()) return false;

            for(Direction direction: Direction.values()) {
                if (direction.getAxis().isHorizontal() &&
                    fluid.getType() == level.getFluidState(pos.relative(direction)).getType() &&
                    level.getFluidState(pos.relative(direction)).isSource()) {

                    fluidSources++;
                }
            }
            if(fluidSources >= 2) {
                return true;
            }

        }catch (Exception e) {
            SimpleWoodenPipes.LOGGER.error(e);
        }
        return false;
    }
}
