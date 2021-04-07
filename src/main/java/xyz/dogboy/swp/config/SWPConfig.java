package xyz.dogboy.swp.config;

import net.minecraftforge.common.ForgeConfigSpec;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import xyz.dogboy.swp.SimpleWoodenPipes;

import static net.minecraftforge.fml.loading.LogMarkers.FORGEMOD;
import static net.minecraftforge.fml.loading.LogMarkers.CORE;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SWPConfig {

    private SWPConfig() {

    }

    public static final ForgeConfigSpec spec;
    public static final General GENERAL;

    static {
        final Pair<General, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(General::new);
        spec = specPair.getRight();
        GENERAL = specPair.getLeft();
    }

    public static class General {
        public final ForgeConfigSpec.ConfigValue<Integer> transferRate;
        public final ForgeConfigSpec.ConfigValue<Integer> pumpRate;
        public final ForgeConfigSpec.ConfigValue<Integer> internalVolume;

        private General(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            transferRate = builder
                    .comment("The transfer rate of pipes")
                    .translation("simplewoodenpipes.config.transferRate")
                    .defineInRange("transferRate", 50, 1, Integer.MAX_VALUE);

            pumpRate = builder
                    .comment("The pump rate of pumps")
                    .translation("simplewoodenpipes.config.pumpRate")
                    .defineInRange("pumpRate", 25, 1, Integer.MAX_VALUE);

            internalVolume = builder
                    .comment("The internal volume of pipes")
                    .translation("simplewoodenpipes.config.internalVolume")
                    .defineInRange("internalVolume", 1000, 1, Integer.MAX_VALUE);

            builder.pop();
        }
    }


    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        SimpleWoodenPipes.LOGGER.debug(FORGEMOD, "Loaded SimpleWoodenPipes config file {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.Reloading configEvent) {
        SimpleWoodenPipes.LOGGER.debug(CORE, "SimpleWoodenPipes config just got changed on the file system!");
    }
}