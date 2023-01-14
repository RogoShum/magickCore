package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public class SpawnContext extends ChildContext {
    public EntityType<?> entityType = null;

    @Override
    public ApplyType getLinkType() {
        return ApplyType.SPAWN_ENTITY;
    }

    public static SpawnContext create(EntityType<?> entityType) {
        SpawnContext context = new SpawnContext();
        context.entityType = entityType;
        return context;
    }

    @Override
    public void serialize(CompoundTag tag) {
        if(entityType != null)
            tag.putString("ENTITY_TYPE", EntityType.getKey(entityType).toString());
    }

    @Override
    public void deserialize(CompoundTag tag) {
        Optional<EntityType<?>> entityType = EntityType.byString(tag.getString("ENTITY_TYPE"));
        entityType.ifPresent(type -> this.entityType = type);
    }

    @Override
    public boolean valid() {
        return entityType != null;
    }

    @Override
    public String getName() {
        return LibContext.SPAWN;
    }

    @Override
    public String getString(int tab) {
        return new TranslatableComponent(entityType.getDescriptionId()).getString();
    }
}
