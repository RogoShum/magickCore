package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.enums.ApplyType;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.context.child.TraceContext;
import com.rogoshum.magickcore.registry.MagickRegistry;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import com.rogoshum.magickcore.magick.context.MagickContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
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
            MagickReleaseHelper.releaseMagick(attribute);
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    }
}
