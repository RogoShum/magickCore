package com.rogoshum.magickcore.tool;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.enums.ApplyType;
import com.rogoshum.magickcore.init.ManaMaterials;
import com.rogoshum.magickcore.init.ModEffects;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.item.ManaItem;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibMaterial;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.magick.context.child.TraceContext;
import com.rogoshum.magickcore.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.magick.materials.Material;
import com.rogoshum.magickcore.registry.MagickRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;

import java.util.HashMap;

public class RoguelikeHelper {

    public static ItemStack createRandomItemByLucky(int lucky) {
        int tick = (int) (Math.pow(lucky, 3) * MagickCore.rand.nextFloat() * (MagickCore.rand.nextInt(lucky * 2) + 1));
       if(MagickCore.rand.nextInt(3) == 0)
       {
           tick *= 100;
           ItemStack stack = new ItemStack(Items.POTION);
           HashMap<String, EffectInstance> map = new HashMap<>();
           while (MagickCore.rand.nextInt(lucky) != 0) {
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
            else
                item = ModItems.laser_staff.get();

           Material material = ManaMaterials.getMaterial(LibMaterial.ORIGIN);
           if(lucky > 7)
                material = ManaMaterials.getMaterialRandom();
            boolean trace = MagickCore.rand.nextInt(lucky) > 3;
            float force = (float) Math.min(MagickCore.rand.nextInt(lucky) + MagickCore.rand.nextInt(lucky) * 1.1, material.getMana());
            int mana = 0;
            for (int i = 0; i < lucky; ++i)
            {
                mana += MagickCore.rand.nextInt((int) (material.getMana() / 4));
            }

            return createRandomManaItem(item, material, force, tick, mana, trace);
       }
    }

    public static ItemStack createRandomManaItem(ManaItem item, Material material, float force, int tick, int mana, boolean trace) {
        ItemStack stack = new ItemStack(item);

        MagickElement element = MagickRegistry.getRandomElement();
        ApplyType manaType = ApplyType.getRandomEnum();
        if(element.type().equals(LibElements.ORIGIN))
            manaType = ApplyType.ATTACK;
        ItemManaData data = ExtraDataHelper.itemManaData(stack);
        data.spellContext().element(element);
        data.spellContext().force(Math.max(1, force));
        data.spellContext().applyType(manaType);
        data.spellContext().tick(tick);
        data.manaCapacity().setMana(mana);
        data.spellContext().range(MagickCore.rand.nextInt(4) + 1);
        if(trace)
            data.spellContext().addChild(new TraceContext());
        return stack;
    }
}
