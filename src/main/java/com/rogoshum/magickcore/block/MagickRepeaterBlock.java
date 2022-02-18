package com.rogoshum.magickcore.block;

import com.google.common.collect.Lists;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.tileentity.MagickCraftingTileEntity;
import com.rogoshum.magickcore.block.tileentity.MagickRepeaterTileEntity;
import com.rogoshum.magickcore.block.tileentity.MagickSupplierTileEntity;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.item.LifeRepeaterItem;
import com.rogoshum.magickcore.magick.lifestate.repeater.LifeRepeater;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MagickRepeaterBlock extends BaseBlock{
    public MagickRepeaterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MagickRepeaterTileEntity();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(worldIn.isRemote) return ActionResultType.SUCCESS;
        MagickRepeaterTileEntity repeaterTileEntity = (MagickRepeaterTileEntity) worldIn.getTileEntity(pos);

        if (player.getHeldItemMainhand().isEmpty()) {
            if(player.isSneaking())
                repeaterTileEntity.changeDirection(player);
            else
                repeaterTileEntity.changeTouchMode();
            return ActionResultType.SUCCESS;
        }

        if (handIn == Hand.MAIN_HAND) {
            player.getHeldItemMainhand();
            if(player.getHeldItemMainhand().getItem() instanceof LifeRepeaterItem)
            {
                LifeRepeater repeater = ((LifeRepeaterItem) player.getHeldItemMainhand().getItem()).getRepeater();
                repeaterTileEntity.setLifeRepeater(repeater);
                player.getHeldItemMainhand().shrink(1);
            }
            else if(player.getHeldItemMainhand().getItem() == Items.REDSTONE){
                repeaterTileEntity.setPortTurningState(hit.getFace(), !repeaterTileEntity.isPortTurnOn(hit.getFace()));
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> list = Lists.newArrayList();
        list.add(new ItemStack(ModItems.magick_repeater.get()));
        return list;
    }
}
