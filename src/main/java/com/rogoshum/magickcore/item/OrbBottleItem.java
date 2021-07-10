package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.CommonProxy;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.capability.IElementAnimalState;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.capability.IManaItemData;
import com.rogoshum.magickcore.entity.ManaElementOrbEntity;
import com.rogoshum.magickcore.event.ElementOrbEvent;
import com.rogoshum.magickcore.helper.NBTTagHelper;
import com.rogoshum.magickcore.helper.RoguelikeHelper;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibItem;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class OrbBottleItem extends BaseItem{

    public OrbBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        CompoundNBT tag = NBTTagHelper.getStackTag(stack);
        if(tag.contains("ELEMENT") && ElementOrbEvent.containAnimalType(target.getType()))
        {
            IElementAnimalState state = target.getCapability(MagickCore.elementAnimal).orElse(null);
            state.setElement(ModElements.getElement(tag.getString("ELEMENT")));
            tag.remove("ELEMENT");
            stack.setTag(tag);
        }
        return super.itemInteractionForEntity(stack, playerIn, target, hand);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, playerIn.getBoundingBox().grow(2));
        for (Entity entity : list)
        {
            boolean flag = false;
            if(entity instanceof ManaElementOrbEntity)
            {
                ManaElementOrbEntity orb = (ManaElementOrbEntity) entity;
                ItemStack stack = playerIn.getHeldItem(handIn);
                CompoundNBT tag = NBTTagHelper.getStackTag(stack);
                tag.putString("ELEMENT", orb.getElement().getType());
                entity.remove();
                flag = true;
            }
            if(flag)
                return ActionResult.resultConsume(playerIn.getHeldItem(handIn));
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT tag = NBTTagHelper.getStackTag(stack);
        if(tag.contains("ELEMENT")) {
            tooltip.add((new TranslationTextComponent(LibItem.ELEMENT)).appendString(" ").append((new TranslationTextComponent(MagickCore.MOD_ID + ".description." + tag.getString("ELEMENT")))));
        }
    }
}
