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

    @Config.Comment("Item used to upgrade a pipe to extract mode. Format: <Item Name> [<Metadata> [NBT Data]]")
    public static String pipeExtractionItem = "minecraft:piston";

    @Config.Comment("Should Pipes connect to variations of the same block variant (allow all wood variants to connect to each other for example)")
    public static boolean variantInterconnection = true;

}
