package com.rogoshum.magickcore.common.item.tool;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.integration.AdditionLoader;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.resource.ResourcePackLoader;

public class WandItem extends BaseItem {
    public static final String SET_KEY = "blockSet";
    public WandItem() {
        super(properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        CompoundTag tag = context.getItemInHand().getOrCreateTagElement(SET_KEY);
        int count = 1;
        Player player = context.getPlayer();
        EntityStateData state = null;
        if(player != null)
            state = ExtraDataUtil.entityStateData(player);
        if(state != null)
            count = (int) (state.getMaxManaValue() / 39);
        HashSet<Vec3> vector3ds = NBTTagHelper.getVectorSet(tag);
        NBTTagHelper.addOrDeleteVector(tag, Vec3.atCenterOf(context.getClickedPos()), vector3ds.size() >= count);
        BlockState state1 = context.getLevel().getBlockState(context.getClickedPos());
        context.getLevel().playSound(null, context.getClickedPos(), state1.getSoundType().getHitSound(), SoundSource.NEUTRAL, 1.0f, 1.0f+ MagickCore.rand.nextFloat());
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if(playerIn.isShiftKeyDown()) {
            playerIn.getItemInHand(handIn).getOrCreateTag().remove(SET_KEY);
            worldIn.playSound(null, new BlockPos(playerIn.position()), SoundEvents.ITEM_FRAME_BREAK, SoundSource.NEUTRAL, 1.0f, 1.0f);
            return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
