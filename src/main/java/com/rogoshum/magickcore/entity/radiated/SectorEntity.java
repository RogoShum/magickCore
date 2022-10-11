package com.rogoshum.magickcore.entity.radiated;

import com.rogoshum.magickcore.entity.base.ManaRadiateEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class SectorEntity extends ManaRadiateEntity {
    public final Predicate<Entity> inSector = (entity -> {
        Vector3d pos = entity.getPositionVec().add(0, entity.getHeight() / 2, 0);
        return this.getDistanceSq(pos) <= spellContext().range * spellContext().range && rightDirection(pos);
    });
    public SectorEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void applyParticle() {
        applyParticle(2);
    }

    @Override
    public void successFX() {
        applyParticle(20);
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(spellContext().range),
                predicate != null ? predicate.and(inSector)
                        : inSector);
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return null;
    }

    protected void applyParticle(int particleAge) {

    }

    public boolean rightDirection(Vector3d vec) {
        return vec.dotProduct(this.getPositionVec()) < this.spellContext().range * 0.1;
    }
}
