package com.rogoshum.magickcore.api.event;

import com.rogoshum.magickcore.api.EnumManaType;
import com.rogoshum.magickcore.api.IManaElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.UUID;

public class EntityEvents {
    public static class HitEntityEvent extends EntityEvent
    {
        private Entity victim;

        public HitEntityEvent(Entity entity, Entity victim)
        {
            super(entity);
            this.setVictim(victim);
        }

        public Entity getVictim() { return victim; }
        public void setVictim(Entity victim) { this.victim = victim; }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class EntityUpdateEvent extends EntityEvent
    {
        public EntityUpdateEvent(Entity entity)
        {
            super(entity);
        }
    }

    public static class MagickPreReleaseEvent extends EntityEvent
    {
        private float mana;
        public MagickPreReleaseEvent(Entity entity, float mana)
        {
            super(entity);
            this.mana = mana;
        }

        public float getMana() { return mana; }
        public void setMana(float mana) { this.mana = mana; }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class MagickReleaseEvent extends EntityEvent
    {
        private IManaElement element;
        private float force;
        private int tick;
        private EnumManaType type;
        private UUID trace;
        private float range;
        private String magickType;
        public MagickReleaseEvent(Entity entity, IManaElement element, float force, int tick, EnumManaType type, UUID trace, float range, String magickType)
        {
            super(entity);
            this.force = force;
            this.element = element;
            this.tick = tick;
            this.trace = trace;
            this.type = type;
            this.range = range;
            this.magickType = magickType;
        }

        public float getForce() { return force; }

        public EnumManaType getType() {
            return type;
        }

        public IManaElement getElement() {
            return element;
        }

        public float getRange() {
            return range;
        }

        public int getTick() {
            return tick;
        }

        public UUID getTrace() {
            return trace;
        }

        public void setForce(float force) { this.force = force; }

        public void setElement(IManaElement element) {
            this.element = element;
        }

        public void setRange(float range) {
            this.range = range;
        }

        public void setTick(int tick) {
            this.tick = tick;
        }

        public void setTrace(UUID trace) {
            this.trace = trace;
        }

        public void setType(EnumManaType type) {
            this.type = type;
        }

        public String getMagickType() {
            return magickType;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class MagickAttackEvent extends LivingAttackEvent
    {
        public MagickAttackEvent(LivingEntity entity, DamageSource source, float amount)
        {
            super(entity, source, amount);
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class ShieldRegenerationEvent extends LivingEvent
    {
        private int amount;
        public ShieldRegenerationEvent(LivingEntity entity, int amount)
        {
            super(entity);
            setAmount(amount);
        }

        public int getAmount() { return amount; }
        public void setAmount(int amount) { this.amount = amount; }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class ManaRegenerationEvent extends LivingEvent
    {
        private float mana;
        public ManaRegenerationEvent(LivingEntity entity, float mana)
        {
            super(entity);
            setMana(mana);
        }

        public float getMana() { return mana; }
        public void setMana(float mana) { this.mana = mana; }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class ApplyManaBuffEvent extends LivingEvent
    {
        private boolean beneficial;
        private String buffType;
        public ApplyManaBuffEvent(LivingEntity entity, String buffType, boolean beneficial)
        {
            super(entity);
            setBeneficial(beneficial);
            setType(buffType);
        }

        public boolean getBeneficial() { return beneficial; }
        public void setBeneficial(boolean beneficial) { this.beneficial = beneficial; }

        public String getType() { return buffType; }
        public void setType(String buffType) { this.buffType = buffType; }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class StateCooldownEvent extends LivingEvent
    {
        private int cooldown;
        private boolean mana;
        private boolean shield;
        public StateCooldownEvent(LivingEntity entity, int cooldown, boolean mana, boolean shield)
        {
            super(entity);
            this.cooldown = cooldown;
            this.mana = mana;
            this.shield = shield;
        }

        public boolean isManaCooldown() { return mana; }
        public boolean isShieldCooldown() { return shield; }

        public void setCooldown(int cooldown) { this.cooldown = cooldown; }
        public int getCooldown() { return cooldown; }
    }

    public static class ShieldCapacityEvent extends LivingEvent
    {
        private int capacity;
        public ShieldCapacityEvent(LivingEntity entity)
        {
            super(entity);
        }

        public void setCapacity(int capacity) { this.capacity = capacity; }
        public int getCapacity() { return capacity; }
    }
}
