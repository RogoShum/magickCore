package com.rogoshum.magickcore.common.magick.condition;

import com.rogoshum.magickcore.api.IConditionOnlyEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

public abstract class EntityCondition extends Condition<Entity> implements IConditionOnlyEntity {
    @Override
    public boolean suitable(Object object) {
        return object instanceof Entity;
    }
}
