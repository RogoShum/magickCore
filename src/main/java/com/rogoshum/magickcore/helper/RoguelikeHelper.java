package com.rogoshum.magickcore.helper;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.EnumManaType;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.item.ManaItem;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.RegistryObject;

public class RoguelikeHelper {
    public static void HandleTickItem(ItemStack stack)
    {
        CompoundNBT tag = NBTTagHelper.getStackTag(stack);
        if(tag.contains(LibItem.ROGUELIKE_MAX_TICK))
        {
            if(tag.contains(LibItem.ROGUELIKE_TICK))
            {
                int tick = tag.getInt(LibItem.ROGUELIKE_TICK) - 1;
                tag.putInt(LibItem.ROGUELIKE_TICK, tick);
                if(tick < 0)
                    stack.setCount(-1);
            }
            else
                tag.putInt(LibItem.ROGUELIKE_TICK, tag.getInt(LibItem.ROGUELIKE_MAX_TICK));
        }
    }

    public static int getItemRemainTime(ItemStack stack)
    {
        CompoundNBT tag = NBTTagHelper.getStackTag(stack);
        return tag.getInt(LibItem.ROGUELIKE_TICK) / 20;
    }

    public static boolean isRogueItem(ItemStack stack)
    {
        CompoundNBT tag = NBTTagHelper.getStackTag(stack);
        return tag.contains(LibItem.ROGUELIKE_MAX_TICK);
    }

    public static ItemStack TransItemRogue(ItemStack stack, int sec)
    {
        CompoundNBT tag = NBTTagHelper.getStackTag(stack);
        tag.putInt(LibItem.ROGUELIKE_MAX_TICK, sec * 20);
        tag.putInt(LibItem.ROGUELIKE_TICK, sec * 20);
        stack.setTag(tag);
        return stack;
    }

    public static ItemStack createRandomManaItem(RegistryObject<ManaItem> item)
    {
        ItemStack stack = new ItemStack(item.get());

        IManaElement element = ModElements.getElementRandom();
        EnumManaType manaType = EnumManaType.getRandomEnum();
        if(element.getType() == LibElements.ORIGIN)
            manaType = EnumManaType.ATTACK;
        ManaItemHelper.putDataIn(stack, element, MagickCore.rand.nextInt(2) + 5
                , MagickCore.rand.nextInt(570) + 30, MagickCore.rand.nextInt(20000) + 10000
                , MagickCore.rand.nextInt(4) + 1, MagickCore.rand.nextBoolean()
                , manaType);
        return stack;
    }
}
