package xyz.dogboy.swp;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import xyz.dogboy.swp.config.SWPConfig;
import xyz.dogboy.swp.registry.BlockRegistry;
import xyz.dogboy.swp.registry.TileRegistry;

@Mod(SimpleWoodenPipes.MOD_ID)
public class SimpleWoodenPipes {
    public static final String MOD_ID = "simplewoodenpipes";
    public static final Logger LOGGER = LogManager.getLogger();
    public SimpleWoodenPipes() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SWPConfig.spec);
        BlockRegistry.registerAll(FMLJavaModLoadingContext.get().getModEventBus());
        TileRegistry.registerAll(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
