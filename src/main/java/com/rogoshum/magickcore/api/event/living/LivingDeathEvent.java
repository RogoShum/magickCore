package com.rogoshum.magickcore.api.event.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class LivingDeathEvent extends LivingEvent{
    private final DamageSource source;
    public LivingDeathEvent(LivingEntity entity, DamageSource source)
    {
        super(entity);
        this.source = source;
    }

    public DamageSource getSource()
    {
        return source;
    }
}
