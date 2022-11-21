package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.init.ModBuff;
import com.rogoshum.magickcore.common.init.ModDamage;
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
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class WitherAbility{
    public static boolean hitEntity(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuff.applyBuff(context.victim, LibBuff.WITHER, context.tick, context.force, false);
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(ModBuff.hasBuff(context.victim, LibBuff.WITHER))
            context.force *= 1.25;

        boolean flag = false;
        if(context.caster != null && context.projectile != null)
            flag = context.victim.attackEntityFrom(ModDamage.applyProjectileWitherDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            flag = context.victim.attackEntityFrom(ModDamage.applyEntityWitherDamage(context.caster), context.force);
        else if(context.projectile != null)
            flag = context.victim.attackEntityFrom(ModDamage.applyEntityWitherDamage(context.projectile), context.force);
        else
            flag = context.victim.attackEntityFrom(ModDamage.getWitherDamage(), context.force);

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
        return ModBuff.applyBuff(context.victim, LibBuff.DECAY, context.tick * 2, context.force, true);
    }

    public static boolean applyDebuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuff.applyBuff(context.victim, LibBuff.CRIPPLE, context.tick, context.force, false) && ModBuff.applyBuff(context.victim, LibBuff.WITHER, context.tick, context.force, false);
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
