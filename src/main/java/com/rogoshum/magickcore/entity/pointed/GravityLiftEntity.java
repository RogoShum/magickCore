package com.rogoshum.magickcore.entity.pointed;

import com.rogoshum.magickcore.entity.base.ManaPointEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class GravityLiftEntity extends ManaPointEntity {
    public GravityLiftEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return null;
    }

    @Override
    protected void applyParticle() {

    }
}
