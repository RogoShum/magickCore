package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.item.OrbBottleRenderer;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.init.ModGroups;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.lib.LibItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class OrbBottleItem extends ElementContainerItem {

    public OrbBottleItem() {
        super(properties().stacksTo(64));
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return new OrbBottleRenderer();
            }
        });
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        CompoundTag tag = NBTTagHelper.getStackTag(stack);
        if(tag.contains("ELEMENT")){
            MagickElement element = MagickRegistry.getElement(tag.getString("ELEMENT"));
            MagickContext attribute = new MagickContext(worldIn).tick(60).<MagickContext>force(2).victim(entityLiving).element(element).applyType(ApplyType.DE_BUFF);
            MagickReleaseHelper.releaseMagick(attribute.noCost());
            return ItemStack.EMPTY;
        }
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 16;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
        if(!playerIn.level.isClientSide) {
            CompoundTag tag = NBTTagHelper.getStackTag(stack);
            if (tag.contains("ELEMENT") && target instanceof Animal) {
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
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        List<Entity> list = worldIn.getEntities(playerIn, playerIn.getBoundingBox().inflate(2));
        for (Entity entity : list) {
            boolean flag = false;
            if(entity instanceof ManaElementOrbEntity) {
                ManaElementOrbEntity orb = (ManaElementOrbEntity) entity;
                ItemStack stack = playerIn.getItemInHand(handIn);
                ItemStack copy = stack.copy();
                copy.setCount(1);
                CompoundTag tag = NBTTagHelper.getStackTag(copy);
                tag.putString("ELEMENT", orb.spellContext().element().type());
                if(!playerIn.addItem(copy))
                    playerIn.drop(copy, false, true);
                stack.shrink(1);
                entity.remove(Entity.RemovalReason.DISCARDED);
                flag = true;
            }
            if(flag)
                return InteractionResultHolder.consume(playerIn.getItemInHand(handIn));
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
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
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
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
    public void inventoryTick(ItemStack p_77663_1_, Level p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayer) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) p_77663_3_, LibAdvancements.ORB_BOTTLE);
        }
    }
}
