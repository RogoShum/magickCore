package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.HashMap;
import java.util.List;

public class LootUtil {

    public static ItemStack createRandomItemByLucky(int lucky) {
        int tick = (int) (Math.pow(lucky, 3) * MagickCore.rand.nextFloat() * (MagickCore.rand.nextInt(lucky * 2) + 1));
        int type = MagickCore.rand.nextInt(3);
       if(type == 0) {
           tick *= 100;
           if(tick > 2400)
               tick = 2400;
           ItemStack stack = new ItemStack(Items.POTION);
           HashMap<String, MobEffectInstance> map = new HashMap<>();
           MobEffect effect = ModEffects.effectList.get(MagickCore.rand.nextInt(ModEffects.effectList.size()));
           effect.getDescriptionId();
           map.put(effect.getDescriptionId(), new MobEffectInstance(effect, tick, Math.min(MagickCore.rand.nextInt(lucky), 2)));
           PotionUtils.setCustomEffects(stack, map.values());
           stack.setHoverName(new TranslatableComponent(effect.getDescriptionId()));
           return stack;
       } else if(type == 1){
           if(tick > 600)
               tick = 600;
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
               mana += MagickCore.rand.nextInt(Math.max((int) (material.getMana() * 0.25), 1));
           }

           return createRandomManaItem(item, force, tick, Math.min(mana, 5000), trace);
       } else {
           ItemStack stack = new ItemStack(ModItems.SPIRIT_CRYSTAL.get());
           stack.setCount(21 + MagickCore.rand.nextInt(44));
           return stack;
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

    public static SpellContext addEntityType(SpellContext spellContext, EntityType<?> type, float force, int tick, float range, boolean trace, MagickElement element) {
        SpellContext spawnContext = SpellContext.create();
        spawnContext.addChild(SpawnContext.create(type)).force(force).tick(tick).range(range).element(element);
        if(trace)
            spawnContext.addChild(new TraceContext());
        spellContext.post(spawnContext);
        return spellContext;
    }

    public static SpellContext attackType(SpellContext spellContext, float force, int tick, float range, MagickElement element) {
        SpellContext context = SpellContext.create();
        context.applyType(ApplyType.ATTACK).force(force).tick(tick).range(range).element(element);
        spellContext.post(context);
        return spellContext;
    }

    public static SpellContext deBuffType(SpellContext spellContext, float force, int tick, float range, MagickElement element) {
        SpellContext context = SpellContext.create();
        context.applyType(ApplyType.DE_BUFF).force(force).tick(tick).range(range).element(element);
        spellContext.post(context);
        return spellContext;
    }

    public static SpellContext buffType(SpellContext spellContext, float force, int tick, float range, MagickElement element) {
        SpellContext context = SpellContext.create();
        context.applyType(ApplyType.BUFF).force(force).tick(tick).range(range).element(element);
        spellContext.post(context);
        return spellContext;
    }

    public static void modifyLivingLoot(LivingEntity entity, List<ItemStack> stacks) {
        ExtraDataUtil.entityStateData(entity, state -> {
            if (!state.getElement().type().equals(LibElements.ORIGIN) && !entity.level.isClientSide) {
                for (int i = 0; i < stacks.size(); ++i) {
                    ItemStack e = stacks.get(i);
                    if (e.isEdible()) {
                        int count = e.getCount();
                        ItemStack stack = new ItemStack(ModItems.ELEMENT_MEAT.get());
                        CompoundTag tag = new CompoundTag();
                        tag.putString("ELEMENT", state.getElement().type());
                        stack.setCount(count);
                        stack.setTag(tag);
                        stacks.set(i, stack);
                    }

                    if (e.getDescriptionId().contains("wool")) {
                        int count = e.getCount();
                        ItemStack stack = new ItemStack(ModItems.ELEMENT_WOOL.get());
                        CompoundTag tag = new CompoundTag();
                        tag.putString("ELEMENT", state.getElement().type());
                        stack.setCount(count);
                        stack.setTag(tag);
                        stacks.set(i, stack);
                    }

                    if (e.getDescriptionId().contains("string")) {
                        int count = e.getCount();
                        ItemStack stack = new ItemStack(ModItems.ELEMENT_STRING.get());
                        CompoundTag tag = new CompoundTag();
                        tag.putString("ELEMENT", state.getElement().type());
                        stack.setCount(count);
                        stack.setTag(tag);
                        stacks.set(i, stack);
                    }
                }
            }
        });
    }
}
