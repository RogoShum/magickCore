package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.item.OrbBottleRenderer;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.event.RegisterEvent;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.init.ModGroups;
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
import net.minecraft.entity.player.ServerPlayerEntity;
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
        super(properties().stacksTo(64).setISTER(() -> OrbBottleRenderer::new));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        CompoundNBT tag = NBTTagHelper.getStackTag(stack);
        if(tag.contains("ELEMENT")){
            MagickElement element = MagickRegistry.getElement(tag.getString("ELEMENT"));
            MagickContext attribute = new MagickContext(worldIn).tick(60).<MagickContext>force(2).victim(entityLiving).element(element).applyType(ApplyType.DE_BUFF);
            MagickReleaseHelper.releaseMagick(attribute.noCost());
            return ItemStack.EMPTY;
        }
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 16;
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if(!playerIn.level.isClientSide) {
            CompoundNBT tag = NBTTagHelper.getStackTag(stack);
            if (tag.contains("ELEMENT") && (RegisterEvent.containAnimalType(target.getType()) || target instanceof AnimalEntity)) {
                EntityStateData state = ExtraDataUtil.entityStateData(target);
                state.setElement(MagickRegistry.getElement(tag.getString("ELEMENT")));
                ItemStack copy = stack.copy();
                copy.setCount(1);
                NBTTagHelper.getStackTag(copy).remove("ELEMENT");
                if(!playerIn.addItem(copy))
                    playerIn.drop(copy, false, true);
                stack.shrink(1);
                playerIn.setItemInHand(hand, stack);
            }
        }
        return super.interactLivingEntity(stack, playerIn, target, hand);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        List<Entity> list = worldIn.getEntities(playerIn, playerIn.getBoundingBox().inflate(2));
        for (Entity entity : list) {
            boolean flag = false;
            if(entity instanceof ManaElementOrbEntity) {
                ManaElementOrbEntity orb = (ManaElementOrbEntity) entity;
                ItemStack stack = playerIn.getItemInHand(handIn);
                ItemStack copy = stack.copy();
                copy.setCount(1);
                CompoundNBT tag = NBTTagHelper.getStackTag(copy);
                tag.putString("ELEMENT", orb.spellContext().element.type());
                if(!playerIn.addItem(copy))
                    playerIn.drop(copy, false, true);
                stack.shrink(1);
                entity.remove();
                flag = true;
            }
            if(flag)
                return ActionResult.consume(playerIn.getItemInHand(handIn));
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT tag = NBTTagHelper.getStackTag(stack);
        if(tag.contains("ELEMENT")) {
            tooltip.add((new TranslationTextComponent(LibItem.ELEMENT)).append(" ").append((new TranslationTextComponent(MagickCore.MOD_ID + ".description." + tag.getString("ELEMENT")))));
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
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if(group == ModGroups.ELEMENT_ITEM_GROUP) {
            MagickRegistry.getRegistry(LibRegistry.ELEMENT).registry().forEach( (key, value) ->
                    items.add(NBTTagHelper.setElement(new ItemStack(this), key))
            );
        }
        if(group == ModGroups.ITEM_GROUP) {
            items.add(new ItemStack(this));
        }
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayerEntity) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) p_77663_3_, LibAdvancements.ORB_BOTTLE);
        }
    }
}
