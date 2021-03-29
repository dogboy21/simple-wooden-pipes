package xyz.dogboy.swp.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import xyz.dogboy.swp.SimpleWoodenPipes;
import xyz.dogboy.swp.utils.SupportedWoodType;


public class ItemModelGenerator extends ItemModelProvider {
    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, SimpleWoodenPipes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (SupportedWoodType type : SupportedWoodType.values()) {
            ItemModelBuilder builder = getBuilder(type.prefix + "_pipe");
            builder.parent(new ModelFile.ExistingModelFile(new ResourceLocation(SimpleWoodenPipes.MOD_ID, "item/pipe"), existingFileHelper));
            builder.texture("wood", type.texturePath);
        }
    }
}
