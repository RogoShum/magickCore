package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.enums.ManaLimit;
import com.rogoshum.magickcore.common.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModDamages;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.ExtraApplyTypeContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.network.Networking;
import com.rogoshum.magickcore.common.network.TakenStatePack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Optional;

public class TakenAbility{
    public static boolean hitEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(!context.victim.canChangeDimensions())
            return ModBuffs.applyBuff(context.victim, LibBuff.TAKEN, context.tick / 2, context.force, false);
        return ModBuffs.applyBuff(context.victim, LibBuff.TAKEN, context.tick, context.force, true);
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(ModBuffs.hasBuff(context.victim, LibBuff.TAKEN))
            context.force *= 1.25;

        boolean flag;
        if(context.caster != null && context.projectile instanceof ProjectileEntity)
            flag = context.victim.hurt(ModDamages.applyProjectileTakenDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            flag = context.victim.hurt(ModDamages.applyEntityTakenDamage(context.caster), context.force);
        else if(context.projectile != null)
            flag = context.victim.hurt(ModDamages.applyEntityTakenDamage(context.projectile), context.force);
        else
            flag = context.victim.hurt(ModDamages.getTakenDamage(), context.force);

        if(flag && context.force >= 9 && context.caster != null && context.victim instanceof MobEntity && ModBuffs.hasBuff(context.victim, LibBuff.TAKEN)) {
            TakenEntityData state = ExtraDataUtil.takenEntityData(context.victim);
            state.setOwner(context.caster.getUUID());
            state.setTime(context.tick);
            Networking.INSTANCE.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> context.victim),
                    new TakenStatePack(context.victim.getId(), context.tick, context.victim.getUUID()));
            context.victim.playSound(SoundEvents.BLAZE_HURT, 2.0F, 0.0f);
        }

        return flag;
    }

    public static boolean hitBlock(MagickContext context) {
        return false;
    }

    public static boolean applyBuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.TAKEN_KING, context.tick * 2, context.force, true);
    }

    public static boolean applyDebuff(MagickContext context) {
        if(context.victim instanceof MobEntity && ModBuffs.hasBuff(context.victim, LibBuff.TAKEN)) {
            if(!context.victim.canChangeDimensions()) return false;
            TakenEntityData state = ExtraDataUtil.takenEntityData(context.victim);
            state.setOwner(context.caster.getUUID());
            int time = (int) (context.tick * context.force);
            state.setTime(time);
            context.victim.playSound(SoundEvents.BLAZE_HURT, 2.0F, 0.0f);
            Networking.INSTANCE.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> context.victim),
                    new TakenStatePack(context.victim.getId(), time, context.victim.getUUID()));
            return true;
        }
        return false;
    }

    public static boolean superEntity(MagickContext context) {
        if(context.caster == null) return false;
        SpawnContext spawnContext = new SpawnContext();
        spawnContext.entityType = ModEntities.ASCENDANT_REALM.get();
        context.addChild(spawnContext);
        context.applyType(ApplyType.SPAWN_ENTITY);
        return MagickReleaseHelper.releaseMagick(context);
    }

    public static boolean agglomerate(MagickContext context) {
        if(!(context.victim instanceof LivingEntity) || !(context.caster instanceof PlayerEntity)) return false;
        if(context.victim instanceof TameableEntity) {
            ((TameableEntity) context.victim).tame((PlayerEntity) context.caster);
            context.world.broadcastEntityEvent(context.victim, (byte)7);
            return true;
        }
        return false;
    }

    public static boolean diffusion(MagickContext context) {
        if(context.doBlock && context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);
            BlockPos pos = new BlockPos(positionContext.pos);
            if (context.world.getBlockEntity(pos) != null) {
                TileEntity tile = context.world.getBlockEntity(pos);
                if (tile instanceof MobSpawnerTileEntity) {
                    CompoundNBT nbt = ((MobSpawnerTileEntity) tile).getSpawner().save(new CompoundNBT());
                    if(nbt.contains("SpawnData")) {
                        CompoundNBT entityTag = nbt.getCompound("SpawnData");
                        boolean success = false;
                        for(int i = 0; i < context.force; i++) {
                            Optional<Entity> optional =  EntityType.create(entityTag, context.world);
                            if(optional.isPresent()) {
                                Entity entity = optional.get();
                                double randX = MagickCore.getNegativeToOne();
                                double randY = MagickCore.getNegativeToOne();
                                double randZ = MagickCore.getNegativeToOne();
                                randX *= 1 + context.range;
                                randY *= 1 + context.range;
                                randZ *= 1 + context.range;
                                entity.setPos(positionContext.pos.x + randX, positionContext.pos.y + randY, positionContext.pos.z + randZ);
                                entity.level.addFreshEntity(entity);
                                success = true;
                            }
                        }
                        return success;
                    }
                }
            }
        }

        if(context.victim instanceof AnimalEntity) {
            AnimalEntity animal = (AnimalEntity) context.victim;
            if(context.caster instanceof PlayerEntity)
                animal.setInLove((PlayerEntity) context.caster);
            else
                animal.setInLove(null);
            animal.setInLoveTime(context.tick);
            return true;
        }

        return false;
    }
}
