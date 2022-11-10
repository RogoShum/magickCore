package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class ElementMeatItem extends ElementContainerItem{
    public ElementMeatItem(Properties builder) {
        super(builder);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        CompoundNBT tag = NBTTagHelper.getStackTag(stack);
        if (tag.contains("ELEMENT")) {
            MagickContext attribute = new MagickContext(worldIn).applyType(ApplyType.BUFF).element(MagickRegistry.getElement(tag.getString("ELEMENT"))).tick(300).force(2);
            attribute.addChild(TraceContext.create(entityLiving));
            MagickReleaseHelper.releaseMagick(attribute.noCost());
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }
}
