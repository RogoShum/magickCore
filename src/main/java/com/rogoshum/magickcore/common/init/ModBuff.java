package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.common.api.event.EntityEvents;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibElements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ModBuff{
    private static HashMap<String, ManaBuff> buff = new HashMap<>();

    public static void initBuff()
    {
        putBuff(LibBuff.PARALYSIS, LibElements.ARC, false, (e) -> {
            ExtraDataUtil.entityData(e).<EntityStateData>execute(LibEntityData.ENTITY_STATE, state -> {
                float force = state.getBuffList().get(LibBuff.PARALYSIS).getForce();
                force = Math.max(force, 1);
                e.setMotion(e.getMotion().scale(1d/(force + 1d)*2d));
            });
        } , true);

        putBuff(LibBuff.SLOW, LibElements.STASIS, false, (e) -> {
            ExtraDataUtil.entityData(e).<EntityStateData>execute(LibEntityData.ENTITY_STATE, state -> {
                float force = state.getBuffList().get(LibBuff.SLOW).getForce();
                force = Math.max(force, 1);
                e.setMotion(e.getMotion().scale(1d/(force + 0.5d)));
            });
        } , true);

        putBuff(LibBuff.WITHER, LibElements.WITHER, false, (e) -> {
            ExtraDataUtil.entityData(e).<EntityStateData>execute(LibEntityData.ENTITY_STATE, state -> {
                if(e instanceof LivingEntity && ((LivingEntity)e).getHealth() > 0.01f)
                    ((LivingEntity)e).setHealth(Math.max(0.01f, ((LivingEntity)e).getHealth() - 0.025f * state.getBuffList().get(LibBuff.WITHER).getForce()));
            });
        } , false);

        putBuff(LibBuff.FRAGILE, LibElements.VOID, false, (e) -> {
            ExtraDataUtil.entityData(e).<EntityStateData>execute(LibEntityData.ENTITY_STATE, state -> {
                if(state.getBuffList().get(LibBuff.FRAGILE).getForce() > 5)
                {
                    if(e.hurtResistantTime > 7)
                        e.hurtResistantTime = 7;
                }
                else if(e.hurtResistantTime > 13)
                    e.hurtResistantTime = 13;
            });
        } , false);

        putBuff(LibBuff.HYPERMUTEKI, LibElements.SOLAR, true, (e) ->{
            if(e instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) e;
                living.setHealth(living.getMaxHealth());
                living.setAbsorptionAmount(20f);
                living.hurtResistantTime = 200;
                living.hurtTime = 0;
            }
        }, true);

        putBuff(LibBuff.RADIANCE_WELL, LibElements.SOLAR, true, (e) ->{
            if(e instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) e;
                living.heal(living.getMaxHealth() * 0.1f);
                if(living.getHealth() == living.getMaxHealth() && living.getAbsorptionAmount() < living.getMaxHealth()) {
                    living.setAbsorptionAmount(living.getAbsorptionAmount() + living.getMaxHealth() * 0.1f);
                }
                living.hurtTime = 0;
            }
        }, true);

        putBuff(LibBuff.DECAY, LibElements.WITHER, true, (e) ->{
            if(e instanceof LivingEntity) {
                ExtraDataUtil.entityData(e).<EntityStateData>execute(LibEntityData.ENTITY_STATE, state -> {
                    ((LivingEntity) e).getActivePotionMap().keySet().removeIf(effect -> !effect.isBeneficial() && ((LivingEntity) e).getActivePotionMap().get(effect).getAmplifier() <= (int) state.getBuffList().get(LibBuff.DECAY).getForce());
                });
            }
        }, true);

        putBuff(LibBuff.TAKEN_KING, LibElements.TAKEN, true, (e) ->{
            if(e instanceof LivingEntity) {
                ExtraDataUtil.entityStateData(e, state -> ((LivingEntity) e).heal(state.getBuffList().get(LibBuff.TAKEN_KING).getForce() / 20f));
            }
        }, true);

        putBuff(LibBuff.WEAKEN, LibElements.VOID, false, (e) ->{}, false);
        putBuff(LibBuff.TAKEN, LibElements.TAKEN, false, (e) ->{}, true);
        putBuff(LibBuff.CRIPPLE, LibElements.WITHER, false, (e) ->{}, false);
        putBuff(LibBuff.FREEZE, LibElements.STASIS, false, (e) ->{}, false);
        putBuff(LibBuff.STASIS, LibElements.STASIS, true, (e) ->{}, true);
        putBuff(LibBuff.LIGHT, LibElements.VOID, true, (e) ->{}, true);
        putBuff(LibBuff.INVISIBILITY, LibElements.VOID, true, (e) ->{}, true);
        putBuff(LibBuff.PURE, LibElements.STASIS, true, (e) ->{}, true);
    }

    protected static void putBuff(String s, String element, boolean beneficial, Consumer<? super Entity> func, boolean canRefreshBuff) {
        ManaBuff buf = new ManaBuff(s, element) {
            @Override
            public void effectEntity(Entity entity) {
                func.accept(entity);
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

        AtomicBoolean applied = new AtomicBoolean(false);
        ExtraDataUtil.entityData(entity).<EntityStateData>execute(LibEntityData.ENTITY_STATE, state -> applied.set(state.applyBuff(getBuff(type).setTick(tick).setForce(force))));

        return applied.get();
    }
}
