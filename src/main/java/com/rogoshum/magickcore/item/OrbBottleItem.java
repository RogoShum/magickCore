package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.capability.IElementAnimalState;
import com.rogoshum.magickcore.client.item.OrbBottleRenderer;
import com.rogoshum.magickcore.entity.ManaElementOrbEntity;
import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.event.ElementOrbEvent;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibItem;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class OrbBottleItem extends BaseItem{

    public OrbBottleItem() {
        super(BaseItem.properties().maxStackSize(1).setISTER(() -> OrbBottleRenderer::new));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        CompoundNBT tag = NBTTagHelper.getStackTag(stack);
        if(tag.contains("ELEMENT")){
            IManaElement element = ModElements.getElement(tag.getString("ELEMENT"));
            ReleaseAttribute attribute = new ReleaseAttribute(null, null, entityLiving, 60, 2);
            MagickReleaseHelper.applyElementFunction(element, EnumManaType.DEBUFF, attribute);
            return ItemStack.EMPTY;
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 16;
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if(!playerIn.world.isRemote) {
            CompoundNBT tag = NBTTagHelper.getStackTag(stack);
            if (tag.contains("ELEMENT") && (ElementOrbEvent.containAnimalType(target.getType()) || target instanceof AnimalEntity)) {
                IElementAnimalState state = target.getCapability(MagickCore.elementAnimal).orElse(null);
                state.setElement(ModElements.getElement(tag.getString("ELEMENT")));
                tag.remove("ELEMENT");
                playerIn.setHeldItem(hand,stack);
            }
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

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return new ItemStack(ModItems.orb_bottle.get());
    }
}
