package com.rogoshum.magickcore.api.event;

import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

public class EntityEvents {
    public static class HitEntityEvent extends EntityEvent {
        private Entity victim;

        public HitEntityEvent(Entity entity, Entity victim) {
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

    public static class EntityUpdateEvent extends EntityEvent {
        public EntityUpdateEvent(Entity entity)
        {
            super(entity);
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class EntityAddedToWorldEvent extends EntityEvent {
        public EntityAddedToWorldEvent(Entity entity) {
            super(entity);
        }
    }

    public static class MagickSpawnEntityEvent extends EntityEvent {
        private final MagickContext context;
        public MagickSpawnEntityEvent(MagickContext context, Entity entity) {
            super(entity);
            this.context = context;
        }

        public MagickContext getMagickContext() {
            return context;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class MagickReleaseCheckEvent extends EntityEvent {
        private float mana;
        private MagickContext context;
        public MagickReleaseCheckEvent(MagickContext context, float mana) {
            super(context.caster);
            this.mana = mana;
            this.context = context;
        }

        public float getMana() { return mana; }
        public void setMana(float mana) { this.mana = mana; }
        public MagickContext getContext() { return context; }
        public void setContext(MagickContext context) { this.context = context; }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class MagickPreReleaseEvent extends EntityEvent {
        private MagickContext context;
        public MagickPreReleaseEvent(MagickContext context) {
            super(context.caster);
            this.context = context;
        }

        public MagickContext getContext() { return context; }

        public void setContext(MagickContext context) {
             this.context = context;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class MagickAttackEvent extends LivingAttackEvent {
        public MagickAttackEvent(LivingEntity entity, DamageSource source, float amount) {
            super(entity, source, amount);
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class ShieldRegenerationEvent extends LivingEvent {
        private float amount;
        public ShieldRegenerationEvent(LivingEntity entity, float amount) {
            super(entity);
            setAmount(amount);
        }

        public float getAmount() { return amount; }
        public void setAmount(float amount) { this.amount = amount; }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class ManaRegenerationEvent extends LivingEvent {
        private float mana;
        public ManaRegenerationEvent(LivingEntity entity, float mana) {
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

    public static class ApplyManaBuffEvent extends LivingEvent {
        private boolean beneficial;
        private ManaBuff buffType;
        public ApplyManaBuffEvent(LivingEntity entity, ManaBuff buffType, boolean beneficial) {
            super(entity);
            setBeneficial(beneficial);
            setType(buffType);
        }

        public boolean getBeneficial() { return beneficial; }
        public void setBeneficial(boolean beneficial) { this.beneficial = beneficial; }

        public ManaBuff getType() { return buffType; }
        public void setType(ManaBuff buffType) { this.buffType = buffType; }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class StateCooldownEvent extends LivingEvent {
        private int cooldown;
        private boolean mana;
        private boolean shield;
        public StateCooldownEvent(LivingEntity entity, int cooldown, boolean mana, boolean shield) {
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

    public static class ShieldCapacityEvent extends LivingEvent {
        private float capacity;
        public ShieldCapacityEvent(LivingEntity entity) {
            super(entity);
        }

        public void setCapacity(float capacity) { this.capacity = capacity; }
        public float getCapacity() { return capacity; }
    }

    public static class EntityVelocity extends EntityEvent {
        private float velocity = 1.0f;
        private float inaccuracy = 10f;
        public EntityVelocity(Entity entity) {
            super(entity);
        }

        public void setVelocity(float velocity) {
            this.velocity = velocity;
        }

        public float getVelocity() {
            return velocity;
        }

        public float getInaccuracy() {
            return inaccuracy;
        }

        public void setInaccuracy(float inaccuracy) {
            this.inaccuracy = inaccuracy;
        }
    }
}
