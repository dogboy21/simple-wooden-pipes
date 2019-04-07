package xyz.dogboy.swp;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreIngredient;

import xyz.dogboy.swp.blocks.BlockPipe;
import xyz.dogboy.swp.blocks.BlockPump;
import xyz.dogboy.swp.items.ItemBlockPipe;
import xyz.dogboy.swp.tiles.TilePipe;
import xyz.dogboy.swp.tiles.TilePump;

@GameRegistry.ObjectHolder(Reference.modid)
@Mod.EventBusSubscriber(modid = Reference.modid)
public class Registry {

    public static final Block PIPE = Blocks.AIR;
    public static Item PIPE_ITEM = Items.AIR;

    public static final Block PUMP = Blocks.AIR;
    public static Item PUMP_ITEM = Items.AIR;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new BlockPipe(),
                new BlockPump()
        );

        GameRegistry.registerTileEntity(TilePipe.class, new ResourceLocation(Reference.modid, "pipe"));
        GameRegistry.registerTileEntity(TilePump.class, new ResourceLocation(Reference.modid, "pump"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                Registry.PIPE_ITEM = new ItemBlockPipe().setRegistryName(Registry.PIPE.getRegistryName()),
                Registry.PUMP_ITEM = new ItemBlock(Registry.PUMP).setRegistryName(Registry.PUMP.getRegistryName())
        );
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        for (ItemStack plank : SimpleWoodenPipes.getAllPlanks()) {
            Ingredient plankIngredient = Ingredient.fromStacks(plank);
            Ingredient glassIngredient = new OreIngredient("blockGlass");

            NonNullList<Ingredient> ingredients = NonNullList.from(plankIngredient,
                    plankIngredient, glassIngredient, plankIngredient,
                    plankIngredient, glassIngredient, plankIngredient,
                    plankIngredient, glassIngredient, plankIngredient
            );

            ItemStack output = ((ItemBlockPipe) Registry.PIPE_ITEM).getWithBaseBlock(plank);
            output.setCount(6);

            ShapedRecipes recipe = new ShapedRecipes("", 3, 3, ingredients, output);
            event.getRegistry().register(recipe.setRegistryName(new ResourceLocation(Reference.modid, "pipe_" + plank.getItem().getRegistryName().getResourceDomain() + "_" + plank.getItem().getRegistryName().getResourcePath() + "_" + plank.getMetadata())));
        }
    }

}
