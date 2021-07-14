package com.rogoshum.magickcore.helper;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.lib.LibElementTool;
import com.rogoshum.magickcore.lib.LibElements;
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

    public static boolean hasElement(ItemStack stack) {
        return getStackTag(stack).contains("ELEMENT");
    }

    public static ItemStack setElement(ItemStack stack, String element)
    {
        if(!stack.isEmpty())
        {
            CompoundNBT tag = getStackTag(stack);
            tag.putString("ELEMENT", element);
            stack.setTag(tag);
        }

        return stack;
    }

    public static String getElement(ItemStack stack)
    {
        if(!stack.isEmpty() && hasElement(stack))
            return getStackTag(stack).getString("ELEMENT");

        return LibElements.ORIGIN;
    }

    public static boolean hasElementOnTool(ItemStack stack, String element)
    {
        try {
            if (!stack.isEmpty() && getToolElementTable(stack).contains(element))
                return true;
        }
        catch (Exception exception)
        {
            MagickCore.LOGGER.info(stack);
            MagickCore.LOGGER.info(element);
            exception.printStackTrace();
        }
        return false;
    }

    public static boolean consumeElementOnTool(ItemStack stack, String element)
    {
        if(!stack.isEmpty() && hasElementOnTool(stack, element))
        {
            CompoundNBT tag = getToolElementTable(stack);
            int count = tag.getInt(element);
            if(count > 0)
                tag.putInt(element, count - 1);
            else
                tag.remove(element);

            return true;
        }
        return false;
    }

    public static boolean putElementOnTool(ItemStack stack, String element)
    {
        CompoundNBT tag = getToolElementTable(stack);
        tag.putInt(element, 300);
        CompoundNBT nbt = getStackTag(stack);
        nbt.put(LibElementTool.TOOL_ELEMENT, tag);
        stack.setTag(nbt);

        return false;
    }

    public static CompoundNBT getToolElementTable(ItemStack stack)
    {
        CompoundNBT tag = getStackTag(stack);
        if(!tag.contains(LibElementTool.TOOL_ELEMENT))
            tag.put(LibElementTool.TOOL_ELEMENT, new CompoundNBT());

        return tag.getCompound(LibElementTool.TOOL_ELEMENT);
    }

    public static void setEntityTag(Entity entity, CompoundNBT tag)
    {
        entity.read(tag);
    }
}
