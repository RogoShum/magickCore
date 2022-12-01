package com.rogoshum.magickcore.common.extradata.entity;

import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.api.enums.ManaLimit;
import com.rogoshum.magickcore.common.extradata.EntityExtraData;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Iterator;

public class EntityStateData extends EntityExtraData {
    public static final String MANA_VALUE = "MANA_VALUE";
    public static final String SHIELD_VALUE = "SHIELD_VALUE";
    public static final String MAX_MANA_VALUE = "MAX_MANA_VALUE";
    public static final String MAX_SHIELD_VALUE = "MAX_SHIELD_VALUE";
    public static final String FINAL_MAX_SHIELD_VALUE = "FINAL_MAX_SHIELD_VALUE";
    public static final String ELEMENT = "ELEMENT";
    public static final String EFFECT_TICK = "EFFECT_TICK";
    public static final String EFFECT_FORCE = "EFFECT_FORCE";
    public static final String ALLOW_ELEMENT = "ALLOW_ELEMENT";
    public static final String UPDATE_RATE = "UPDATE_RATE";
    public static final String UPDATE_TICK = "UPDATE_TICK";

    private float maxManaValue;
    private float maxShieldValue;
    private float finalMaxShieldValue;
    private float manaValue;
    private float shieldValue;
    private MagickElement element = ModElements.ORIGIN;
    private final HashMap<String, ManaBuff> buffList = new HashMap<>();

    private int shieldCoolDown;
    private int manaCoolDown;
    private boolean allowElement;

    private int updateRate = 1;
    private int updateTick = 0;

    public void setElemented() {
        allowElement = true;
    }

    public void setUpdateRate(int rate) {
        if(rate < 1)
            rate = 1;
        this.updateRate = rate;
    }

    public boolean allowElement() {
        return !allowElement;
    }

    public MagickElement getElement() {
        return this.element;
    }

    public void setElement(MagickElement element) {
        this.element = element;
    }

    public float getManaValue() {
        return this.manaValue;
    }

    public void setManaValue(float mana) {
        this.manaValue = Math.min(Math.max(0, mana), this.getMaxManaValue());
    }

    public void hitElementShield() { shieldCoolDown = 120; }

    public int getElementCoolDown() { return shieldCoolDown; }

    public void releaseMagick() { manaCoolDown = 30; }

    public int getManaCoolDown() { return manaCoolDown; }

    public float getElementShieldMana() {
        return this.shieldValue;
    }

    public void setElementShieldMana(float mana) {
        this.shieldValue = mana;
    }

    public float getMaxManaValue() {
        return maxManaValue;
    }

    public void setMaxManaValue(float mana) {
        this.maxManaValue = ManaLimit.MAX_MANA.limit(mana);
    }

    public float getFinalMaxElementShield() {
        return finalMaxShieldValue;
    }

    public void setFinalMaxElementShield(float mana) {
        finalMaxShieldValue = mana;
    }

    public float getMaxElementShieldMana() {
        return maxShieldValue;
    }

    public void setMaxElementShieldMana(float mana) {
        this.maxShieldValue = mana;
    }

    public boolean applyBuff(ManaBuff buff) {
        if(buff == null) return false;
        if(!buffList.containsKey(buff.getType())) {
            buffList.put(buff.getType(), buff);
            return true;
        }
        else if(buff.canRefreshBuff()) {
            buffList.put(buff.getType(), buff);
            return true;
        }
        return false;
    }

    public boolean setBuff(String type, int tick, int force) {
        if(buffList.containsKey(type)) {
            buffList.get(type).setTick(tick).setForce(force);
            return true;
        }
        return false;
    }

    public HashMap<String, ManaBuff> getBuffList() {
        return buffList;
    }

    public void removeBuff(String type) {
        buffList.remove(type);
    }

    public void tick(Entity entity) {
        updateTick++;
        if(updateTick % updateRate == 0) {
            updateTick = 0;
        } else
            return;
        EntityEvents.StateCooldownEvent cEvent = new EntityEvents.StateCooldownEvent((LivingEntity) entity, shieldCoolDown, false, true);
        MinecraftForge.EVENT_BUS.post(cEvent);
        if(shieldCoolDown > cEvent.getCooldown())
            shieldCoolDown = cEvent.getCooldown();

        EntityEvents.StateCooldownEvent cEvent_m = new EntityEvents.StateCooldownEvent((LivingEntity) entity, manaCoolDown, true, false);
        MinecraftForge.EVENT_BUS.post(cEvent_m);
        if(manaCoolDown > cEvent_m.getCooldown())
            manaCoolDown = cEvent_m.getCooldown();

        if(shieldCoolDown > 0)
            shieldCoolDown--;


        if(getElementShieldMana() < getMaxElementShieldMana() && shieldCoolDown <= 0) {
            float shieldRegen = updateRate;
            EntityEvents.ShieldRegenerationEvent event = new EntityEvents.ShieldRegenerationEvent((LivingEntity) entity, shieldRegen);
            MinecraftForge.EVENT_BUS.post(event);
            shieldRegen = event.getAmount();
            if(shieldRegen > 0 && this.getManaValue() > 5) {
                this.setElementShieldMana(Math.min(this.getMaxElementShieldMana(), this.getElementShieldMana() + shieldRegen));
                this.setManaValue(this.getManaValue() - 5);
            }
        } else
            this.setElementShieldMana(Math.min(this.getMaxElementShieldMana(), this.getElementShieldMana()));

        EntityEvents.ShieldCapacityEvent shield_value = new EntityEvents.ShieldCapacityEvent((LivingEntity) entity);
        MinecraftForge.EVENT_BUS.post(shield_value);
        this.setMaxElementShieldMana(shield_value.getCapacity() + finalMaxShieldValue);

        if(manaCoolDown > 0)
            manaCoolDown--;

        if(manaCoolDown <= 0 && this.getManaValue() < this.getMaxManaValue()) {
            float manaRegen = updateRate;
            EntityEvents.ManaRegenerationEvent eventMana = new EntityEvents.ManaRegenerationEvent((LivingEntity) entity, manaRegen);
            MinecraftForge.EVENT_BUS.post(eventMana);
            manaRegen = eventMana.getMana();

            this.setManaValue(this.getManaValue() + manaRegen);
        }

        Iterator<String> i = buffList.keySet().iterator();

        while(i.hasNext()) {
            ManaBuff buff = buffList.get(i.next());
            buff.setTick(buff.getTick() - updateRate).effectEntity(entity);
            if(buff.getTick() < 1) {
                i.remove();
                if(buff.getType().equals(LibBuff.FREEZE))
                    entity.setSilent(false);
            }
        }
    }

    public boolean isEntitySuitable(Entity entity) {
        boolean suitable = entity instanceof LivingEntity;
        if(!(entity instanceof PlayerEntity))
            setUpdateRate(5);
        return suitable;
    }

    public void read(CompoundNBT nbt) {
        this.setMaxManaValue(nbt.getFloat(MAX_MANA_VALUE));
        this.setMaxElementShieldMana(nbt.getFloat(MAX_SHIELD_VALUE));
        this.setManaValue(nbt.getFloat(MANA_VALUE));
        this.setElementShieldMana(nbt.getFloat(SHIELD_VALUE));
        this.setElement(MagickRegistry.getElement(nbt.getString(ELEMENT)));
        if(nbt.getBoolean(ALLOW_ELEMENT))
            this.setElemented();
        CompoundNBT effectTick = nbt.getCompound(EFFECT_TICK);
        CompoundNBT effectForce = nbt.getCompound(EFFECT_FORCE);
        this.setFinalMaxElementShield(nbt.getFloat(FINAL_MAX_SHIELD_VALUE));
        for (String type : effectTick.keySet()) {
            if (effectForce.contains(type)) {
                this.applyBuff(ModBuffs.getBuff(type).setTick(effectTick.getInt(type)).setForce(effectForce.getFloat(type)));
            }
        }
        this.setUpdateRate(nbt.getInt(UPDATE_RATE));
        this.updateTick = nbt.getInt(UPDATE_TICK);
    }

    public void write(CompoundNBT nbt) {
        nbt.putFloat(MANA_VALUE, this.getManaValue());
        nbt.putFloat(SHIELD_VALUE, this.getElementShieldMana());
        nbt.putFloat(MAX_MANA_VALUE, this.getMaxManaValue());
        nbt.putFloat(MAX_SHIELD_VALUE, this.getMaxElementShieldMana());
        nbt.putString(ELEMENT, this.getElement().type());
        nbt.putBoolean(ALLOW_ELEMENT, !this.allowElement());
        nbt.putFloat(FINAL_MAX_SHIELD_VALUE, this.getFinalMaxElementShield());
        CompoundNBT effectTick = new CompoundNBT();
        for (String value : this.getBuffList().keySet()) {
            ManaBuff buff = this.getBuffList().get(value);
            effectTick.putInt(buff.getType(), buff.getTick());
        }
        nbt.put(EFFECT_TICK, effectTick);

        CompoundNBT effectForce = new CompoundNBT();
        for (String s : this.getBuffList().keySet()) {
            ManaBuff buff = this.getBuffList().get(s);
            effectForce.putFloat(buff.getType(), buff.getForce());
        }
        nbt.put(EFFECT_FORCE, effectForce);
        nbt.putInt(UPDATE_TICK, updateTick);
        nbt.putInt(UPDATE_RATE, updateRate);
    }
}
