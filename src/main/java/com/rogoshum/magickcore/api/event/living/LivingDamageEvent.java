package com.rogoshum.magickcore.api.event.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class LivingDamageEvent extends LivingEvent{
    private final DamageSource source;
    private float amount;
    public LivingDamageEvent(LivingEntity entity, DamageSource source, float amount)
    {
        super(entity);
        this.source = source;
        this.amount = amount;
    }

    public DamageSource getSource() { return source; }

    public float getAmount() { return amount; }

    public void setAmount(float amount) { this.amount = amount; }
}
