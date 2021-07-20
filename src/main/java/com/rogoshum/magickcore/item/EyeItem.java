package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.*;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.capability.IManaItemData;
import com.rogoshum.magickcore.entity.ManaEyeEntity;
import com.rogoshum.magickcore.entity.ManaRuneEntity;
import com.rogoshum.magickcore.helper.MagickReleaseHelper;
import com.rogoshum.magickcore.helper.RoguelikeHelper;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.init.ModEntites;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class EyeItem extends ManaItem{
    public EyeItem() {
        super(BaseItem.properties.maxStackSize(1));
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, IEntityState state, ItemStack stack) {
        IManaItem item = (IManaItem) stack.getItem();
        if(!playerIn.world.isRemote) {
            List<Entity> list = playerIn.world.getEntitiesWithinAABB(ManaRuneEntity.class, playerIn.getBoundingBox().grow(16));
            if(list.size() >= 1) {
                UUID trace = item.getTrace(stack) ? MagickCore.emptyUUID : MagickCore.emptyUUID_EYE;
                IManaElement element = item.getMana(stack) > 0 ? item.getElement(stack) : state.getElement();
                MagickReleaseHelper.releasePointEntity(ModEntites.mana_eye, playerIn, list.get(0).getPositionVec().add(0, 0, 0), element, trace, item.getForce(stack), item.getTickTime(stack)
                        , item.getRange(stack), EnumTargetType.NONE, item.getManaType(stack));
                list.get(0).remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(stack.getItem() instanceof IManaItem) {
            IManaItemData data = ((IManaItem) stack.getItem()).getItemData(stack);
            if(data != null) {
                tooltip.add(new TranslationTextComponent(LibItem.EYE_D));
                tooltip.add(new StringTextComponent(""));
                tooltip.add((new TranslationTextComponent(LibItem.ELEMENT)).appendString(" ").append((new TranslationTextComponent(MagickCore.MOD_ID + ".description." + data.getElement().getType()))));
                tooltip.add((new TranslationTextComponent(LibItem.FORCE)).appendString(" ").append((new StringTextComponent(Float.toString(data.getForce())))));
                tooltip.add((new TranslationTextComponent(LibItem.TICK)).appendString(" ").append((new StringTextComponent(Float.toString((float) data.getTickTime() / 4f) + "s"))));
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
}
