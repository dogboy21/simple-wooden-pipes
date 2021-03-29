package xyz.dogboy.swp.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.dogboy.swp.SimpleWoodenPipes;
import xyz.dogboy.swp.blocks.PipeBlock;
import xyz.dogboy.swp.blocks.PumpBlock;
import xyz.dogboy.swp.blocks.TankBlock;
import xyz.dogboy.swp.utils.SupportedWoodType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;


@Mod.EventBusSubscriber(modid = SimpleWoodenPipes.MOD_ID)
public class BlockRegistry {

    private BlockRegistry() {

    }

    public static final String PIPE_POSTFIX = "_pipe";
    public static final String PUMP_POSTFIX = "_pump";

    public static final Item.Properties DEFAULT_ITEM_PROPERTIES = new Item.Properties().tab(ItemGroup.TAB_REDSTONE);
    public static final AbstractBlock.Properties DEFAULT_BLOCK_PROPERTIES = AbstractBlock.Properties.of(Material.WOOD).strength(2.5f);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SimpleWoodenPipes.MOD_ID);
    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SimpleWoodenPipes.MOD_ID);

    public static final RegistryObject<Block> PUMP = registerBlockWithItem("pump", () -> new PumpBlock(DEFAULT_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> TANK = null; //registerBlockWithItem("tank", () -> new TankBlock(DEFAULT_BLOCK_PROPERTIES));
    public static final BiMap<SupportedWoodType, Supplier<PipeBlock>> WOOD_TYPE_PIPES = HashBiMap.create();
    public static final BiMap<SupportedWoodType, Supplier<PumpBlock>> WOOD_TYPE_PUMPS = HashBiMap.create();

    public static void registerAll(IEventBus bus) {
        registerBlocks();
        BLOCKS.register(bus);
        BLOCK_ITEMS.register(bus);
    }

    private static void registerBlocks() {
        registerPipes();
        registerPumps();
    }
    private static void registerPipes() {
        for(SupportedWoodType supportedWoodType: SupportedWoodType.values()) {
            String registryName = supportedWoodType.prefix + PIPE_POSTFIX;
            RegistryObject<PipeBlock> pipeBlock = registerBlockWithPipeItem(registryName, (() -> new PipeBlock(DEFAULT_BLOCK_PROPERTIES, supportedWoodType)));
            WOOD_TYPE_PIPES.put(supportedWoodType, pipeBlock);
        }
    }
    private static void registerPumps() {
        for(SupportedWoodType supportedWoodType: SupportedWoodType.values()) {
            String registryName = supportedWoodType.prefix + PUMP_POSTFIX;
            RegistryObject<PumpBlock> pumpBlock = registerBlock(registryName, () -> new PumpBlock(AbstractBlock.Properties.copy(PUMP.get()).dropsLike(PUMP.get())));
            WOOD_TYPE_PUMPS.put(supportedWoodType, pumpBlock);
        }
    }
    private static <T extends Block> RegistryObject<T> registerBlockWithItem(String registryName, final Supplier<T> blockSupplier) {
        RegistryObject<T> registryObject = BLOCKS.register(registryName, blockSupplier);
        BLOCK_ITEMS.register(registryName, (Supplier<Item>) () -> new BlockItem(registryObject.get(), DEFAULT_ITEM_PROPERTIES));
        return registryObject;
    }
    private static <T extends Block> RegistryObject<T> registerBlockWithPipeItem(String registryName, final Supplier<T> blockSupplier) {
        RegistryObject<T> registryObject = BLOCKS.register(registryName, blockSupplier);
        BLOCK_ITEMS.register(registryName, (Supplier<Item>) () -> new PipeItem(registryObject.get(), DEFAULT_ITEM_PROPERTIES));
        return registryObject;
    }
    private static <T extends Block> RegistryObject<T> registerBlock(String registryName, final Supplier<T> blockSupplier) {
        return BLOCKS.register(registryName, blockSupplier);
    }

    public static Supplier<PumpBlock> getPumpByWoodType(SupportedWoodType type) {
        return getByWoodType(type, WOOD_TYPE_PUMPS);
    }
    private static <T extends Block> Supplier<T> getByWoodType(SupportedWoodType type, BiMap<SupportedWoodType, Supplier<T>> map) {
        final ValueHolder<Supplier<T>> valueHolder = new ValueHolder();
        map.forEach((supportedWoodType, blockSupplier) -> {
            if(supportedWoodType == type) valueHolder.setValue(blockSupplier);
        });
        return valueHolder.getValue();
    }
    public static SupportedWoodType getTypeByPipe(PipeBlock pipe) {
        return getTypeByBlock(pipe, WOOD_TYPE_PIPES);
    }
    private static <T extends Block> SupportedWoodType getTypeByBlock(T block, BiMap<SupportedWoodType, Supplier<T>> map) {
        final ValueHolder<SupportedWoodType> valueHolder = new ValueHolder();
        map.forEach((supportedWoodType, blockSupplier) -> {
            if(blockSupplier.get() == block) valueHolder.setValue(supportedWoodType);
        });
        return valueHolder.getValue();
    }

    private static class PipeItem extends BlockItem {
        public PipeItem(Block p_i48527_1_, Properties p_i48527_2_) {
            super(p_i48527_1_, p_i48527_2_);
        }

        @Override
        protected boolean allowdedIn(ItemGroup p_194125_1_) {
            boolean isAllowed = super.allowdedIn(p_194125_1_);
            String modId = getTypeByPipe((PipeBlock)getBlock()).requiresModId;
            if(modId.isEmpty()) {
                return isAllowed;
            }
            return ModList.get().isLoaded(modId) && isAllowed;
        }

        @Override
        public void appendHoverText(ItemStack itemStack, @Nullable World World, List<ITextComponent> lines, ITooltipFlag flag) {
            super.appendHoverText(itemStack, World, lines, flag);
            SupportedWoodType type = getTypeByPipe((PipeBlock)getBlock());
            if(!type.fireSafe) {
                lines.add(new TranslationTextComponent("simplewoodenpipes.tooltip.pipe.low_temp_only"));
            }else{
                lines.add(new TranslationTextComponent("simplewoodenpipes.tooltip.pipe.also_high_temp"));
            }
            lines.add(new TranslationTextComponent("simplewoodenpipes.tooltip.pipe.add_piston"));

        }
    }

    private static class ValueHolder<Type> {
        private Type value;

        public void setValue(Type value) {
            this.value = value;
        }

        public Type getValue() {
            return value;
        }
    }
}
