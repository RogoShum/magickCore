package com.rogoshum.magickcore.magick.extradata.entity;

import com.rogoshum.magickcore.api.entity.IManaMob;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.buff.ManaBuff;
import com.rogoshum.magickcore.enums.EnumManaLimit;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.registry.MagickRegistry;
import com.rogoshum.magickcore.magick.extradata.EntityExtraData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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

    private float maxManaValue;
    private float maxShieldValue;
    private float finalMaxShieldValue;
    private float manaValue;
    private float shieldValue;
    private MagickElement element = MagickRegistry.getElement(LibElements.ORIGIN);
    private HashMap<String, ManaBuff> buffList = new HashMap<>();

    private int shieldCooldown;
    private int manaCooldown;
    private boolean IsDeprived;
    private boolean allowElement;

    public void setElemented() {
        allowElement = true;
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

    public void hitElementShield() { shieldCooldown = 120; }

    public int getElementCooldown() { return shieldCooldown; }

    public void releaseMagick() { manaCooldown = 30; }

    public int getManaCooldown() { return manaCooldown; }

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
        this.maxManaValue = EnumManaLimit.MAX_MANA.limit(mana);
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
        if(buffList.containsKey(type))
            buffList.remove(type);
    }

    public void tick(Entity entity) {
        EntityEvents.StateCooldownEvent cEvent = new EntityEvents.StateCooldownEvent((LivingEntity) entity, shieldCooldown, false, true);
        MinecraftForge.EVENT_BUS.post(cEvent);
        if(shieldCooldown > cEvent.getCooldown())
            shieldCooldown = cEvent.getCooldown();

        EntityEvents.StateCooldownEvent cEvent_m = new EntityEvents.StateCooldownEvent((LivingEntity) entity, manaCooldown, true, false);
        MinecraftForge.EVENT_BUS.post(cEvent_m);
        if(manaCooldown > cEvent_m.getCooldown())
            manaCooldown = cEvent_m.getCooldown();

        EntityEvents.ShieldCapacityEvent shield_value = new EntityEvents.ShieldCapacityEvent((LivingEntity) entity);
        MinecraftForge.EVENT_BUS.post(shield_value);
        this.setMaxElementShieldMana(shield_value.getCapacity() + finalMaxShieldValue);

        if(shieldCooldown > 0)
            shieldCooldown--;

        int shieldRegen = 1;
        EntityEvents.ShieldRegenerationEvent event = new EntityEvents.ShieldRegenerationEvent((LivingEntity) entity, shieldRegen);
        MinecraftForge.EVENT_BUS.post(event);
        shieldRegen = event.getAmount();

        if(this.getElementShieldMana() < this.getMaxElementShieldMana() && shieldCooldown <= 0 && shieldRegen > 0 && this.getManaValue() > 1) {
            this.setElementShieldMana(Math.min(this.getMaxElementShieldMana(), this.getElementShieldMana() + shieldRegen));
            this.setManaValue(this.getManaValue() - 1);
        }

        if(manaCooldown > 0)
            manaCooldown--;

        float manaRegen = 1;
        EntityEvents.ManaRegenerationEvent eventMana = new EntityEvents.ManaRegenerationEvent((LivingEntity) entity, manaRegen);
        MinecraftForge.EVENT_BUS.post(eventMana);
        manaRegen = eventMana.getMana();

        if(manaCooldown <= 0)
            this.setManaValue(Math.min(this.getMaxManaValue(), this.getManaValue() + manaRegen));

        Iterator<String> i = buffList.keySet().iterator();

        while(i.hasNext()) {
            ManaBuff buff = buffList.get(i.next());
            buff.setTick(buff.getTick() - 1).effectEntity(entity);
            if(buff.getTick() < 1) {
                i.remove();
                if(i.equals(LibBuff.FREEZE))
                    event.getEntityLiving().setSilent(false);
            }
        }
    }

    public boolean getIsDeprived() {
        return IsDeprived;
    }

    public void setDeprived() {
        IsDeprived = true;
    }

    public boolean isEntitySuitable(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof IManaMob;
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
                this.applyBuff(ModBuff.getBuff(type).setTick(effectTick.getInt(type)).setForce(effectForce.getFloat(type)));
            }
        }
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
    }
}
