package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.buff.ManaBuff;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.lib.LibBuff;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Consumer;

public class ModBuff{
    private static HashMap<String, ManaBuff> buff = new HashMap<>();

    public static void initBuff()
    {
        putBuff(LibBuff.PARALYSIS, (e) -> {
            IEntityState state = e.getCapability(MagickCore.entityState).orElse(null);
            if(state != null) {
                float force = state.getBuffList().get(LibBuff.PARALYSIS).getForce();
                force = Math.max(force, 1);
                e.setMotion(e.getMotion().scale(1d/(force + 1d)*2d));
            }
        } , true);

        putBuff(LibBuff.SLOW, (e) -> {
            IEntityState state = e.getCapability(MagickCore.entityState).orElse(null);
            if(state != null) {
                float force = state.getBuffList().get(LibBuff.SLOW).getForce();
                force = Math.max(force, 1);
                e.setMotion(e.getMotion().scale(1d/(force + 0.5d)));
            }
        } , true);

        putBuff(LibBuff.WITHER, (e) -> {
            IEntityState state = e.getCapability(MagickCore.entityState).orElse(null);
            if(state != null) {
                if(e instanceof LivingEntity && ((LivingEntity)e).getHealth() > 0.01f)
                    ((LivingEntity)e).setHealth(Math.max(0.01f, ((LivingEntity)e).getHealth() - 0.025f * state.getBuffList().get(LibBuff.WITHER).getForce()));
            }
        } , false);

        putBuff(LibBuff.FRAGILE, (e) -> {
            IEntityState state = e.getCapability(MagickCore.entityState).orElse(null);
            if(state != null) {
                if(state.getBuffList().get(LibBuff.FRAGILE).getForce() > 5)
                {
                    if(e.hurtResistantTime > 7)
                        e.hurtResistantTime = 7;
                }
                else if(e.hurtResistantTime > 13)
                    e.hurtResistantTime = 13;
            }
        } , false);

        putBuff(LibBuff.HYPERMUTEKI, (e) ->{
            if(e instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) e;
                living.setHealth(living.getMaxHealth());
                living.setAbsorptionAmount(20f);
                living.hurtResistantTime = 200;
                living.hurtTime = 0;
            }
        }, true);

        putBuff(LibBuff.RADIANCE_WELL, (e) ->{
            IEntityState state = e.getCapability(MagickCore.entityState).orElse(null);
            if(e instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) e;
                living.heal(living.getMaxHealth() * 0.1f);
                if(living.getHealth() == living.getMaxHealth() && living.getAbsorptionAmount() < living.getMaxHealth())
                {
                    living.setAbsorptionAmount(living.getAbsorptionAmount() + living.getMaxHealth() * 0.1f);
                }
                living.hurtTime = 0;
            }
        }, true);

        putBuff(LibBuff.DECAY, (e) ->{
            IEntityState state = e.getCapability(MagickCore.entityState).orElse(null);
            if(e instanceof LivingEntity) {
                ((LivingEntity) e).getActivePotionMap().keySet().removeIf(effect -> !effect.isBeneficial() && ((LivingEntity) e).getActivePotionMap().get(effect).getAmplifier() <= (int) state.getBuffList().get(LibBuff.DECAY).getForce());
            }
        }, true);

        putBuff(LibBuff.TAKEN_KING, (e) ->{
            IEntityState state = e.getCapability(MagickCore.entityState).orElse(null);
            if(e instanceof LivingEntity) {
                ((LivingEntity) e).heal(state.getBuffList().get(LibBuff.TAKEN_KING).getForce() / 20f);
            }
        }, true);

        putBuff(LibBuff.WEAKEN, (e) ->{}, false);
        putBuff(LibBuff.TAKEN, (e) ->{}, true);
        putBuff(LibBuff.CRIPPLE, (e) ->{}, false);
        putBuff(LibBuff.FREEZE, (e) ->{}, false);
        putBuff(LibBuff.STASIS, (e) ->{}, true);
        putBuff(LibBuff.LIGHT, (e) ->{}, true);
    }

    protected static void putBuff(String s, Consumer<? super Entity> func, boolean canRefreshBuff)
    {
        buff.put(s, new ManaBuff(s) {
            @Override
            public void effectEntity(Entity entity) {
                func.accept(entity);
            }

            @Override
            public boolean canRefreshBuff() {
                return canRefreshBuff;
            }
        });
    }

    public static ManaBuff getBuff(String type){
        ManaBuff b = null;
        try {
            b = buff.get(type).clone();
        } catch (CloneNotSupportedException e) {
        e.printStackTrace();
        }
        return b;
    }

    public static boolean hasBuff(Entity entity, String type){
        if(entity == null)
            return false;
        IEntityState state = entity.getCapability(MagickCore.entityState).orElse(null);
        if(state != null)
            return state.getBuffList().containsKey(type);

        return false;
    }

    public static boolean applyBuff(Entity entity, String type, int tick, float force, boolean beneficial){
        if(!(entity instanceof LivingEntity))
            return false;
        EntityEvents.ApplyManaBuffEvent event = new EntityEvents.ApplyManaBuffEvent((LivingEntity) entity, type, beneficial);
        MinecraftForge.EVENT_BUS.post(event);
        if(event.isCanceled())
            return false;

        IEntityState state = entity.getCapability(MagickCore.entityState).orElse(null);
        if(state != null)
            return state.applyBuff(getBuff(type).setTick(tick).setForce(force));

        return false;
    }
}
