package com.rogoshum.magickcore.common.item.tool;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.HashSet;

public class WandItem extends BaseItem {
    public static final String SET_KEY = "blockSet";
    public WandItem() {
        super(properties());
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        CompoundNBT tag = context.getItemInHand().getOrCreateTagElement(SET_KEY);
        int count = 1;
        PlayerEntity player = context.getPlayer();
        EntityStateData state = null;
        if(player != null)
            state = ExtraDataUtil.entityStateData(player);
        if(state != null)
            count = (int) (state.getMaxManaValue() / 39);
        HashSet<Vector3d> vector3ds = NBTTagHelper.getVectorSet(tag);
        NBTTagHelper.addOrDeleteVector(tag, Vector3d.atCenterOf(context.getClickedPos()), vector3ds.size() >= count);
        BlockState state1 = context.getLevel().getBlockState(context.getClickedPos());
        context.getLevel().playSound(null, context.getClickedPos(), state1.getSoundType().getHitSound(), SoundCategory.NEUTRAL, 1.0f, 1.0f+ MagickCore.rand.nextFloat());
        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(playerIn.isShiftKeyDown()) {
            playerIn.getItemInHand(handIn).getOrCreateTag().remove(SET_KEY);
            worldIn.playSound(null, new BlockPos(playerIn.position()), SoundEvents.ITEM_FRAME_BREAK, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            return ActionResult.success(playerIn.getItemInHand(handIn));
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
