package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.common.tileentity.ElementCrystalTileEntity;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.tileentity.ElementWoolTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

public class ElementCrystalBlock extends CropBlock implements EntityBlock {
    public ElementCrystalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockState getStateForAge(int age) {
        return super.getStateForAge(age);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockGetter world) {
        return new ElementCrystalTileEntity();
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.ELEMENT_CRYSTAL_SEEDS.get();
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return 10;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        ChestBlock
        Vector3d pos = builder.getOptionalParameter(LootContextParams.ORIGIN);
        if(pos == null) return super.getDrops(state, builder);
        TileEntity tileentity = builder.getLevel().getBlockEntity(new BlockPos(pos));
        if (tileentity instanceof ElementCrystalTileEntity) {
            ElementCrystalTileEntity tile = (ElementCrystalTileEntity)tileentity;
            return tile.getDrops();
        }
        return super.getDrops(state, builder);
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof ElementCrystalTileEntity) {
            ElementCrystalTileEntity tile = (ElementCrystalTileEntity)tileentity;
            tile.dropItem();
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(this.isMaxAge(state)) {
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof ElementCrystalTileEntity) {
                ElementCrystalTileEntity tile = (ElementCrystalTileEntity)tileentity;
                tile.dropItem();
            }
            worldIn.setBlockAndUpdate(pos, defaultBlockState());
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }
}
