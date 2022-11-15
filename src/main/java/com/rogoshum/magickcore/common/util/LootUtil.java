package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.magick.materials.Material;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.init.ManaMaterials;
import com.rogoshum.magickcore.common.init.ModEffects;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibMaterial;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.HashMap;

public class LootUtil {

    public static ItemStack createRandomItemByLucky(int lucky) {
        int tick = (int) (Math.pow(lucky, 3) * MagickCore.rand.nextFloat() * (MagickCore.rand.nextInt(lucky * 2) + 1));
       if(MagickCore.rand.nextBoolean()) {
           tick *= 100;
           ItemStack stack = new ItemStack(Items.POTION);
           HashMap<String, EffectInstance> map = new HashMap<>();
           Effect effect = ModEffects.effectList.get(MagickCore.rand.nextInt(ModEffects.effectList.size()));
           effect.getName();
           map.put(effect.getName(), new EffectInstance(effect, tick,  MagickCore.rand.nextInt(lucky)));
           PotionUtils.appendEffects(stack, map.values());
           stack.setDisplayName(new TranslationTextComponent(effect.getName()));
           return stack;
       } else {
           ManaItem item;
           int chance = MagickCore.rand.nextInt(4);
           switch (chance) {
               case 0:
                   item = ModItems.ORB_STAFF.get();
                   break;
               case 1:
                   item = ModItems.STAR_STAFF.get();
                   break;
               case 2:
                   item = ModItems.LASER_STAFF.get();
                   break;
               default:
                   item = ModItems.RAY_STAFF.get();
           }

           Material material = ManaMaterials.getMaterial(LibMaterial.ORIGIN);
           if(lucky > 7)
               material = ManaMaterials.getMaterialRandom();
           boolean trace = MagickCore.rand.nextInt(lucky) > 3;
           float force = (float) Math.min(MagickCore.rand.nextInt(lucky) + MagickCore.rand.nextInt(lucky) * 1.1, material.getMana());
           int mana = 0;
           for (int i = 0; i < lucky; ++i) {
               mana += MagickCore.rand.nextInt((int) (material.getMana() * 0.25));
           }

           return createRandomManaItem(item, force, tick, mana, trace);
       }
    }

    public static ItemStack createRandomManaItem(ManaItem item, float force, int tick, int mana, boolean trace) {
        ItemStack stack = new ItemStack(item);

        MagickElement element = MagickRegistry.getRandomElement();
        ApplyType manaType;
        int chance = MagickCore.rand.nextInt(5);
        switch (chance) {
            case 0:
                manaType = ApplyType.BUFF;
                break;
            case 1:
                manaType = ApplyType.DE_BUFF;
                break;
            case 2:
                manaType = ApplyType.AGGLOMERATE;
                break;
            case 3:
                manaType = ApplyType.DIFFUSION;
                break;
            default:
                manaType = ApplyType.ATTACK;
        }
        if(element.type().equals(LibElements.ORIGIN))
            manaType = ApplyType.ATTACK;
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
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
