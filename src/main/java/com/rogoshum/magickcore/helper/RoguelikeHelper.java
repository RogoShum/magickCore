package com.rogoshum.magickcore.helper;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.EnumManaType;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.init.ModEffects;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.item.ManaItem;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.fml.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class RoguelikeHelper {
    public static final float MAX_FORCE = 8;
    public static final float MAX_MANA = 20001;
    public static final float MAX_TICK = 1800;

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
        if(sec == Integer.MAX_VALUE)
            return stack;
        CompoundNBT tag = NBTTagHelper.getStackTag(stack);
        tag.putInt(LibItem.ROGUELIKE_MAX_TICK, sec * 20);
        tag.putInt(LibItem.ROGUELIKE_TICK, sec * 20);
        stack.setTag(tag);
        return stack;
    }

    public static ItemStack createRandomItemWithLucky(int lucky)
    {
        int tick = (int) (Math.pow(lucky, 3) * MagickCore.rand.nextFloat() * MagickCore.rand.nextInt(lucky * 2));
       if(MagickCore.rand.nextInt(3) == 0)
       {
           tick *= 100;
           ItemStack stack = new ItemStack(Items.POTION);
           HashMap<String, EffectInstance> map = new HashMap<>();
           while (MagickCore.rand.nextInt(lucky) != 0)
           {
               Effect effect = ModEffects.effectList.get(MagickCore.rand.nextInt(ModEffects.effectList.size()));
               if(!map.containsKey(effect.getName()))
                   map.put(effect.getName(), new EffectInstance(effect, tick,  MagickCore.rand.nextInt(lucky)));
           }
           PotionUtils.appendEffects(stack, map.values());
           return stack;
       }
       else
       {
           ManaItem item;
           int chance = MagickCore.rand.nextInt(10);
            if(chance < 4)
                item = ModItems.orb_staff.get();
            else if(chance < 6)
                item = ModItems.star_staff.get();
            else if(chance < 8)
                item = ModItems.laser_staff.get();
            else
                item = ModItems.eye.get();

            boolean trace = MagickCore.rand.nextInt(lucky) > 3;
            float force = (float) Math.min(MagickCore.rand.nextInt(lucky) + MagickCore.rand.nextInt(lucky) * 1.1, MAX_FORCE);
            int mana = 0;
            for (int i = 0; i < lucky; ++i)
            {
                mana += MagickCore.rand.nextInt((int) (MAX_MANA / 4));
            }

            return createRandomManaItem(item, force, tick, mana, trace);
       }
    }

    public static ItemStack createRandomManaItem(ManaItem item, float force, int tick, int mana, boolean trace)
    {
        ItemStack stack = new ItemStack(item);

        IManaElement element = ModElements.getElementRandom();
        EnumManaType manaType = EnumManaType.getRandomEnum();
        if(element.getType() == LibElements.ORIGIN)
            manaType = EnumManaType.ATTACK;
        ManaItemHelper.putDataIn(stack, element, Math.max(1, force) , tick, mana , MagickCore.rand.nextInt(4) + 1, trace , manaType);
        return stack;
    }
}
