package com.rogoshum.magickcore.magick.context.child;

import com.rogoshum.magickcore.lib.LibContext;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;

public class SpawnContext extends ChildContext {
    public EntityType<?> entityType = null;

    public static SpawnContext create(EntityType<?> entityType) {
        SpawnContext context = new SpawnContext();
        context.entityType = entityType;
        return context;
    }

    @Override
    public void serialize(CompoundNBT tag) {
        if(entityType != null)
            tag.putString("ENTITY_TYPE", EntityType.getKey(entityType).toString());
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        Optional<EntityType<?>> entityType = EntityType.byKey(tag.getString("ENTITY_TYPE"));
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
        return new TranslationTextComponent(entityType.getTranslationKey()).getString();
    }
}
