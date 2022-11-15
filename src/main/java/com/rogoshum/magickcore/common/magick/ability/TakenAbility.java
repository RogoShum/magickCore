package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.api.enums.ManaLimit;
import com.rogoshum.magickcore.common.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.common.init.ModBuff;
import com.rogoshum.magickcore.common.init.ModDamage;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.ExtraApplyTypeContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class TakenAbility{
    public static boolean hitEntity(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuff.applyBuff(context.victim, LibBuff.TAKEN, context.tick, context.force, true);
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(ModBuff.hasBuff(context.victim, LibBuff.TAKEN))
            context.force *= 1.75;

        boolean flag;
        if(context.caster != null && context.projectile != null)
            flag = context.victim.attackEntityFrom(ModDamage.applyProjectileTakenDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            flag = context.victim.attackEntityFrom(ModDamage.applyEntityTakenDamage(context.caster), context.force);
        else if(context.projectile != null)
            flag = context.victim.attackEntityFrom(ModDamage.applyEntityTakenDamage(context.projectile), context.force);
        else
            flag = context.victim.attackEntityFrom(ModDamage.getTakenDamage(), context.force);

        if(flag && context.force >= ManaLimit.FORCE.getValue() * 1.75 && context.caster != null && context.victim instanceof MobEntity && ModBuff.hasBuff(context.victim, LibBuff.TAKEN)) {
            TakenEntityData state = ExtraDataUtil.takenEntityData(context.victim);
            state.setOwner(context.caster.getUniqueID());
            state.setTime(context.tick);
            context.victim.playSound(SoundEvents.ENTITY_BLAZE_HURT, 2.0F, 0.0f);
        }

        return flag;
    }

    public static boolean hitBlock(MagickContext context) {
        if(!context.world.isRemote && context.containChild(LibContext.POSITION) && context.containChild(LibContext.APPLY_TYPE)) {
            ExtraApplyTypeContext applyTypeContext = context.getChild(LibContext.APPLY_TYPE);
            if (applyTypeContext.applyType != ApplyType.DIFFUSION) return false;
            PositionContext positionContext = context.getChild(LibContext.POSITION);
            BlockPos pos = new BlockPos(positionContext.pos);
            if (context.world.getTileEntity(pos) != null) {
                TileEntity tile = context.world.getTileEntity(pos);
                if (tile instanceof MobSpawnerTileEntity) {
                    CompoundNBT nbt = ((MobSpawnerTileEntity) tile).getSpawnerBaseLogic().write(new CompoundNBT());
                    if(nbt.contains("SpawnData")) {
                        CompoundNBT entityTag = nbt.getCompound("SpawnData");
                        for(int i = 0; i < context.force; i++) {
                            Optional<Entity> optional =  EntityType.loadEntityUnchecked(entityTag, context.world);
                            if(optional.isPresent()) {
                                Entity entity = optional.get();
                                double randX = MagickCore.getNegativeToOne();
                                double randY = MagickCore.getNegativeToOne();
                                double randZ = MagickCore.getNegativeToOne();
                                randX *= 1 + context.range;
                                randY *= 1 + context.range;
                                randZ *= 1 + context.range;
                                entity.setPosition(positionContext.pos.x + randX, positionContext.pos.y + randY, positionContext.pos.z + randZ);
                                entity.world.addEntity(entity);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean applyBuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuff.applyBuff(context.victim, LibBuff.TAKEN_KING, context.tick, context.force, true);
    }

    public static boolean applyDebuff(MagickContext context) {
        if(context.victim instanceof MobEntity && ModBuff.hasBuff(context.victim, LibBuff.TAKEN)) {
            TakenEntityData state = ExtraDataUtil.takenEntityData(context.victim);
            state.setOwner(context.victim.getUniqueID());
            state.setTime((int) (context.tick * context.force));

            return true;
        }
        return false;
    }

    public static boolean superEntity(MagickContext context) {
        if(context.caster == null) return false;
        PositionContext positionContext = PositionContext.create(context.caster.getPositionVec());
        context.addChild(positionContext);
        SpawnContext spawnContext = new SpawnContext();
        spawnContext.entityType = ModEntities.ASCENDANT_REALM.get();
        context.addChild(spawnContext);
        context.applyType(ApplyType.SPAWN_ENTITY);
        return MagickReleaseHelper.releaseMagick(context);
    }

    public static boolean agglomerate(MagickContext context) {
        if(!(context.victim instanceof LivingEntity) || !(context.caster instanceof PlayerEntity)) return false;
        if(context.victim instanceof TameableEntity) {
            ((TameableEntity) context.victim).setTamedBy((PlayerEntity) context.caster);
            context.world.setEntityState(context.victim, (byte)7);
            return true;
        }
        return false;
    }
}
