package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.api.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.magick.materials.Material;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.common.init.ManaMaterials;
import com.rogoshum.magickcore.common.init.ModEffects;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibMaterial;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.magick.context.child.TraceContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.HashMap;

public class LootUtil {

    public static ItemStack createRandomItemByLucky(int lucky) {
        int tick = (int) (Math.pow(lucky, 3) * MagickCore.rand.nextFloat() * (MagickCore.rand.nextInt(lucky * 2) + 1));
        int type = MagickCore.rand.nextInt(7);
       if(type < 2) {
           tick *= 100;
           if(tick > 2400)
               tick = 2400;
           ItemStack stack = new ItemStack(Items.POTION);
           HashMap<String, MobEffectInstance> map = new HashMap<>();
           MobEffect effect = ModEffects.effectList.get(MagickCore.rand.nextInt(ModEffects.effectList.size()));
           if(!effect.isBeneficial())
               return ItemStack.EMPTY;
           map.put(effect.getDescriptionId(), new MobEffectInstance(effect, tick, Math.min(MagickCore.rand.nextInt(lucky), 2)));
           PotionUtils.setCustomEffects(stack, map.values());
           stack.setHoverName(new TranslatableComponent(effect.getDescriptionId()));
           return stack;
       } else if(type < 4) {
           if(tick > 600)
               tick = 600;
           ManaItem item;
           EntityType<? extends IManaEntity> entityType;
           int chance = MagickCore.rand.nextInt(4);
           switch (chance) {
               case 0 -> {
                   item = ModItems.ORB_STAFF.get();
                   entityType = ModEntities.MANA_ORB.get();
               }
               case 1 -> {
                   item = ModItems.STAR_STAFF.get();
                   entityType = ModEntities.MANA_STAR.get();
               }
               case 2 -> {
                   item = ModItems.LASER_STAFF.get();
                   entityType = ModEntities.MANA_LASER.get();
               }
               default -> {
                   item = ModItems.RAY_STAFF.get();
                   entityType = ModEntities.RAY.get();
               }
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

           return createRandomManaItem(item, entityType, force, tick, Math.min(mana, 50000), trace);
       } else {
           ItemStack stack = new ItemStack(ModItems.SPIRIT_CRYSTAL.get());
           stack.setCount(21 + MagickCore.rand.nextInt(22));
           return stack;
       }
    }

    public static ItemStack createRandomManaItem(ManaItem item, EntityType<? extends IManaEntity> entityType, float force, int tick, int mana, boolean trace) {
        ItemStack stack = new ItemStack(item);

        MagickElement element = MagickRegistry.getRandomElement();

        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        SpellContext projectileContext = createEntityType(data.spellContext(), entityType, 3, 200, 0, trace, element);

        if(MagickCore.rand.nextBoolean()) {
            EntityType<? extends IManaEntity> rangeType;
            int chance = MagickCore.rand.nextInt(4);
            rangeType = switch (chance) {
                case 1 -> ModEntities.SPHERE.get();
                case 2 -> ModEntities.SQUARE.get();
                case 3 -> ModEntities.SECTOR.get();
                default -> ModEntities.MANA_SPHERE.get();
            };
            SpellContext rangeContext = createEntityType(SpellContext.create(), rangeType, 0, 100, MagickCore.rand.nextInt(4) + 3, false, element);
            if(chance > 0 && MagickCore.rand.nextBoolean()) {
                SpellContext additionContext = addEntityType(rangeContext, entityType, 3, 100, 0, trace, element);
                randomType(additionContext, Math.max(1, force), tick, MagickCore.rand.nextInt(4) + 1, element);
                rangeContext.post(additionContext);
            } else {
                randomType(rangeContext, Math.max(1, force), tick, MagickCore.rand.nextInt(4) + 1, element);
            }
            data.spellContext().post(rangeContext);
        } else {
            randomType(data.spellContext(), Math.max(1, force), tick, MagickCore.rand.nextInt(4) + 1, element);
        }
        data.manaCapacity().setMana(mana);
        return stack;
    }

    public static void randomType(SpellContext context, float force, int tick, float range, MagickElement element) {
        if(element.type().equals(LibElements.ORIGIN)) {
            attackType(context, force, tick, range, element);
        } else {
            int chance = MagickCore.rand.nextInt(4);
            switch (chance) {
                case 1 -> deBuffType(context, force, tick, range, element);
                case 2 -> agglomerateType(context, force, tick, range, element);
                case 3 -> diffusionType(context, force, tick, range, element);
                default -> attackType(context, force, tick, range, element);
            }
        }
    }

    public static SpellContext createEntityType(SpellContext spawnContext, EntityType<?> type, float force, int tick, float range, boolean trace, MagickElement element) {
        spawnContext.applyType(ApplyType.SPAWN_ENTITY).addChild(SpawnContext.create(type)).force(force).tick(tick).range(range).element(element);
        if(trace)
            spawnContext.addChild(new TraceContext());
        return spawnContext;
    }

    public static SpellContext createEntityType(EntityType<?> type, float force, int tick, float range, boolean trace, MagickElement element) {
        SpellContext spawnContext = SpellContext.create();
        spawnContext.applyType(ApplyType.SPAWN_ENTITY).addChild(SpawnContext.create(type)).force(force).tick(tick).range(range).element(element);
        if(trace)
            spawnContext.addChild(new TraceContext());
        return spawnContext;
    }

    public static SpellContext addEntityType(SpellContext spellContext, EntityType<?> type, float force, int tick, float range, boolean trace, MagickElement element) {
        SpellContext spawnContext = SpellContext.create();
        spawnContext.applyType(ApplyType.SPAWN_ENTITY).addChild(SpawnContext.create(type)).force(force).tick(tick).range(range).element(element);
        if(trace)
            spawnContext.addChild(new TraceContext());
        spellContext.post(spawnContext);
        return spawnContext;
    }

    public static SpellContext attackType(SpellContext spellContext, float force, int tick, float range, MagickElement element) {
        SpellContext context = SpellContext.create();
        context.applyType(ApplyType.ATTACK).force(force).tick(tick).range(range).element(element);
        spellContext.post(context);
        return context;
    }

    public static SpellContext deBuffType(SpellContext spellContext, float force, int tick, float range, MagickElement element) {
        SpellContext context = SpellContext.create();
        context.applyType(ApplyType.DE_BUFF).force(force).tick(tick).range(range).element(element);
        spellContext.post(context);
        return context;
    }

    public static SpellContext diffusionType(SpellContext spellContext, float force, int tick, float range, MagickElement element) {
        SpellContext context = SpellContext.create();
        context.applyType(ApplyType.DIFFUSION).force(force).tick(tick).range(range).element(element);
        spellContext.post(context);
        return context;
    }

    public static SpellContext agglomerateType(SpellContext spellContext, float force, int tick, float range, MagickElement element) {
        SpellContext context = SpellContext.create();
        context.applyType(ApplyType.AGGLOMERATE).force(force).tick(tick).range(range).element(element);
        spellContext.post(context);
        return context;
    }
}
