package com.rogoshum.magickcore.common.magick.condition;

import com.rogoshum.magickcore.api.enums.TargetType;
import com.rogoshum.magickcore.common.lib.LibConditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;
import java.util.Optional;

public class EntityTypeCondition extends EntityCondition {
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
    protected void serialize(CompoundTag tag) {
        if(entityType != null)
            tag.putString("ENTITY_TYPE", EntityType.getKey(entityType).toString());
    }

    @Override
    protected void deserialize(CompoundTag tag) {
        Optional<EntityType<?>> entityType = EntityType.byString(tag.getString("ENTITY_TYPE"));
        entityType.ifPresent(type -> this.entityType = type);
    }

    @Override
    public String toString() {
        if(entityType == null)
            return "";
        return new TranslatableComponent(entityType.getDescriptionId()).getString();
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
