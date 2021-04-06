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
import xyz.dogboy.swp.items.ItemBlockWoodenVariation;
import xyz.dogboy.swp.tiles.TilePipe;
import xyz.dogboy.swp.tiles.TilePump;

import java.util.stream.Stream;

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
        Stream.concat(SimpleWoodenPipes.getAllPlanks().stream(), BlockPipe.stoneVariants.stream())
                .map(baseBlock -> {
                    Ingredient baseBlockIngredient = Ingredient.fromStacks(baseBlock);
                    Ingredient glassIngredient = new OreIngredient("blockGlass");

                    ItemStack output = ((ItemBlockWoodenVariation) Registry.PIPE_ITEM).getWithBaseBlock(baseBlock);
                    output.setCount(6);

                    return Registry.getRecipe(
                            String.format("pipe_%s_%s_%d", baseBlock.getItem().getRegistryName().getResourceDomain(),
                                    baseBlock.getItem().getRegistryName().getResourcePath(), baseBlock.getMetadata()),
                            output,

                            baseBlockIngredient, glassIngredient, baseBlockIngredient,
                            baseBlockIngredient, glassIngredient, baseBlockIngredient,
                            baseBlockIngredient, glassIngredient, baseBlockIngredient
                    );
                })
                .forEach(event.getRegistry()::register);
    }

    private static IRecipe getRecipe(String id, ItemStack output, Ingredient... ingredients) {
        ShapedRecipes recipe = new ShapedRecipes("", 3, 3, NonNullList.from(ingredients[0], ingredients), output);
        return recipe.setRegistryName(new ResourceLocation(Reference.modid, id));
    }

}
