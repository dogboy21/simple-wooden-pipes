package xyz.dogboy.swp.utils;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import xyz.dogboy.swp.SimpleWoodenPipes;
import xyz.dogboy.swp.blocks.PipeBlock;
import xyz.dogboy.swp.blocks.PumpBlock;
import xyz.dogboy.swp.tiles.PipeTileEntity;

import java.util.ArrayList;
import java.util.List;

public class PipeNetworkHelper {

    private PipeNetworkHelper() {

    }

    public static void generateNetwork(IWorldReader world, BlockPos pos) {
        List<BlockPos> pipes = getConnectedPipes(world, pos);
        List<Both<BlockPos, Direction>> tanks = getConnectedTanks(pipes, world);
        updateReferences(pipes, tanks, world);
    }

    private static List<BlockPos> getConnectedPipes(IWorldReader world, BlockPos pos) {
        List<BlockPos> connectedPipes = new ArrayList<>();
        try {
            addConnectedToPipes(world, pos, connectedPipes);
        } catch (Exception e) {
            SimpleWoodenPipes.LOGGER.error("Cant generate Connected Pipes Network. Network too large. Please don't escalate so much", e);
        }
        return connectedPipes;
    }
    private static List<BlockPos> addConnectedToPipes(IWorldReader world, BlockPos pos, List<BlockPos> connectedPipes) {
        connectedPipes.add(pos);
        for(Direction direction: Direction.values()) {
            if (!connectedPipes.contains(pos.relative(direction)) &&
               canConnectTo(world.getBlockState(pos), world, pos, direction, false) && world.getBlockState(pos.relative(direction)).getBlock() instanceof PipeBlock) {

               addConnectedToPipes(world, pos.relative(direction), connectedPipes);
            }
        }
        return connectedPipes;
    }
    private static List<Both<BlockPos, Direction>> getConnectedTanks(List<BlockPos> pipes, IWorldReader world) {
        List<Both<BlockPos, Direction>> connectedTanks = new ArrayList<>();
        pipes.forEach(pos -> addConnectedTanks(connectedTanks, pos, world));
        return connectedTanks;
    }
    private static List<Both<BlockPos, Direction>> addConnectedTanks(List<Both<BlockPos, Direction>> tanks, BlockPos pos, IWorldReader world) {
        for(Direction direction: Direction.values()) {
            if(tanks.contains(pos.relative(direction))) continue;
            if(canConnectTo(world.getBlockState(pos), world, pos, direction, true) &&
                !world.getBlockState(pos).getValue(PipeBlock.DIRECTION_EXTRACTION_PROPERTY_MAP.get(direction)) &&
                !(world.getBlockState(pos.relative(direction)).getBlock() instanceof PumpBlock)) {
                tanks.add(new Both(pos.relative(direction), direction));
            }
        }
        return tanks;
    }
    private static void updateReferences(List<BlockPos> pipes, List<Both<BlockPos,Direction>> tanks, IWorldReader world) {
        pipes.forEach(pipe -> ((PipeTileEntity)world.getBlockEntity(pipe)).connectedTanks = tanks);
    }

    public static boolean canConnectTo(BlockState state, IBlockReader world, BlockPos pipePos, Direction direction, boolean withoutPipe) {
        BlockState connectTo = world.getBlockState(pipePos.relative(direction));
        if (withoutPipe) {
            if(connectTo.getBlock() instanceof PipeBlock || state.getBlock() instanceof PumpBlock) {
                return false;
            }
        } else {
            if((connectTo.getBlock() instanceof PumpBlock && direction == Direction.DOWN)) {
                return true;
            }
            if(connectTo.getBlock() instanceof PipeBlock) {
                return ((PipeBlock)connectTo.getBlock()).getWoodType() == ((PipeBlock)state.getBlock()).getWoodType();
            }
        }
        TileEntity tileEntity = world.getBlockEntity(pipePos.relative(direction));
        return tileEntity != null && tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).isPresent();
    }


}
