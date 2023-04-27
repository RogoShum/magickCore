package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class ElementMeatItem extends ElementContainerItem{
    public ElementMeatItem(Properties builder) {
        super(builder);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        CompoundTag tag = NBTTagHelper.getStackTag(stack);
        if (tag.contains("ELEMENT")) {
            MagickContext attribute = new MagickContext(worldIn).victim(entityLiving).applyType(ApplyType.BUFF).element(MagickRegistry.getElement(tag.getString("ELEMENT"))).tick(600).force(2);
            attribute.addChild(TraceContext.create(entityLiving));
            MagickReleaseHelper.releaseMagick(attribute.noCost());
        }
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, Level p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayer) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) p_77663_3_, LibAdvancements.ELEMENT_MEAT);
        }
    }
}
