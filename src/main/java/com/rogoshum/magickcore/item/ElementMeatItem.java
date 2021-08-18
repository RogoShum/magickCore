package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
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
            ReleaseAttribute attribute = new ReleaseAttribute(null, null, entityLiving, 300, 2);
            MagickReleaseHelper.applyElementFunction(ModElements.getElement(tag.getString("ELEMENT")), EnumManaType.BUFF, attribute);
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    }
}
