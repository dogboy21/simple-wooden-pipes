package xyz.dogboy.swp.blocks;


import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xyz.dogboy.swp.tiles.TankTileEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public class TankBlock extends Block {
    public TankBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if(!world.isClientSide() && tileEntity != null && tileEntity instanceof TankTileEntity) {
            TankTileEntity tank = (TankTileEntity) tileEntity;
            player.sendMessage(new TranslationTextComponent(tank.fluidTank.getFluid().getTranslationKey()), UUID.randomUUID());
            player.sendMessage(new StringTextComponent(tank.fluidTank.getFluidAmount() + "mb"), UUID.randomUUID());
            return ActionResultType.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, ray);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TankTileEntity();
    }
}
