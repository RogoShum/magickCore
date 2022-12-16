package com.rogoshum.magickcore.common.entity.base;

import com.rogoshum.magickcore.api.IConditionOnlyBlock;
import com.rogoshum.magickcore.api.IConditionOnlyEntity;
import com.rogoshum.magickcore.api.entity.IExistTick;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.init.ModSounds;
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
    boolean cast = false;
    public ManaRadiateEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 0;
    }

    @Override
    public boolean releaseMagick() {
        if(!spellContext().valid()) return false;
        AtomicBoolean entityOnly = new AtomicBoolean(false);
        AtomicBoolean blockOnly = new AtomicBoolean(false);
        AtomicBoolean released = new AtomicBoolean(false);

        ConditionContext condition = null;
        if(spellContext().containChild(LibContext.CONDITION))
            condition = spellContext().getChild(LibContext.CONDITION);
        if(condition != null)
            condition.conditions.forEach(condition1 -> {
                if(condition1 instanceof IConditionOnlyEntity)
                    entityOnly.set(true);
                if(condition1 instanceof IConditionOnlyBlock)
                    blockOnly.set(true);
            });
        List<Entity> livings = new ArrayList<>();
        if(!blockOnly.get())
            livings = findEntity();
        for(Entity living : livings) {
            if(!suitableEntity(living)) continue;
            boolean pass = true;
             if(condition != null) {
                 if(!condition.test(this.getOwner(), living))
                     pass = false;
             }
            if(pass) {
                MagickContext context = MagickContext.create(this.world, spellContext().postContext)
                        .replenishChild(DirectionContext.create(living.getPositionVec().add(0, living.getHeight() * 0.5, 0).subtract(this.getPositionVec())))
                        .<MagickContext>replenishChild(PositionContext.create(this.getPositionVec()))
                        .caster(getOwner()).projectile(this)
                        .victim(living).noCost();
                boolean success = MagickReleaseHelper.releaseMagick(beforeCast(context));
                if(success)
                    released.set(true);
            }
        }

        if(released.get())
            cast = true;


        if(!entityOnly.get() && !released.get() && this.ticksExisted == 1) {
            Iterable<BlockPos> blocks = findBlocks();
            Predicate<BlockPos> posPredicate = blockPosPredicate();
            for (BlockPos pos : blocks) {
                if(!posPredicate.test(pos)) continue;
                if(condition != null && !condition.test(null, world.getBlockState(pos).getBlock()))
                    continue;
                MagickContext context = MagickContext.create(this.world, spellContext().postContext).<MagickContext>applyType(ApplyType.HIT_BLOCK)
                        .replenishChild(DirectionContext.create(this.getPositionVec().subtract(Vector3d.copyCentered(pos))))
                        .<MagickContext>replenishChild(PositionContext.create(Vector3d.copy(pos)))
                        .caster(getOwner()).projectile(this).noCost();
                if (spellContext().postContext != null)
                    context.addChild(ExtraApplyTypeContext.create(spellContext().postContext.applyType));
                boolean success = MagickReleaseHelper.releaseMagick(beforeCast(context));
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

        return released.get();
    }

    @Override
    public void remove() {
        if(!cast) {
            MagickContext context = MagickContext.create(this.world, spellContext().postContext)
                    .<MagickContext>replenishChild(PositionContext.create(this.getPositionVec()))
                    .caster(getOwner()).projectile(this)
                    .victim(this).noCost();
            if(spellContext().containChild(LibContext.DIRECTION)) {
                context.replenishChild(DirectionContext.create(spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction));
            }
            boolean success = MagickReleaseHelper.releaseMagick(beforeCast(context));
            if(success && world.isRemote)
                successFX();
        }
        super.remove();
    }

    @Override
    protected void makeSound() {
        if (this.ticksExisted == 1) {
            this.playSound(ModSounds.soft_sweep.get(), 0.15F, 1.0F + this.rand.nextFloat());
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
