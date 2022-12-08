package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class SpinEntity extends ManaPointEntity {
    public SpinEntity(EntityType<?> entityTypeIn, World worldIn) {
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
    public ManaFactor getManaFactor() {
        return null;
    }

    @Override
    protected void applyParticle() {

    }
}
