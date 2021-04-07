package xyz.dogboy.swp.datagen;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import xyz.dogboy.swp.blocks.PipeBlock;
import xyz.dogboy.swp.registry.BlockRegistry;
import xyz.dogboy.swp.SimpleWoodenPipes;
import xyz.dogboy.swp.utils.SupportedWoodType;

import java.util.concurrent.atomic.AtomicInteger;

public class BlockStateGenerator extends BlockStateProvider {

    ExistingFileHelper helper;
    public BlockStateGenerator(DataGenerator p_i232520_1_, ExistingFileHelper fileHelper) {
        super(p_i232520_1_, SimpleWoodenPipes.MOD_ID, fileHelper);
        helper = fileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        registerPipeMultiparts();
        registerPumpMultiparts();
    }
    private void registerPipeMultiparts() {

        BlockModelBuilder[][] pipeParts = createPipeParts();
        for(SupportedWoodType type: SupportedWoodType.values()) {
            registerPipeMultipart(type, pipeParts[0][type.ordinal()], pipeParts[1][type.ordinal()], pipeParts[2][0]);
        }
    }
    private void registerPipeMultipart(SupportedWoodType type, BlockModelBuilder centerPart, BlockModelBuilder side, BlockModelBuilder extraction) {

        Block pipeBlock = BlockRegistry.WOOD_TYPE_PIPES.get(type).get();

        MultiPartBlockStateBuilder builder = getMultipartBuilder(pipeBlock);
        builder.part().modelFile(centerPart).uvLock(false).addModel();

        builder.part().modelFile(side).uvLock(false).addModel().condition(PipeBlock.DIRECTION_PROPERTY_MAP.get(Direction.NORTH), true);
        builder.part().modelFile(extraction).uvLock(false).addModel().condition(PipeBlock.DIRECTION_EXTRACTION_PROPERTY_MAP.get(Direction.NORTH), true);

        builder.part().modelFile(side).uvLock(false).rotationY(90).addModel().condition(PipeBlock.DIRECTION_PROPERTY_MAP.get(Direction.EAST), true);
        builder.part().modelFile(extraction).uvLock(false).rotationY(90).addModel().condition(PipeBlock.DIRECTION_EXTRACTION_PROPERTY_MAP.get(Direction.EAST), true);

        builder.part().modelFile(side).uvLock(false).rotationY(180).addModel().condition(PipeBlock.DIRECTION_PROPERTY_MAP.get(Direction.SOUTH), true);
        builder.part().modelFile(extraction).uvLock(false).rotationY(180).addModel().condition(PipeBlock.DIRECTION_EXTRACTION_PROPERTY_MAP.get(Direction.SOUTH), true);

        builder.part().modelFile(side).uvLock(false).rotationY(270).addModel().condition(PipeBlock.DIRECTION_PROPERTY_MAP.get(Direction.WEST), true);
        builder.part().modelFile(extraction).uvLock(false).rotationY(270).addModel().condition(PipeBlock.DIRECTION_EXTRACTION_PROPERTY_MAP.get(Direction.WEST), true);

        builder.part().modelFile(side).uvLock(false).rotationX(-90).addModel().condition(PipeBlock.DIRECTION_PROPERTY_MAP.get(Direction.UP), true);
        builder.part().modelFile(extraction).uvLock(false).rotationX(-90).addModel().condition(PipeBlock.DIRECTION_EXTRACTION_PROPERTY_MAP.get(Direction.UP), true);

        builder.part().modelFile(side).uvLock(false).rotationX(90).addModel().condition(PipeBlock.DIRECTION_PROPERTY_MAP.get(Direction.DOWN), true);
        builder.part().modelFile(extraction).uvLock(false).rotationX(90).addModel().condition(PipeBlock.DIRECTION_EXTRACTION_PROPERTY_MAP.get(Direction.DOWN), true);
    }
    private BlockModelBuilder[][] createPipeParts() {
        BlockModelBuilder[] cores = new BlockModelBuilder[SupportedWoodType.values().length];
        for(SupportedWoodType type: SupportedWoodType.values()) {
            BlockModelBuilder builder = models().getBuilder(models().BLOCK_FOLDER + "/" + type.prefix + "_pipe_middle");
            addCube(builder, 6,6,6, 10,10,10,  "#wood");
            addCube(builder, 4,10,6,6,12,10,"#wood");
            addCube(builder, 4,4,6,6,6,10,"#wood");
            addCube(builder, 10,4,6,12,6,10,"#wood");
            addCube(builder, 10,10,6,12,12,10,"#wood");
            addCube(builder, 6,10,4,10,12,6,"#wood");
            addCube(builder, 6,4,10,10,6,12,"#wood");
            addCube(builder, 6,10,10,10,12,12,"#wood");
            addCube(builder, 6,4,4,10,6,6,"#wood");
            addCube(builder, 10,4,4,12,12,6,"#wood");
            addCube(builder, 10,4,10,12,12,12,"#wood");
            addCube(builder, 4,4,10,6,12,12,"#wood");
            addCube(builder, 4,4,4,6,12,6,"#wood");

            builder.texture("wood", type.texturePath).texture("particle", type.texturePath);
            cores[type.ordinal()] = builder;
        }
        BlockModelBuilder[] sides = new BlockModelBuilder[SupportedWoodType.values().length];
        for(SupportedWoodType type: SupportedWoodType.values()) {
            BlockModelBuilder builder = models().getBuilder(models().BLOCK_FOLDER + "/" + type.prefix + "_pipe_side");
            addCube(builder, 5,5,0, 11,6,4,  "#wood");
            addCube(builder, 5,10,0, 11,11,4,  "#wood");
            addCube(builder, 10,6,0, 11,10,4,  "#wood");
            addCube(builder, 5,6,0, 6,10,4,  "#wood");

            builder.texture("wood", type.texturePath).texture("particle", type.texturePath);
            sides[type.ordinal()] = builder;
        }
        BlockModelBuilder[] extraction = new BlockModelBuilder[1];

        BlockModelBuilder builder = models().getBuilder(models().BLOCK_FOLDER + "/pipe_extraction");
        addCube(builder, 4,4,0, 12,5,2,  "#iron");
        addCube(builder, 4,11,0, 12,12,2,  "#iron");
        addCube(builder, 4,5,0, 5,11,2,  "#iron");
        addCube(builder, 11,5,0, 12,11,2,  "#iron");

        builder.texture("iron", new ResourceLocation("minecraft", "block/iron_block"));
        extraction[0] = builder;
        return new BlockModelBuilder[][]{cores, sides, extraction};
    }

    private void registerPumpMultiparts() {
        ModelFile pump = new ModelFile.ExistingModelFile(new ResourceLocation(SimpleWoodenPipes.MOD_ID, "block/pump_unconnected"), helper);
        ModelFile[] pipes = createPipesForPump();
        for(SupportedWoodType type: SupportedWoodType.values()) {
            registerPumpMultipart(type, pump, pipes[type.ordinal()]);
        }
    }

    private ModelFile[] createPipesForPump() {
        ModelFile[] pipes = new ModelFile[SupportedWoodType.values().length];
        for(SupportedWoodType type: SupportedWoodType.values()) {
            BlockModelBuilder builder = models().getBuilder(models().BLOCK_FOLDER + "/" + type.prefix + "_pump");
            addCube(builder, 10,5,5,11,16,11, "#wood");
            addCube(builder, 5,5,5,6,16,11, "#wood");
            addCube(builder, 6,5,5,10,16,6, "#wood");
            addCube(builder, 6,5,10,10,16,11, "#wood");
            builder.texture("wood", type.texturePath).texture("particle", type.texturePath);
            pipes[type.ordinal()] = builder;
        }
        return pipes;
    }

    private void registerPumpMultipart(SupportedWoodType type, ModelFile pump, ModelFile pipe) {
        Block pumpBlock = BlockRegistry.WOOD_TYPE_PUMPS.get(type).get();

        MultiPartBlockStateBuilder builder = getMultipartBuilder(pumpBlock);
        builder.part().modelFile(pump).addModel();
        builder.part().modelFile(pipe).addModel();
    }
    private BlockModelBuilder addCube(BlockModelBuilder builder, int fromX, int fromY, int fromZ, int toX, int toY, int toZ, String texture) {
        final AtomicInteger integer = new AtomicInteger();
        return builder.element().from(fromX, fromY, fromZ).to(toX, toY, toZ).allFaces((direction, faceBuilder) -> {
            faceBuilder.texture(texture);
            switch (integer.getAndIncrement()) {
                case 2:
                case 3:
                    faceBuilder.uvs(fromX, fromY, toX, toY); //North+South
                    break;
                case 4:
                case 5:
                    faceBuilder.uvs(fromZ, fromY, toZ, toY); //East+West
                    break;
                case 0:
                case 1:
                    faceBuilder.uvs(fromX, fromZ, toX, toZ); //Up+Down
            }

        }).end();
    }
}
