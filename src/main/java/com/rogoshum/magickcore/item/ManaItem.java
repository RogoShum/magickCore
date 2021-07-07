package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.EnumManaType;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.capability.IManaItemData;
import com.rogoshum.magickcore.helper.RoguelikeHelper;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibItem;
import com.rogoshum.magickcore.network.ManaItemDataPack;
import com.rogoshum.magickcore.network.Networking;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public abstract class ManaItem extends BaseItem implements IManaItem {
    public ManaItem() {
        super(BaseItem.properties.maxStackSize(1));
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 0;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.setActiveHand(handIn);
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        if(stack.getItem() instanceof IManaItem) {
            IManaItemData data = ((IManaItem) stack.getItem()).getItemData(stack);
            float[] color = data.getElement().getRenderer().getColor();
            float[] hsv = Color.RGBtoHSB((int)(color[0] * 255), (int)(color[1] * 255), (int)(color[2] * 255), null);

            return MathHelper.hsvToRGB(hsv[0], hsv[1], hsv[2]);
        }
        return MathHelper.hsvToRGB(0.0f, 0.0F, 1.0F);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if(stack.getItem() instanceof IManaItem) {
            IManaItemData data = ((IManaItem) stack.getItem()).getItemData(stack);
            return 1f - data.getMana() / data.getMaxMana();
        }
        return 0;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(stack.getItem() instanceof IManaItem) {
            IManaItemData data = ((IManaItem) stack.getItem()).getItemData(stack);
            if(data != null) {
                tooltip.add((new TranslationTextComponent(LibItem.ELEMENT)).appendString(" ").append((new TranslationTextComponent(MagickCore.MOD_ID + ".description." + data.getElement().getType()))));
                tooltip.add((new TranslationTextComponent(LibItem.FORCE)).appendString(" ").append((new StringTextComponent(Float.toString(data.getForce())))));
                tooltip.add((new TranslationTextComponent(LibItem.TICK)).appendString(" ").append((new StringTextComponent(Float.toString((float) data.getTickTime() / 20f) + "s"))));
                tooltip.add((new TranslationTextComponent(LibItem.MANA_TYPE)).appendString(" ").append((new StringTextComponent(data.getManaType().getLabel()))));

                if (data.getTrace()) {
                    tooltip.add((new TranslationTextComponent("")));
                    tooltip.add((new TranslationTextComponent(LibItem.TRACE)));
                }

                if (RoguelikeHelper.isRogueItem(stack))
                    tooltip.add((new TranslationTextComponent(LibItem.ROGUE_TICK)).appendString(" ").append((new StringTextComponent(Integer.toString(RoguelikeHelper.getItemRemainTime(stack))))));
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        updateData(stack, entityIn, itemSlot);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        updateData(stack, entity, 0);
        return false;
    }

    private void updateData(ItemStack stack, Entity entityIn, int slot)
    {
        if(!entityIn.world.isRemote())
        {
            IManaItem data = (IManaItem)stack.getItem();
            Networking.INSTANCE.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entityIn),
                    new ManaItemDataPack(entityIn.getEntityId(), slot, data.getElement(stack).getType(), data.getTrace(stack)
                            , data.getManaType(stack).getLabel(), data.getRange(stack), data.getForce(stack)
                            , data.getMana(stack), data.getTickTime(stack)));
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 3;
    }

    @Override
    public float getMaxMana(ItemStack stack) {
        if(stack.getItem() instanceof IManaItem)
            return ((IManaItem)stack.getItem()).getItemData(stack).getMaxMana();
        return 0;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        IEntityState state = entityLiving.getCapability(MagickCore.entityState).orElse(null);
        releaseMagick(entityLiving, state, stack);
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    public abstract boolean releaseMagick(LivingEntity playerIn, IEntityState state, ItemStack stack);

    @Override
    public boolean getTrace(ItemStack stack) {
        if(stack.getItem() instanceof IManaItem)
            return ((IManaItem)stack.getItem()).getItemData(stack).getTrace();
        return false;
    }

    @Override
    public void setTrace(ItemStack stack, boolean trace) {
        if(stack.getItem() instanceof IManaItem)
            ((IManaItem)stack.getItem()).getItemData(stack).setTrace(trace);
    }

    @Nullable
    @Override
    public IManaItemData getItemData(ItemStack stack) {
        return stack.getCapability(MagickCore.manaItemData).orElse(null);
    }

    @Override
    public float getMana(ItemStack stack) {
        if(stack.getItem() instanceof IManaItem)
            return ((IManaItem)stack.getItem()).getItemData(stack).getMana();
        return 0;
    }

    @Override
    public void setMana(ItemStack stack, float mana) {
        if(stack.getItem() instanceof IManaItem)
            ((IManaItem)stack.getItem()).getItemData(stack).setMana(mana);
    }

    @Override
    public float receiveMana(ItemStack stack, float mana) {
        if(stack.getItem() instanceof IManaItem)
            return ((IManaItem)stack.getItem()).getItemData(stack).receiveMana(mana);

        return mana;
    }

    @Override
    public IManaElement getElement(ItemStack stack) {
        if(stack.getItem() instanceof IManaItem)
            return ((IManaItem)stack.getItem()).getItemData(stack).getElement();

        return ModElements.getElement(LibElements.ORIGIN);
    }

    @Override
    public void setElement(ItemStack stack, IManaElement manaElement) {
        if(stack.getItem() instanceof IManaItem)
            ((IManaItem)stack.getItem()).getItemData(stack).setElement(manaElement);
    }

    @Override
    public float getRange(ItemStack stack) {
        if(stack.getItem() instanceof IManaItem)
            return ((IManaItem)stack.getItem()).getItemData(stack).getRange();

        return 0;
    }

    @Override
    public void setRange(ItemStack stack, float range) {
        if(stack.getItem() instanceof IManaItem)
            ((IManaItem)stack.getItem()).getItemData(stack).setRange(range);
    }

    @Override
    public float getForce(ItemStack stack) {
        if(stack.getItem() instanceof IManaItem)
            return ((IManaItem)stack.getItem()).getItemData(stack).getForce();

        return 0;
    }

    @Override
    public void setForce(ItemStack stack, float force) {
        if(stack.getItem() instanceof IManaItem)
            ((IManaItem)stack.getItem()).getItemData(stack).setForce(force);
    }

    @Override
    public EnumManaType getManaType(ItemStack stack) {
        if(stack.getItem() instanceof IManaItem)
            return ((IManaItem)stack.getItem()).getItemData(stack).getManaType();

        return null;
    }

    @Override
    public void setManaType(ItemStack stack, EnumManaType manaType) {
        if(stack.getItem() instanceof IManaItem)
            ((IManaItem)stack.getItem()).getItemData(stack).setManaType(manaType);
    }

    @Override
    public int getTickTime(ItemStack stack) {
        if(stack.getItem() instanceof IManaItem)
            return ((IManaItem)stack.getItem()).getItemData(stack).getTickTime();

        return 0;
    }

    @Override
    public void setTickTime(ItemStack stack, int tick) {
        if(stack.getItem() instanceof IManaItem)
            ((IManaItem)stack.getItem()).getItemData(stack).setTickTime(tick);
    }
}
