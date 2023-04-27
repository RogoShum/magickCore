package com.rogoshum.magickcore.api.magick.condition;

import com.rogoshum.magickcore.api.IConditionOnlyEntity;
import net.minecraft.world.entity.Entity;

public abstract class EntityCondition extends Condition<Entity> implements IConditionOnlyEntity {
    @Override
    public boolean suitable(Object object) {
        return object instanceof Entity;
    }
}
