package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.itemstack.IManaData;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.TooltipFlag;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ManaItem extends BaseItem implements IManaData {
    public ManaItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        playerIn.startUsingItem(handIn);
        EntityStateData state = ExtraDataUtil.entityStateData(playerIn);
        boolean success;
        if(state != null) {
            success = releaseMagick(playerIn, state, itemstack, handIn);
            if(success) {
                spawnParticle(playerIn, state);
            }
        }
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        ItemManaData data = ExtraDataUtil.itemManaData(stack, 0);
        Color color = data.spellContext().element().getRenderer().getPrimaryColor();
        if(color.equals(Color.ORIGIN_COLOR) && RenderHelper.getPlayer() != null) {
            color = ExtraDataUtil.entityStateData(RenderHelper.getPlayer()).getElement().primaryColor();
        }
        return color.getDecimalColor();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        ItemManaData data = ExtraDataUtil.itemManaData(stack, 0);
        return (int) (13 * (data.manaCapacity().getMana() / data.manaCapacity().getMaxMana()));
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return super.getRarity(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        MagickElement element = ModElements.ORIGIN;
        if(Minecraft.getInstance().player != null)
            element = ExtraDataUtil.entityStateData(Minecraft.getInstance().player).getElement();
        MagickElement finalElement = element;
        ExtraDataUtil.itemManaData(stack, data -> {
            String information = "";
            KeyMapping key = Minecraft.getInstance().options.keyShift;
            boolean isKeyDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key.getKey().getValue());
            if(isKeyDown)
                information = data.spellContext().toString(finalElement);
            else
                information = data.spellContext().toStringSample(finalElement);
            if(!information.isEmpty()) {
                String[] tips = information.split("\n");
                for (String tip : tips) {
                    tooltip.add(new TextComponent(tip));
                }
            }
        });
    }

    @Nullable
    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        return super.getShareTag(stack);
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        super.readShareTag(stack, nbt);
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

    public abstract boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack, InteractionHand handIn);
}
