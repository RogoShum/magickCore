package com.rogoshum.magickcore.entity.base;

import com.rogoshum.magickcore.api.entity.IExistTick;
import com.rogoshum.magickcore.enums.TargetType;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.magick.context.child.PositionContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ManaRadiateEntity extends ManaEntity implements IExistTick {
    public ManaRadiateEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 0;
    }

    @Override
    public void releaseMagick() {
        if(!spellContext().valid()) return;

        AtomicBoolean released = new AtomicBoolean(false);
        List<Entity> livings = findEntity();
        for(Entity living : livings) {
            if(!suitableEntity(living)) continue;
            AtomicReference<Boolean> pass = new AtomicReference<>(true);
            if(spellContext().containChild(LibContext.CONDITION)) {
                ConditionContext context = spellContext().getChild(LibContext.CONDITION);
                context.conditions.forEach((condition -> {
                    if(condition.getType() == TargetType.TARGET) {
                        if(!condition.test(living))
                            pass.set(false);
                    } else if(!condition.test(this.getOwner()))
                        pass.set(false);
                }));
            }
            if(pass.get()) {
                MagickContext context = MagickContext.create(this.world, spellContext().postContext)
                        .replenishChild(DirectionContext.create(this.getPositionVec().subtract(living.getPositionVec())))
                        .<MagickContext>replenishChild(PositionContext.create(this.getPositionVec()))
                        .caster(getOwner()).projectile(this)
                        .victim(living);
                boolean success = MagickReleaseHelper.releaseMagick(context);
                if(success)
                    released.set(true);
            }
        }

        if(released.get()) {
            successFX();
            this.remove();
        }
    }

    @Override
    public void reSize() {

    }

    abstract public void successFX();

    @Override
    public int getTickThatNeedExistingBeforeRemove() {
        return 1;
    }
}
