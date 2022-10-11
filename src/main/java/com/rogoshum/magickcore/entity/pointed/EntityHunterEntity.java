package com.rogoshum.magickcore.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.enums.EnumTargetType;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.magick.context.child.DirectionContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class EntityHunterEntity extends ManaPointEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/entity_capture.png");
    public EntityHunterEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public void releaseMagick() {

    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    protected void applyParticle() {

    }
}
