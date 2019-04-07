package xyz.dogboy.swp.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import xyz.dogboy.swp.CommonProxy;
import xyz.dogboy.swp.Reference;
import xyz.dogboy.swp.Registry;
import xyz.dogboy.swp.client.model.BakedWoodenVariationModel;

public class ClientProxy extends CommonProxy {

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Registry.PIPE_ITEM, 0,
                new ModelResourceLocation(new ResourceLocation(Reference.modid, "pipe"), "normal"));
        ModelLoader.setCustomModelResourceLocation(Registry.PUMP_ITEM, 0,
                new ModelResourceLocation(new ResourceLocation(Reference.modid, "pump"), "inventory"));
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        for (ModelResourceLocation model : event.getModelRegistry().getKeys()) {
            if (!model.getResourceDomain().equals(Reference.modid)) {
                continue;
            }

            if (model.getResourcePath().equals("pipe")) {
                this.replaceWoodenVariationModel(model, Arrays.asList("main", "particle"), event);
            } else if (model.getResourcePath().equals("pump")) {
                this.replaceWoodenVariationModel(model, Collections.singletonList("pipe"), event);
            }
        }
    }

    private void replaceWoodenVariationModel(ModelResourceLocation modelLocation, List<String> replaceTextures, ModelBakeEvent event) {
        try {
            IModel model = ModelLoaderRegistry.getModel(modelLocation);
            IBakedModel baseModel = event.getModelRegistry().getObject(modelLocation);
            IBakedModel modifiedModel = new BakedWoodenVariationModel(baseModel, model, replaceTextures);
            event.getModelRegistry().putObject(modelLocation, modifiedModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTextureFromBlock(Block block, int meta) {
        IBlockState state = block.getStateFromMeta(meta);
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state).getIconName();
    }

}
