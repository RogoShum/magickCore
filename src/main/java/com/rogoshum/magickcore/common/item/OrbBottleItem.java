package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.item.OrbBottleRenderer;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;
import com.rogoshum.magickcore.common.event.RegisterEvent;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.ExtraDataUtil;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.init.ModGroup;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.lib.LibItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class OrbBottleItem extends BaseItem{

    public OrbBottleItem() {
        super(properties().maxStackSize(64).setISTER(() -> OrbBottleRenderer::new));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        CompoundNBT tag = NBTTagHelper.getStackTag(stack);
        if(tag.contains("ELEMENT")){
            MagickElement element = MagickRegistry.getElement(tag.getString("ELEMENT"));
            MagickContext attribute = new MagickContext(worldIn).tick(60).<MagickContext>force(2).victim(entityLiving).element(element).applyType(ApplyType.DE_BUFF);
            MagickReleaseHelper.releaseMagick(attribute.noCost());
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
            if (tag.contains("ELEMENT") && (RegisterEvent.containAnimalType(target.getType()) || target instanceof AnimalEntity)) {
                EntityStateData state = ExtraDataUtil.entityStateData(target);
                state.setElement(MagickRegistry.getElement(tag.getString("ELEMENT")));
                tag.remove("ELEMENT");
                playerIn.setHeldItem(hand,stack);
            }
        }
        return super.itemInteractionForEntity(stack, playerIn, target, hand);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, playerIn.getBoundingBox().grow(2));
        for (Entity entity : list) {
            boolean flag = false;
            if(entity instanceof ManaElementOrbEntity) {
                ManaElementOrbEntity orb = (ManaElementOrbEntity) entity;
                ItemStack stack = playerIn.getHeldItem(handIn);
                CompoundNBT tag = NBTTagHelper.getStackTag(stack);
                tag.putString("ELEMENT", orb.spellContext().element.type());
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
        return new ItemStack(ModItems.ORB_BOTTLE.get());
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if(group == ModGroup.ELEMENT_ITEM_GROUP) {
            MagickRegistry.getRegistry(LibRegistry.ELEMENT).registry().forEach( (key, value) ->
                    items.add(NBTTagHelper.setElement(new ItemStack(this), key))
            );
        }
        if(group == ModGroup.ITEM_GROUP) {
            items.add(new ItemStack(this));
        }
    }
}
