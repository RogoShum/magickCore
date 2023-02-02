package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.api.itemstack.IColorDurabilityBar;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.api.itemstack.IManaData;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ManaItem extends BaseItem implements IManaData, IColorDurabilityBar {
    public ManaItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        playerIn.startUsingItem(handIn);
        EntityStateData state = ExtraDataUtil.entityStateData(playerIn);
        boolean success = false;
        if(state != null) {
            success = releaseMagick(playerIn, state, itemstack);
            if(success) {
                spawnParticle(playerIn, state);
            }
        }
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        Color color = data.spellContext().element.getRenderer().getColor();
        if(color.equals(Color.ORIGIN_COLOR) && RenderHelper.getPlayer() != null) {
            color = ExtraDataUtil.entityStateData(RenderHelper.getPlayer()).getElement().color();
        }
        return color.getDecimalColor();
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        return 1f - data.manaCapacity().getMana() / data.manaCapacity().getMaxMana();
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return super.getUseDuration(itemStack);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return super.getRarity(stack);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        ExtraDataUtil.itemManaData(stack, data -> {
            String information = "";
            KeyMapping key = Minecraft.getInstance().options.keyShift;
            boolean isKeyDown = key.isDown();
            if(isKeyDown)
                information = data.spellContext().toString();
            else
                information = data.spellContext().toStringSample();
            if(!information.isEmpty()) {
                String[] tips = information.split("\n");
                for (String tip : tips) {
                    tooltip.add(new TextComponent(tip));
                }
            }
        });
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }

    public void spawnParticle(LivingEntity playerIn, EntityStateData state) {
        if(state != null)
            ParticleUtil.spawnBlastParticle(playerIn.level, playerIn.position().add(0, playerIn.getBbHeight() * 0.5, 0), 3, state.getElement(), ParticleType.PARTICLE);
    }

    public abstract boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack);
}
