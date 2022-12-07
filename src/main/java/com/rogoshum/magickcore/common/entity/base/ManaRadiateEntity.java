package com.rogoshum.magickcore.common.entity.base;

import com.rogoshum.magickcore.api.entity.IExistTick;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.enums.TargetType;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.magick.context.child.ExtraApplyTypeContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

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
                        .replenishChild(DirectionContext.create(living.getPositionVec().add(0, living.getHeight() * 0.5, 0).subtract(this.getPositionVec())))
                        .<MagickContext>replenishChild(PositionContext.create(this.getPositionVec()))
                        .caster(getOwner()).projectile(this)
                        .victim(living).noCost();
                boolean success = MagickReleaseHelper.releaseMagick(context);
                if(success)
                    released.set(true);
            }
        }

        if(!released.get() && this.ticksExisted == 1) {
            Iterable<BlockPos> blocks = findBlocks();
            Predicate<BlockPos> posPredicate = blockPosPredicate();
            for (BlockPos pos : blocks) {
                if(!posPredicate.test(pos)) continue;
                MagickContext context = MagickContext.create(this.world, spellContext().postContext).<MagickContext>applyType(ApplyType.HIT_BLOCK)
                        .replenishChild(DirectionContext.create(this.getPositionVec().subtract(Vector3d.copyCentered(pos))))
                        .<MagickContext>replenishChild(PositionContext.create(Vector3d.copy(pos)))
                        .caster(getOwner()).projectile(this).noCost();
                if (spellContext().postContext != null)
                    context.addChild(ExtraApplyTypeContext.create(spellContext().postContext.applyType));
                boolean success = MagickReleaseHelper.releaseMagick(context);
                if(success)
                    released.set(true);
            }
        }

        if(released.get()) {
            if(!world.isRemote)
                world.setEntityState(this, (byte)14);
            else
                successFX();
            this.remove();
        }
    }

    public Iterable<BlockPos> findBlocks() {
        return Collections.emptyList();
    }

    @Override
    public void reSize() {

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleStatusUpdate(byte id) {
        if(id == 14)
            successFX();
        else
            super.handleStatusUpdate(id);
    }

    abstract public void successFX();

    @Override
    public int getTickThatNeedExistingBeforeRemove() {
        return 1;
    }

    public Predicate<BlockPos> blockPosPredicate() {
        return (pos -> true);
    }
}
