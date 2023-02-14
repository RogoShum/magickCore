package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.network.EntityStatePack;
import com.rogoshum.magickcore.common.network.Networking;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ModBuffs {
    private static HashMap<String, ManaBuff> buff = new HashMap<>();

    public static void initBuff() {
        putBuff(LibBuff.PARALYSIS, LibElements.ARC, false, (e, force) -> {
            force = Math.max(force, 1);
            e.setDeltaMovement(e.getDeltaMovement().scale(1d/(force + 1d)*2d));
        } , true);

        putBuff(LibBuff.SLOW, LibElements.STASIS, false, (e, force) -> {
            force = Math.max(force, 1);
            e.setDeltaMovement(e.getDeltaMovement().scale(1d/(force + 0.5d)));
        } , true);

        putBuff(LibBuff.WITHER, LibElements.WITHER, false, (e, force) -> {
            if(e instanceof LivingEntity && ((LivingEntity)e).getHealth() > 0.01f)
                ((LivingEntity)e).setHealth(Math.max(0.01f, ((LivingEntity)e).getHealth() - 0.025f * force));
        } , false);

        putBuff(LibBuff.FRAGILE, LibElements.VOID, false, (e, force) -> {
            e.invulnerableTime -= force;
        } , false);

        putBuff(LibBuff.HYPERMUTEKI, LibElements.SOLAR, true, (e, force) ->{
            if(e instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) e;
                living.setHealth(living.getMaxHealth());
                living.setAbsorptionAmount(20f);
                living.invulnerableTime = 200;
                living.hurtTime = 0;
            }
        }, true);

        putBuff(LibBuff.RADIANCE_WELL, LibElements.SOLAR, true, (e, force) -> {
            if(e instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) e;
                living.heal(living.getMaxHealth() * 0.05f * force);
                if(living.getHealth() == living.getMaxHealth() && living.getAbsorptionAmount() < living.getMaxHealth()) {
                    living.setAbsorptionAmount(living.getAbsorptionAmount() + living.getMaxHealth() * 0.01f * force);
                }
                living.hurtTime = 0;
            }
        }, true);

        putBuff(LibBuff.DECAY, LibElements.WITHER, true, (e, force) ->{
            if(e instanceof LivingEntity) {
                ((LivingEntity) e).getActiveEffectsMap().keySet().removeIf(effect -> !effect.isBeneficial() && ((LivingEntity) e).getActiveEffectsMap().get(effect).getAmplifier() <= force);
            }
        }, true);

        putBuff(LibBuff.TAKEN_KING, LibElements.TAKEN, true, (e, force) ->{}, true);
        putBuff(LibBuff.WEAKEN, LibElements.VOID, false, (e, force) ->{}, false);
        putBuff(LibBuff.TAKEN, LibElements.TAKEN, false, (e, force) ->{}, true);
        putBuff(LibBuff.CRIPPLE, LibElements.WITHER, false, (e, force) ->{}, false);
        putBuff(LibBuff.FREEZE, LibElements.STASIS, false, (e, force) ->{}, false);
        putBuff(LibBuff.STASIS, LibElements.STASIS, true, (e, force) ->{
            force *=2;
            List<Entity> entityList = e.level.getEntities(e, e.getBoundingBox().inflate(force, force, force));

            for(int i = 0; i< entityList.size(); ++i) {
                Entity entity = entityList.get(i);
                if(!MagickReleaseHelper.sameLikeOwner(e, entity)) {
                    ModBuffs.applyBuff(entity, LibBuff.SLOW, 20, force, true);
                }
            }
        }, true);
        putBuff(LibBuff.LIGHT, LibElements.VOID, true, (e, force) ->{}, true);
        putBuff(LibBuff.INVISIBILITY, LibElements.VOID, true, (e, force) ->{}, true);
        putBuff(LibBuff.PURE, LibElements.STASIS, true, (e, force) ->{
            force *=2;
            List<Entity> entityList = e.level.getEntities(e, e.getBoundingBox().inflate(force, force, force));

            for(int i = 0; i< entityList.size(); ++i) {
                Entity entity = entityList.get(i);
                if(!MagickReleaseHelper.sameLikeOwner(e, entity) && !(entity instanceof LivingEntity)) {
                    double factor = entity.getDeltaMovement().normalize().dot(e.position().add(0, e.getBbHeight() * 0.5, 0).subtract(entity.position().add(0, entity.getBbHeight() * 0.5, 0)).normalize());
                    if(factor > 0.8) {
                        Vec3 motion = entity.getDeltaMovement();
                        entity.push(-motion.x, -motion.y, -motion.z);
                    }
                }
            }
        }, true);
    }

    protected static void putBuff(String s, String element, boolean beneficial, BiConsumer<? super Entity, Float> func, boolean canRefreshBuff) {
        ManaBuff buf = new ManaBuff(s, element) {
            @Override
            public void effectEntity(Entity entity, float force) {
                func.accept(entity, force);
            }

            @Override
            public boolean canRefreshBuff() {
                return canRefreshBuff;
            }
        };

        if(beneficial)
            buff.put(s, buf.beneficial());
        else
            buff.put(s, buf);
    }

    public static ManaBuff getBuff(String type) {
        ManaBuff b = null;
        try {
            if(buff.containsKey(type))
                b = buff.get(type).clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return b;
    }

    public static boolean hasBuff(Entity entity, String type) {
        if(entity == null)
            return false;
        AtomicBoolean hasBuff = new AtomicBoolean(false);
        ExtraDataUtil.entityData(entity).<EntityStateData>execute(LibEntityData.ENTITY_STATE, state -> {
            if(state.getBuffList().containsKey(type))
                hasBuff.set(true);
        });

        return hasBuff.get();
    }

    public static boolean applyBuff(Entity entity, String type, int tick, float force, boolean beneficial) {
        if(!(entity instanceof LivingEntity))
            return false;
        ManaBuff buff = getBuff(type);
        if(buff == null) return false;
        EntityEvents.ApplyManaBuffEvent event = new EntityEvents.ApplyManaBuffEvent((LivingEntity) entity, buff, beneficial);
        MinecraftForge.EVENT_BUS.post(event);
        if(event.isCanceled())
            return false;
        if(force < 1 && force > 0.4)
            force = 1;

        boolean applied = false;
        EntityStateData state = ExtraDataUtil.entityStateData(entity);
        if(state != null) {
            applied = (state.applyBuff(getBuff(type).setTick(tick).setForce(force)));
            if(applied && !entity.level.isClientSide) {
                CompoundTag tag = new CompoundTag();
                state.write(tag);
                Networking.INSTANCE.send(
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(event::getEntity),
                        new EntityStatePack(event.getEntity().getId(), tag));
            }
        }
        return applied;
    }
}
