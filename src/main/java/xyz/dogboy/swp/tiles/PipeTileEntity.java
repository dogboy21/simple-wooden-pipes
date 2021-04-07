package xyz.dogboy.swp.tiles;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import xyz.dogboy.swp.blocks.PipeBlock;
import xyz.dogboy.swp.config.SWPConfig;
import xyz.dogboy.swp.registry.BlockRegistry;
import xyz.dogboy.swp.registry.TileRegistry;
import xyz.dogboy.swp.utils.Both;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PipeTileEntity extends FluidProvidingTileEntity implements ITickableTileEntity {

    public PipeTileEntity() {
        super(TileRegistry.PIPE.get());
    }

    public List<Both<BlockPos, Direction>> connectedTanks = new ArrayList<>();
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction direction) {
        if(direction == null) return super.getCapability(cap, null);
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY &&
            getBlockState().getValue(PipeBlock.DIRECTION_PROPERTY_MAP.get(direction))) {
            return LazyOptional.of(() -> fluidTank).cast();
        }
        return super.getCapability(cap, direction);
    }
    @Override
    public void tick() {
        if(level.isClientSide()) return;
        int temperature = fluidTank.getFluid().getFluid().getAttributes().getTemperature();
        boolean isTemperatureImmune = BlockRegistry.getTypeByPipe((PipeBlock) getBlockState().getBlock()).fireSafe;
        if (!isTemperatureImmune && temperature >= 550) {
            level.setBlock(worldPosition, Blocks.FIRE.defaultBlockState(), Constants.BlockFlags.NOTIFY_NEIGHBORS + Constants.BlockFlags.BLOCK_UPDATE);
            return;
        }

        for (Direction direction : Direction.values()) {
            if(!isConnectedInDirection(direction)) {
                continue;
            }
            TileEntity tileEntity = level.getBlockEntity(worldPosition.relative(direction));
            if (tileEntity == null) {
                continue;
            }

            LazyOptional<IFluidHandler> fluidHandler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
            if (!fluidHandler.isPresent()) {
                continue;
            }

            if (isExtractionInDirection(direction) || tileEntity instanceof PumpTileEntity) {
                pullFromTank(fluidHandler);
            }
        }
        if (fluidTank.isEmpty()) {
            return;
        }
        pushInto();
    }
    private void pullFromTank(LazyOptional<IFluidHandler> pullFrom) {
        int freeSpace = fluidTank.getSpace();
        if (freeSpace <= 0) {
            return;
        }

        pullFrom.ifPresent(iFluidHandler -> {
                FluidStack drained = iFluidHandler.drain(Math.min(freeSpace,SWPConfig.GENERAL.transferRate.get()), IFluidHandler.FluidAction.EXECUTE);
                fluidTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
        });
    }

    private void pushInto() {
        AtomicInteger removedFluid = new AtomicInteger();
        for(Both<BlockPos, Direction> tankConnection: connectedTanks) {
            TileEntity tank = level.getBlockEntity(tankConnection.left());
            if(tank == null || fluidTank.isEmpty()) continue;
            LazyOptional<IFluidHandler> pushInto = tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, tankConnection.right().getOpposite());

            pushInto.ifPresent(iFluidHandler -> {
                FluidStack toRemove = fluidTank.getFluid().copy();
                toRemove.setAmount(Math.min(toRemove.getAmount(), SWPConfig.GENERAL.transferRate.get() - removedFluid.get()));
                int pushed = iFluidHandler.fill(toRemove, IFluidHandler.FluidAction.EXECUTE);
                toRemove.setAmount(pushed);
                fluidTank.drain(toRemove, IFluidHandler.FluidAction.EXECUTE);
                removedFluid.addAndGet(pushed);
            });
        }

    }
    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);

        AtomicInteger iterator = new AtomicInteger();
        CompoundNBT tankData = new CompoundNBT();
        tankData.putInt("count", connectedTanks.size());
        connectedTanks.forEach(blockPosDirectionBoth -> {
            CompoundNBT connectionNBT = NBTUtil.writeBlockPos(blockPosDirectionBoth.left());
            connectionNBT.putInt("direction", blockPosDirectionBoth.right().ordinal());
            tankData.put(iterator.get() + "", connectionNBT);
        });

        nbt.put("connectionData",tankData);
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);

        CompoundNBT tankData = nbt.getCompound("connectionData");
        for(int i = 0; i < tankData.getInt("count"); i++) {
            CompoundNBT connectionNBT = tankData.getCompound(i + "");
            BlockPos pos = NBTUtil.readBlockPos(connectionNBT);
            Direction direction = Direction.values()[connectionNBT.getInt("direction")];
            connectedTanks.add(new Both<>(pos, direction));
        }
    }

    private boolean isExtractionInDirection(Direction direction) {
        return isConnectedInDirection(direction) && getBlockState().getValue(PipeBlock.DIRECTION_EXTRACTION_PROPERTY_MAP.get(direction));
    }
    private boolean isConnectedInDirection(Direction direction) {
        return getBlockState().getValue(PipeBlock.DIRECTION_PROPERTY_MAP.get(direction));
    }
}