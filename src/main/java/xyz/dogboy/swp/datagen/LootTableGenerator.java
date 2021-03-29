package xyz.dogboy.swp.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ForgeLootTableProvider;
import net.minecraftforge.fml.RegistryObject;
import xyz.dogboy.swp.registry.BlockRegistry;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootTableGenerator extends ForgeLootTableProvider {
    public LootTableGenerator(DataGenerator generatorIn) {
        super(generatorIn);

    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return ImmutableList.of(Pair.of(BlockLootTableGenerator::new, LootParameterSets.BLOCK));
    }

    public class BlockLootTableGenerator extends BlockLootTables {

        @Override
        protected void addTables() {
            BlockRegistry.WOOD_TYPE_PIPES.forEach((woodType, blockPipeSupplier) -> dropSelf(blockPipeSupplier.get()));
            dropSelf(BlockRegistry.PUMP.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return BlockRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
        }
    }
}
