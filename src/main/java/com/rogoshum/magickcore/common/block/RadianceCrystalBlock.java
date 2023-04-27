package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.tileentity.RadianceCrystalTileEntity;
import com.rogoshum.magickcore.common.tileentity.RadianceCrystalTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RadianceCrystalBlock extends BaseEntityBlock {
    public RadianceCrystalBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new RadianceCrystalTileEntity(p_153215_, p_153216_);
    }

    @Override
    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModTileEntities.RADIANCE_CRYSTAL_TILE_ENTITY.get(), RadianceCrystalTileEntity::tick);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        Vec3 pos = builder.getOptionalParameter(LootContextParams.ORIGIN);
        if(pos == null) return super.getDrops(state, builder);
        BlockEntity tileentity = builder.getLevel().getBlockEntity(new BlockPos(pos));
        if (tileentity instanceof RadianceCrystalTileEntity) {
            RadianceCrystalTileEntity tile = (RadianceCrystalTileEntity)tileentity;
            return tile.getDrops();
        }
        return super.getDrops(state, builder);
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof RadianceCrystalTileEntity) {
            RadianceCrystalTileEntity tile = (RadianceCrystalTileEntity)tileentity;
            tile.dropItem();
            tile.dropThis();
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
        BlockEntity tile = p_60504_.getBlockEntity(p_60505_);
        if(tile instanceof RadianceCrystalTileEntity) {
            if(p_60506_.isCrouching()) {
                ((RadianceCrystalTileEntity) tile).dropItem();
            } else {
                ItemStack stack = p_60506_.getItemInHand(p_60507_);
                if(stack.getItem() instanceof IManaMaterial) {
                    IManaMaterial material = (IManaMaterial) stack.getItem();
                    if(material.typeMaterial()) {
                        ((RadianceCrystalTileEntity) tile).setItemStack(stack);
                    }
                }
            }
        }
        return super.use(p_60503_, p_60504_, p_60505_, p_60506_, p_60507_, p_60508_);
    }
}
