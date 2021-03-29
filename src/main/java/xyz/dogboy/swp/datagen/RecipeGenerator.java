package xyz.dogboy.swp.datagen;

import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.data.ForgeRecipeProvider;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.dogboy.swp.registry.BlockRegistry;
import xyz.dogboy.swp.utils.SupportedWoodType;

import java.util.function.Consumer;

public class RecipeGenerator extends ForgeRecipeProvider implements IConditionBuilder {
    public RecipeGenerator(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        for(SupportedWoodType type: SupportedWoodType.values()) {
            buildPipeRecipe(consumer, type);
        }
    }
    private void buildPipeRecipe(Consumer<IFinishedRecipe> consumer, SupportedWoodType type) {
        Item plankWood = ForgeRegistries.ITEMS.getValue(type.plankRegistryName);
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(BlockRegistry.WOOD_TYPE_PIPES.get(type).get(),6).define('W', plankWood).define('X', Tags.Items.GLASS).pattern("WWW").pattern("WXW").pattern("WWW").unlockedBy("has_planks", has(ItemTags.PLANKS));

        if(type.requiresModId.equals("")) {
            builder.save(consumer, BlockRegistry.WOOD_TYPE_PIPES.get(type).get().getRegistryName());
            return;
        }
        ConditionalRecipe.Builder conditionalBuilder = ConditionalRecipe.builder();
        conditionalBuilder.addCondition(modLoaded(type.requiresModId)).addRecipe(builder::save).build(consumer, BlockRegistry.WOOD_TYPE_PIPES.get(type).get().getRegistryName());
    }
}
