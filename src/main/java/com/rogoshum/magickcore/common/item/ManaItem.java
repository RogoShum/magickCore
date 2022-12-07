package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.api.itemstack.IManaData;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ManaItem extends BaseItem implements IManaData {
    public ManaItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(itemstack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        AtomicInteger i = new AtomicInteger(MathHelper.hsvToRGB(0.0f, 0.0F, 1.0F));
        ExtraDataUtil.itemManaData(stack, data -> {
            com.rogoshum.magickcore.common.magick.Color color = data.spellContext().element.getRenderer().getColor();
            if(color.equals(com.rogoshum.magickcore.common.magick.Color.ORIGIN_COLOR) && RenderHelper.getPlayer() != null) {
                color = ExtraDataUtil.entityStateData(RenderHelper.getPlayer()).getElement().color();
            }
            float[] hsv = Color.RGBtoHSB((int)(color.r() * 255), (int)(color.g() * 255), (int)(color.b() * 255), null);

            i.set(MathHelper.hsvToRGB(hsv[0], hsv[1], hsv[2]));
        });

        return i.get();
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        return 1f - data.manaCapacity().getMana() / data.manaCapacity().getMaxMana();
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return super.getRarity(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        ExtraDataUtil.itemManaData(stack, data -> {
            String information = data.spellContext().toString();
            if(!information.isEmpty()) {
                String[] tips = information.split("\n");
                for (String tip : tips) {
                    tooltip.add(new StringTextComponent(tip));
                }
            }
        });
    }

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        return super.getShareTag(stack);
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
        super.readShareTag(stack, nbt);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 10;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        EntityStateData state = ExtraDataUtil.entityStateData(entityLiving);
        if(state != null) {
            boolean success = releaseMagick(entityLiving, state, stack);
            if(success)
                spawnParticle(entityLiving, state);
        }

        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    public void spawnParticle(LivingEntity playerIn, EntityStateData state) {
        ParticleUtil.spawnBlastParticle(playerIn.world, playerIn.getPositionVec().add(0, playerIn.getHeight() * 0.5, 0), 3, state.getElement(), ParticleType.PARTICLE);
    }

    public abstract boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack);
}
