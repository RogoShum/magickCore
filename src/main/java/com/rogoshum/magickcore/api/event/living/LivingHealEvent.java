package com.rogoshum.magickcore.api.event.living;

import net.minecraft.world.entity.LivingEntity;

public class LivingHealEvent extends LivingEvent {
    private float amount;
    public LivingHealEvent(LivingEntity entity, float amount)
    {
        super(entity);
        this.setAmount(amount);
    }

    public float getAmount()
    {
        return amount;
    }

    public void setAmount(float amount)
    {
        this.amount = amount;
    }
}
