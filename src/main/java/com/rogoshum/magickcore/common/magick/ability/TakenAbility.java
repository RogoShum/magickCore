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
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
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
        return ModBuffs.applyBuff(context.victim, LibBuff.TAKEN, context.tick, context.force, true);
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(ModBuffs.hasBuff(context.victim, LibBuff.TAKEN))
            context.force *= 1.75;

        boolean flag;
        if(context.caster != null && context.projectile != null)
            flag = context.victim.attackEntityFrom(ModDamages.applyProjectileTakenDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            flag = context.victim.attackEntityFrom(ModDamages.applyEntityTakenDamage(context.caster), context.force);
        else if(context.projectile != null)
            flag = context.victim.attackEntityFrom(ModDamages.applyEntityTakenDamage(context.projectile), context.force);
        else
            flag = context.victim.attackEntityFrom(ModDamages.getTakenDamage(), context.force);

        if(flag && context.force >= ManaLimit.FORCE.getValue() * 1.75 && context.caster != null && context.victim instanceof MobEntity && ModBuffs.hasBuff(context.victim, LibBuff.TAKEN)) {
            TakenEntityData state = ExtraDataUtil.takenEntityData(context.victim);
            state.setOwner(context.caster.getUniqueID());
            state.setTime(context.tick);
            Networking.INSTANCE.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> context.victim),
                    new TakenStatePack(context.victim.getEntityId(), context.tick, context.victim.getUniqueID()));
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
                        boolean success = false;
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
                                success = true;
                            }
                        }
                        return success;
                    }
                }
            }
        }
        return false;
    }

    public static boolean applyBuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.TAKEN_KING, context.tick * 2, context.force, true);
    }

    public static boolean applyDebuff(MagickContext context) {
        if(context.victim instanceof MobEntity && ModBuffs.hasBuff(context.victim, LibBuff.TAKEN)) {
            TakenEntityData state = ExtraDataUtil.takenEntityData(context.victim);
            state.setOwner(context.victim.getUniqueID());
            int time = (int) (context.tick * context.force);
            state.setTime(time);
            Networking.INSTANCE.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> context.victim),
                    new TakenStatePack(context.victim.getEntityId(), time, context.victim.getUniqueID()));
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
            ((TameableEntity) context.victim).setTamedBy((PlayerEntity) context.caster);
            context.world.setEntityState(context.victim, (byte)7);
            return true;
        }
        return false;
    }
}
