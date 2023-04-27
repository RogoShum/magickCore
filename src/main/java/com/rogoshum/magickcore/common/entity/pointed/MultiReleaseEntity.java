package com.rogoshum.magickcore.common.entity.pointed;

import com.google.common.collect.Lists;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.api.magick.context.child.ExtraManaFactorContext;
import com.rogoshum.magickcore.api.magick.context.child.RemoveHurtTimeContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class MultiReleaseEntity extends ManaEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/multi_release.png");
    private static final EntityDataAccessor<Integer> TARGET = SynchedEntityData.defineId(MultiReleaseEntity.class, EntityDataSerializers.INT);
    protected Entity target;
    protected ManaFactor former;
    public MultiReleaseEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        entityData.define(TARGET, -1);
    }

    @Override
    public void tick() {
        if(level.isClientSide) {
            target = level.getEntity(entityData.get(TARGET));
        } else {
            if(target != null)
                entityData.set(TARGET, target.getId());
            else
                entityData.set(TARGET, -1);
        }
        super.tick();
    }

    @Override
    public void beforeJoinWorld(MagickContext context) {
        target = context.victim;
        if(context.projectile instanceof IManaEntity) {
            former = ((IManaEntity) context.projectile).getManaFactor();
        }
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        if(target == null)
            return Lists.newArrayList(this);
        return Lists.newArrayList(target);
    }

    @Override
    public boolean releaseMagick() {
        Vec3 direction = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        } else if (getCaster() != null) {
            direction = getCaster().getLookAngle().normalize();
        } else
            direction = getLookAngle();

        if(!spellContext().valid()) return false;

        if(spellContext().force >= 1) {
            Vec3[] vectors = ParticleUtil.drawCone(this.position().add(0, getBbHeight() * 0.5, 0), direction.normalize(), 4.5 * spellContext().range, (int) (spellContext().force + 1));
            for (Vec3 vector : vectors) {
                Vec3 dir = vector.subtract(this.position().add(0, getBbHeight() * 0.5, 0)).normalize();
                MagickContext context = MagickContext.create(((Entity)this).level, spellContext().postContext)
                        .replenishChild(RemoveHurtTimeContext.create())
                        .replenishChild(ExtraManaFactorContext.create(getManaFactor()))
                        .<MagickContext>replenishChild(DirectionContext.create(dir))
                        .caster(getCaster()).projectile((Entity) this)
                        .victim(target).noCost();
                MagickReleaseHelper.releaseMagick(beforeCast(context));
            }
        } else {
            MagickContext context = MagickContext.create(((Entity)this).level, spellContext().postContext)
                    .<MagickContext>replenishChild(DirectionContext.create(direction))
                    .caster(getCaster()).projectile((Entity) this)
                    .victim(target).noCost();
            MagickReleaseHelper.releaseMagick(beforeCast(context));
        }
        remove(RemovalReason.DISCARDED);
        ParticleUtil.spawnImpactParticle(level, this.position(), 1, direction.normalize().scale(0.2), spellContext().element, ParticleType.PARTICLE);
        return true;
    }

    @Override
    public void reSize() {
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        float force = 1 / (spellContext().force <= 1 ? 2 : spellContext().force + 1);
        if(force < 1)
            force += 0.1f;
        return ManaFactor.create(force, force, force);
    }

    @Override
    public MagickContext beforeCast(MagickContext context) {
        return super.beforeCast(context);
    }

    @Override
    protected void applyParticle() {

    }
}
