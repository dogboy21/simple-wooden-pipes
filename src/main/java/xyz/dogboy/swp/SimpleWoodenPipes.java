package xyz.dogboy.swp;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = Reference.modid, name = Reference.name, version = Reference.version, certificateFingerprint = Reference.fingerprint)
public class SimpleWoodenPipes {

    @Mod.Instance(Reference.modid)
    private static SimpleWoodenPipes instance;

    @SidedProxy(serverSide = Reference.proxy, clientSide = Reference.clientProxy)
    private static CommonProxy proxy;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(SimpleWoodenPipes.getProxy());
        SimpleWoodenPipes.proxy.onPreInit();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        SimpleWoodenPipes.proxy.onInit();
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        SimpleWoodenPipes.proxy.onPostInit();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Reference.modid)) {
            ConfigManager.sync(Reference.modid, Config.Type.INSTANCE);
        }
    }

    public static SimpleWoodenPipes getInstance() {
        return SimpleWoodenPipes.instance;
    }

    public static CommonProxy getProxy() {
        return SimpleWoodenPipes.proxy;
    }

    public static List<ItemStack> getAllPlanks() {
        NonNullList<ItemStack> planks = NonNullList.create();

        for (ItemStack plank : OreDictionary.getOres("plankWood")) {
            if (plank.getMetadata() == OreDictionary.WILDCARD_VALUE) {
                plank.getItem().getSubItems(CreativeTabs.SEARCH, planks);
            } else {
                planks.add(plank);
            }
        }

        return planks;
    }

}
