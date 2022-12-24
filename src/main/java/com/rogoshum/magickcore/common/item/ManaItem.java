package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.api.itemstack.IManaData;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
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
        EntityStateData state = ExtraDataUtil.entityStateData(playerIn);
        boolean success = false;
        if(state != null) {
            success = releaseMagick(playerIn, state, itemstack);
            if(success) {
                spawnParticle(playerIn, state);
            }
        }
        return ActionResult.resultConsume(itemstack);
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
    public Rarity getRarity(ItemStack stack) {
        return super.getRarity(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        ExtraDataUtil.itemManaData(stack, data -> {
            String information = "";
            KeyBinding key = Minecraft.getInstance().gameSettings.keyBindSneak;
            boolean isKeyDown = InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), key.getKey().getKeyCode());
            if(isKeyDown)
                information = data.spellContext().toString();
            else
                information = data.spellContext().toStringSample();
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
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    public void spawnParticle(LivingEntity playerIn, EntityStateData state) {
        if(state != null)
            ParticleUtil.spawnBlastParticle(playerIn.world, playerIn.getPositionVec().add(0, playerIn.getHeight() * 0.5, 0), 3, state.getElement(), ParticleType.PARTICLE);
    }

    public abstract boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack);
}
