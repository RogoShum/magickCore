package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModDamages;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.magick.context.child.PositionContext;
import com.rogoshum.magickcore.api.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.network.Networking;
import com.rogoshum.magickcore.common.network.TakenStatePack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;

public class TakenAbility {

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
        if(context.caster != null && context.projectile instanceof Projectile)
            flag = context.victim.hurt(ModDamages.applyProjectileTakenDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            flag = context.victim.hurt(ModDamages.applyEntityTakenDamage(context.caster), context.force);
        else if(context.projectile != null)
            flag = context.victim.hurt(ModDamages.applyEntityTakenDamage(context.projectile), context.force);
        else
            flag = context.victim.hurt(ModDamages.getTakenDamage(), context.force);

        if(flag && context.force >= 9 && context.caster != null && context.victim instanceof Mob && ModBuffs.hasBuff(context.victim, LibBuff.TAKEN)) {
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
        if(context.victim instanceof Mob && ModBuffs.hasBuff(context.victim, LibBuff.TAKEN)) {
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
        if(!(context.victim instanceof LivingEntity) || !(context.caster instanceof Player)) return false;
        if(context.victim instanceof TamableAnimal) {
            ((TamableAnimal) context.victim).tame((Player) context.caster);
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
                BlockEntity tile = context.world.getBlockEntity(pos);
                if (tile instanceof SpawnerBlockEntity) {
                    CompoundTag nbt = ((SpawnerBlockEntity) tile).getSpawner().save(new CompoundTag());
                    if(nbt.contains("SpawnData")) {
                        CompoundTag entityTag = nbt.getCompound("SpawnData");
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

        if(context.victim instanceof Animal animal) {
            if(context.caster instanceof Player)
                animal.setInLove((Player) context.caster);
            else
                animal.setInLove(null);
            animal.setInLoveTime(context.tick * 20);
            return true;
        }

        return false;
    }
}
