package com.rogoshum.magickcore.helper;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class NBTTagHelper {
    private static final String TAG_ITEM_DAMAGE = "Damege";
    private static final String TAG_ITEM_COUNT = "Count";
    private static final String TAG_ITEM_TAG = "tag";
    private static final String TAG_ITEM_ID = "id";

    public static CompoundNBT getStackTag(ItemStack stack)
    {
        if(!stack.hasTag())
            stack.setTag(new CompoundNBT());
        return stack.getTag();
    }

    public static CompoundNBT getEntityTag(Entity entity)
    {
        CompoundNBT nbt = new CompoundNBT();
        entity.writeWithoutTypeId(nbt);
        return nbt;
    }

    public static void setEntityTag(Entity entity, CompoundNBT tag)
    {
        entity.read(tag);
    }
}
