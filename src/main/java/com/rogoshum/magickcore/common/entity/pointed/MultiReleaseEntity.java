package com.rogoshum.magickcore.common.entity.pointed;

import com.google.common.collect.Lists;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.magick.context.child.ExtraManaFactorContext;
import com.rogoshum.magickcore.common.magick.context.child.RemoveHurtTimeContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class MultiReleaseEntity extends ManaEntity {
    private static final DataParameter<Integer> TARGET = EntityDataManager.createKey(MultiReleaseEntity.class, DataSerializers.VARINT);
    protected Entity target;
    protected ManaFactor former;
    public MultiReleaseEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        dataManager.register(TARGET, -1);
    }

    @Override
    public void tick() {
        if(world.isRemote) {
            target = world.getEntityByID(dataManager.get(TARGET));
        } else {
            if(target != null)
                dataManager.set(TARGET, target.getEntityId());
            else
                dataManager.set(TARGET, -1);
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
        Vector3d direction = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        } else if (getOwner() != null) {
            direction = getOwner().getLookVec().normalize();
        } else
            direction = getLookVec();

        if(!spellContext().valid()) return false;

        if(spellContext().force >= 1) {
            Vector3d[] vectors = ParticleUtil.drawCone(this.getPositionVec().add(0, getHeight() * 0.5, 0), direction.normalize(), 4.5 * spellContext().range, (int) (spellContext().force + 1));
            for (Vector3d vector : vectors) {
                Vector3d dir = vector.subtract(this.getPositionVec().add(0, getHeight() * 0.5, 0)).normalize();
                MagickContext context = MagickContext.create(((Entity)this).world, spellContext().postContext)
                        .replenishChild(RemoveHurtTimeContext.create())
                        .replenishChild(ExtraManaFactorContext.create(getManaFactor()))
                        .<MagickContext>replenishChild(DirectionContext.create(dir))
                        .caster(getOwner()).projectile((Entity) this)
                        .victim(target).noCost();
                MagickReleaseHelper.releaseMagick(beforeCast(context));
            }
        } else {
            MagickContext context = MagickContext.create(((Entity)this).world, spellContext().postContext)
                    .<MagickContext>replenishChild(DirectionContext.create(direction))
                    .caster(getOwner()).projectile((Entity) this)
                    .victim(target).noCost();
            MagickReleaseHelper.releaseMagick(beforeCast(context));
        }
        remove();
        return true;
    }

    @Override
    public void reSize() {
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return null;
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
