package com.rogoshum.magickcore.common.extradata.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.EntityEvent;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.api.enums.ManaLimit;
import com.rogoshum.magickcore.common.extradata.EntityExtraData;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;

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
    public static final String PERSIST_DATA = "persist_data";

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

    private CompoundTag preState = new CompoundTag();

    public CompoundTag getState() {
        return state;
    }

    private CompoundTag state = new CompoundTag();
    private CompoundTag persistData = new CompoundTag();

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
        EntityEvent.StateCooldownEvent cEvent = new EntityEvent.StateCooldownEvent((LivingEntity) entity, shieldCoolDown, false, true);
        MagickCore.EVENT_BUS.post(cEvent);
        if(shieldCoolDown > cEvent.getCooldown())
            shieldCoolDown = cEvent.getCooldown();

        EntityEvent.StateCooldownEvent cEvent_m = new EntityEvent.StateCooldownEvent((LivingEntity) entity, manaCoolDown, true, false);
        MagickCore.EVENT_BUS.post(cEvent_m);
        if(manaCoolDown > cEvent_m.getCooldown())
            manaCoolDown = cEvent_m.getCooldown();

        if(shieldCoolDown > 0)
            shieldCoolDown--;


        if(getElementShieldMana() < getMaxElementShieldMana() && shieldCoolDown <= 0) {
            float shieldRegen = updateRate;
            EntityEvent.ShieldRegenerationEvent event = new EntityEvent.ShieldRegenerationEvent((LivingEntity) entity, shieldRegen);
            MagickCore.EVENT_BUS.post(event);
            shieldRegen = event.getAmount();
            if(shieldRegen > 0 && this.getManaValue() > 5) {
                this.setElementShieldMana(Math.min(this.getMaxElementShieldMana(), this.getElementShieldMana() + shieldRegen));
                this.setManaValue(this.getManaValue() - 5);
            }
        } else
            this.setElementShieldMana(Math.min(this.getMaxElementShieldMana(), this.getElementShieldMana()));

        EntityEvent.ShieldCapacityEvent shield_value = new EntityEvent.ShieldCapacityEvent((LivingEntity) entity);
        MagickCore.EVENT_BUS.post(shield_value);
        this.setMaxElementShieldMana(shield_value.getCapacity() + finalMaxShieldValue);

        if(manaCoolDown > 0)
            manaCoolDown--;

        if(manaCoolDown <= 0 && this.getManaValue() < this.getMaxManaValue()) {
            float manaRegen = updateRate;
            EntityEvent.ManaRegenerationEvent eventMana = new EntityEvent.ManaRegenerationEvent((LivingEntity) entity, manaRegen);
            MagickCore.EVENT_BUS.post(eventMana);
            manaRegen = eventMana.getMana();

            this.setManaValue(this.getManaValue() + manaRegen);
        }

        Iterator<String> i = buffList.keySet().iterator();

        while(i.hasNext()) {
            ManaBuff buff = buffList.get(i.next());
            buff.setTick(buff.getTick() - updateRate).effectEntity(entity, buff.getForce());
            if(buff.getTick() < 1) {
                i.remove();
                if(buff.getType().equals(LibBuff.FREEZE))
                    entity.setSilent(false);
            }
        }
        CompoundTag state = new CompoundTag();
        write(state);
        this.preState = this.state;
        this.state = state;
    }

    public boolean isEntitySuitable(Entity entity) {
        boolean suitable = entity instanceof LivingEntity;
        if(!(entity instanceof Player))
            setUpdateRate(5);
        return suitable;
    }

    public void read(CompoundTag nbt) {
        this.setMaxManaValue(nbt.getFloat(MAX_MANA_VALUE));
        this.setMaxElementShieldMana(nbt.getFloat(MAX_SHIELD_VALUE));
        this.setManaValue(nbt.getFloat(MANA_VALUE));
        this.setElementShieldMana(nbt.getFloat(SHIELD_VALUE));
        this.setElement(MagickRegistry.getElement(nbt.getString(ELEMENT)));
        if(nbt.getBoolean(ALLOW_ELEMENT))
            this.setElemented();
        CompoundTag effectTick = nbt.getCompound(EFFECT_TICK);
        CompoundTag effectForce = nbt.getCompound(EFFECT_FORCE);
        this.setFinalMaxElementShield(nbt.getFloat(FINAL_MAX_SHIELD_VALUE));
        for (String type : effectTick.getAllKeys()) {
            if (effectForce.contains(type)) {
                this.applyBuff(ModBuffs.getBuff(type).setTick(effectTick.getInt(type)).setForce(effectForce.getFloat(type)));
            }
        }
        this.setUpdateRate(nbt.getInt(UPDATE_RATE));
        this.updateTick = nbt.getInt(UPDATE_TICK);
        this.persistData = nbt.getCompound(PERSIST_DATA);
    }

    public void write(CompoundTag nbt) {
        nbt.putFloat(MANA_VALUE, this.getManaValue());
        nbt.putFloat(SHIELD_VALUE, this.getElementShieldMana());
        nbt.putFloat(MAX_MANA_VALUE, this.getMaxManaValue());
        nbt.putFloat(MAX_SHIELD_VALUE, this.getMaxElementShieldMana());
        nbt.putString(ELEMENT, this.getElement().type());
        nbt.putBoolean(ALLOW_ELEMENT, !this.allowElement());
        nbt.putFloat(FINAL_MAX_SHIELD_VALUE, this.getFinalMaxElementShield());
        CompoundTag effectTick = new CompoundTag();
        for (String value : this.getBuffList().keySet()) {
            ManaBuff buff = this.getBuffList().get(value);
            effectTick.putInt(buff.getType(), buff.getTick());
        }
        nbt.put(EFFECT_TICK, effectTick);

        CompoundTag effectForce = new CompoundTag();
        for (String s : this.getBuffList().keySet()) {
            ManaBuff buff = this.getBuffList().get(s);
            effectForce.putFloat(buff.getType(), buff.getForce());
        }
        nbt.put(EFFECT_FORCE, effectForce);
        nbt.putInt(UPDATE_TICK, updateTick);
        nbt.putInt(UPDATE_RATE, updateRate);
        nbt.put(PERSIST_DATA, persistData);
    }

    public CompoundTag getPreState() {
        return preState;
    }

    public CompoundTag getPersistData() {
        return persistData;
    }
}
