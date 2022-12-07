package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModDamages;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.ExtraApplyTypeContext;
import com.rogoshum.magickcore.common.magick.context.child.ItemContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.util.ItemStackUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class WitherAbility{
    public static boolean hitEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(context.victim instanceof ItemEntity) {
            ItemEntity itemEntity = ((ItemEntity) context.victim);
            ITagCollection<Item> itagcollection = ItemTags.getCollection();
            AtomicReference<ITag<Item>> iTag = new AtomicReference<>();
            AtomicBoolean ores = new AtomicBoolean(false);
            itagcollection.getIDTagMap().forEach((key, itemITag) -> {
                if((key.getPath().contains("ores/") || key.getPath().contains("ingots/")) && itemEntity.getItem().getItem().isIn(itemITag)) {
                    ResourceLocation res = new ResourceLocation(key.getNamespace(), key.getPath().replace("ingots", "dusts"));
                    if(key.getPath().contains("ores")) {
                        ores.set(true);
                        res = new ResourceLocation(key.getNamespace(), key.getPath().replace("ores", "dusts"));
                    }
                    if(itagcollection.getIDTagMap().containsKey(res))
                        iTag.set(itagcollection.getIDTagMap().get(res));
                    else if(key.getPath().contains("ores/")){
                        res = new ResourceLocation(key.getNamespace(), key.getPath().replace("ores", "gems"));
                        if(itagcollection.getIDTagMap().containsKey(res))
                            iTag.set(itagcollection.getIDTagMap().get(res));
                    }
                }
            });
            if(iTag.get() == null)
                return false;
            ITag<Item> itemITag = iTag.get();
            Item item = itemEntity.getItem().getItem();
            for (Item tagItem : itemITag.getAllElements()) {
                if(tagItem.getRegistryName().getNamespace().equals(itemEntity.getItem().getItem().getRegistryName().getNamespace()))
                    item = tagItem;
                else if(!item.isIn(itemITag))
                    item = tagItem;
            }
            if(item == itemEntity.getItem().getItem()) return false;
            CompoundNBT nbt = itemEntity.getItem().write(new CompoundNBT());
            ResourceLocation resourcelocation = Registry.ITEM.getKey(item);
            nbt.putString("id", resourcelocation.toString());
            ItemStack dustItem = ItemStack.read(nbt);
            if(dustItem.isEmpty()) return false;
            itemEntity.setItem(dustItem);
            if(ores.get()) {
                ItemStack dustCopy = itemEntity.getItem().copy();
                dustItem = ItemStackUtil.mergeStacks(dustItem, dustCopy, 64);
                if(!dustCopy.isEmpty()) {
                    ItemEntity entity = new ItemEntity(itemEntity.world, itemEntity.getPosX(), itemEntity.getPosY(), itemEntity.getPosZ(), dustCopy);
                    if(!entity.world.isRemote)
                        entity.world.addEntity(entity);
                }
            }
            if(context.force >= 7) {
                ItemStack dustCopy = itemEntity.getItem().copy();
                dustItem = ItemStackUtil.mergeStacks(dustItem, dustCopy, 64);
                if(!dustCopy.isEmpty()) {
                    ItemEntity entity = new ItemEntity(itemEntity.world, itemEntity.getPosX(), itemEntity.getPosY(), itemEntity.getPosZ(), dustCopy);
                    if(!entity.world.isRemote)
                        entity.world.addEntity(entity);
                }
            }
            itemEntity.setItem(dustItem);
            ParticleUtil.spawnBlastParticle(context.world, itemEntity.getPositionVec().add(0, itemEntity.getHeight(), 0), 2, ModElements.WITHER, ParticleType.PARTICLE);
            return true;
        } else
            return ModBuffs.applyBuff(context.victim, LibBuff.WITHER, context.tick, context.force, false);
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(ModBuffs.hasBuff(context.victim, LibBuff.WITHER))
            context.force *= 1.25;

        boolean flag = false;
        if(context.caster != null && context.projectile != null)
            flag = context.victim.attackEntityFrom(ModDamages.applyProjectileWitherDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            flag = context.victim.attackEntityFrom(ModDamages.applyEntityWitherDamage(context.caster), context.force);
        else if(context.projectile != null)
            flag = context.victim.attackEntityFrom(ModDamages.applyEntityWitherDamage(context.projectile), context.force);
        else
            flag = context.victim.attackEntityFrom(ModDamages.getWitherDamage(), context.force);

        return flag;
    }

    public static boolean hitBlock(MagickContext context) {
        if(!context.containChild(LibContext.APPLY_TYPE)) return false;
        ExtraApplyTypeContext typeContext = context.getChild(LibContext.APPLY_TYPE);
        if(typeContext.applyType != ApplyType.AGGLOMERATE) return false;
        if(context.world instanceof ServerWorld && context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);
            BlockPos pos = new BlockPos(positionContext.pos);
            BlockState blockstate = context.world.getBlockState(pos);
            if (blockstate.getBlock() instanceof IGrowable) {
                IGrowable igrowable = (IGrowable)blockstate.getBlock();
                for (int i = 0; i < context.force; i++) {
                    igrowable.grow((ServerWorld)context.world, context.world.rand, pos, blockstate);
                }
                return true;
            }
            blockstate = context.world.getBlockState(pos.up());
            if (blockstate.getBlock() instanceof IGrowable) {
                IGrowable igrowable = (IGrowable)blockstate.getBlock();
                for (int i = 0; i < context.force; i++) {
                    igrowable.grow((ServerWorld)context.world, context.world.rand, pos.up(), blockstate);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean applyBuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.DECAY, context.tick * 2, context.force, true);
    }

    public static boolean applyDebuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.CRIPPLE, context.tick, context.force, false);
    }

    public static boolean applyToolElement(MagickContext context) {
        if(!context.containChild(LibContext.ITEM) || context.world.isRemote()) return false;
        ItemContext itemStack = context.getChild(LibContext.ITEM);
        if(!context.valid()) return false;
        if(itemStack.itemStack.getDamage() > 0) {
            NBTTagHelper.consumeElementOnTool(itemStack.itemStack, LibElements.WITHER);
            int maxDamage = 3;
            int damage;
            if(itemStack.itemStack.getDamage() <= maxDamage)
                damage = MagickCore.rand.nextInt(itemStack.itemStack.getDamage());
            else {
                damage = itemStack.itemStack.getDamage() - MagickCore.rand.nextInt(maxDamage);
            }
            itemStack.itemStack.setDamage(damage);
        }
        return true;
    }

    public static boolean superEntity(MagickContext context) {
        if(context.caster == null) return false;
        PositionContext positionContext = PositionContext.create(context.caster.getPositionVec());
        positionContext.pos = positionContext.pos.add(0, 1, 0);
        context.addChild(positionContext);
        SpawnContext spawnContext = new SpawnContext();
        spawnContext.entityType = ModEntities.THORNS_CARESS.get();
        context.addChild(spawnContext);
        context.applyType(ApplyType.SPAWN_ENTITY);
        return MagickReleaseHelper.releaseMagick(context);
    }

    public static boolean diffusion(MagickContext context) {
        if(!(context.victim instanceof LivingEntity)) return false;
        float health = Math.min(((LivingEntity) context.victim).getHealth() - 0.1f, context.force * 0.5f);

        ((LivingEntity) context.victim).setHealth(((LivingEntity) context.victim).getHealth() - health);
        ((LivingEntity)context.victim).setAbsorptionAmount(((LivingEntity) context.victim).getAbsorptionAmount() + health);

        return true;
    }
}
