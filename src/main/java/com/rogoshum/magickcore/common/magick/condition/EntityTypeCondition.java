package com.rogoshum.magickcore.common.magick.condition;

import com.rogoshum.magickcore.api.enums.TargetType;
import com.rogoshum.magickcore.common.lib.LibConditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;

import java.util.Objects;
import java.util.Optional;

public class EntityTypeCondition extends Condition{
    private EntityType<? extends Entity> entityType;

    public void setEntityType(EntityType<? extends Entity> entityType) {
        this.entityType = entityType;
    }

    @Override
    public String getName() {
        return LibConditions.ENTITY_TYPE;
    }

    @Override
    public TargetType getType() {
        return TargetType.TARGET;
    }

    @Override
    public boolean test(Entity entity) {
        return entity.getType().equals(entityType);
    }

    @Override
    protected void serialize(CompoundNBT tag) {
        if(entityType != null)
            tag.putString("ENTITY_TYPE", EntityType.getKey(entityType).toString());
    }

    @Override
    protected void deserialize(CompoundNBT tag) {
        Optional<EntityType<?>> entityType = EntityType.byKey(tag.getString("ENTITY_TYPE"));
        entityType.ifPresent(type -> this.entityType = type);
    }

    @Override
    public String toString() {
        return "EntityTypeCondition{" +
                "entityType=" + entityType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityTypeCondition that = (EntityTypeCondition) o;
        return Objects.equals(entityType, that.entityType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityType);
    }
}
