package xyz.dogboy.swp.utils;

import net.minecraft.util.ResourceLocation;

public enum SupportedWoodType {
    OAK("oak", false),
    SPRUCE("spruce", false),
    BIRCH("birch", false),
    ACACIA("acacia", false),
    JUNGLE("jungle", false),
    DARK_OAK("dark_oak", false),
    CRIMSON("crimson", true),
    WARPED("warped", true);
    public final ResourceLocation texturePath;
    public final ResourceLocation plankRegistryName;
    public final String prefix;
    public final boolean fireSafe;
    public final String requiresModId = "";

    SupportedWoodType(String prefix, boolean fireSafe) {
        this(generateByPrefix(prefix), prefix, fireSafe);
    }
    SupportedWoodType(ResourceLocation texturePath, String prefix, boolean fireSafe) {
        this.texturePath = texturePath;
        this.prefix = prefix;
        this.fireSafe = fireSafe;
        plankRegistryName = new ResourceLocation("minecraft", prefix+"_planks");
    }
    private static ResourceLocation generateByPrefix(String prefix) {
        return new ResourceLocation("minecraft", "block/" + prefix + "_planks");
    }
}
