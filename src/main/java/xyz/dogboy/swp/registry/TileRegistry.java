package xyz.dogboy.swp.registry;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.dogboy.swp.SimpleWoodenPipes;
import xyz.dogboy.swp.tiles.PipeTileEntity;
import xyz.dogboy.swp.tiles.PumpTileEntity;
import xyz.dogboy.swp.tiles.TankTileEntity;


public class TileRegistry {
    private TileRegistry() {

    }
    public static final DeferredRegister<TileEntityType<?>> TILE_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, SimpleWoodenPipes.MOD_ID);

    public static final RegistryObject<TileEntityType<PipeTileEntity>> PIPE = TILE_TYPES.register("pipe", () -> TileEntityType.Builder.of(PipeTileEntity::new, getPipeBlocks()).build(null));


    public static final RegistryObject<TileEntityType<PumpTileEntity>> PUMP = TILE_TYPES.register("pump", () -> TileEntityType.Builder.of(PumpTileEntity::new, getPumpBlocks()).build(null));
    public static final RegistryObject<TileEntityType<TankTileEntity>> TANK = null; //TILE_TYPES.register("tank", () -> TileEntityType.Builder.of(TankTileEntity::new, BlockRegistry.TANK.get()).build(null));

    private static Block[] getPumpBlocks() {
        final Block[] pumpBlocks = new Block[BlockRegistry.WOOD_TYPE_PUMPS.size()+1];
        BlockRegistry.WOOD_TYPE_PUMPS.forEach((supportedWoodType, blockSupplier) -> pumpBlocks[supportedWoodType.ordinal()] = blockSupplier.get());
        pumpBlocks[pumpBlocks.length - 1] = BlockRegistry.PUMP.get();
        return pumpBlocks;
    }
    private static Block[] getPipeBlocks() {
        final Block[] pipeBlocks = new Block[BlockRegistry.WOOD_TYPE_PIPES.size()];
        BlockRegistry.WOOD_TYPE_PIPES.forEach((supportedWoodType, blockSupplier) -> pipeBlocks[supportedWoodType.ordinal()] = blockSupplier.get());
        return pipeBlocks;
    }

    public static void registerAll(IEventBus modEventBus) {
        TILE_TYPES.register(modEventBus);
    }
}
