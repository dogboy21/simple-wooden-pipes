package xyz.dogboy.swp.config;

import net.minecraftforge.common.config.Config;

import xyz.dogboy.swp.Reference;

@Config(modid = Reference.modid, type = Config.Type.INSTANCE)
public class SWPConfig {

    @Config.RangeInt(min = 1)
    public static int internalVolume = 1000;

    @Config.RangeInt(min = 1)
    public static int pumpRate = 25;

    @Config.RangeInt(min = 1)
    public static int transferRate = 50;

}
