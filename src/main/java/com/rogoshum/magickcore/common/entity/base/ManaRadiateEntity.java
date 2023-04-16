package com.rogoshum.magickcore.common.entity.base;

import com.rogoshum.magickcore.api.IConditionOnlyBlock;
import com.rogoshum.magickcore.api.IConditionOnlyEntity;
import com.rogoshum.magickcore.api.entity.IExistTick;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.magick.context.child.ExtraApplyTypeContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public abstract class ManaRadiateEntity extends ManaEntity implements IExistTick {
    boolean cast = false;
    public ManaRadiateEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected float getEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 0;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
    }

    @Override
    public boolean releaseMagick() {
        if(!spellContext().valid() || level.isClientSide) return false;
        AtomicBoolean entityOnly = new AtomicBoolean(false);
        AtomicBoolean blockOnly = new AtomicBoolean(false);
        boolean released = false;

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
                 if(!condition.test(this.getCaster(), living))
                     pass = false;
             }
            if(pass) {
                MagickContext context = MagickContext.create(this.level, spellContext().postContext)
                        .replenishChild(DirectionContext.create(living.position().add(0, living.getBbHeight() * 0.5, 0).subtract(this.position())))
                        .<MagickContext>replenishChild(PositionContext.create(this.position()))
                        .caster(getCaster()).projectile(this)
                        .victim(living).noCost();
                boolean success = MagickReleaseHelper.releaseMagick(beforeCast(context));
                if(success)
                    released = true;
            }
        }

        if(released)
            cast = true;


        if(!entityOnly.get() && !released && this.tickCount == 1) {
            Iterable<BlockPos> blocks = findBlocks();
            Predicate<BlockPos> posPredicate = blockPosPredicate();
            for (BlockPos pos : blocks) {
                if(!posPredicate.test(pos)) continue;
                if(condition != null && !condition.test(null, level.getBlockState(pos).getBlock()))
                    continue;
                MagickContext context = MagickContext.create(this.level, spellContext().postContext).doBlock()
                        .replenishChild(DirectionContext.create(this.position().subtract(Vec3.atCenterOf(pos))))
                        .<MagickContext>replenishChild(PositionContext.create(Vec3.atLowerCornerOf(pos)))
                        .caster(getCaster()).projectile(this).noCost();
                if (spellContext().postContext != null)
                    context.addChild(ExtraApplyTypeContext.create(spellContext().postContext.applyType));
                boolean success = MagickReleaseHelper.releaseMagick(beforeCast(context));
                if(success)
                    released = true;
            }
        }

        if(released) {
            if(!level.isClientSide)
                level.broadcastEntityEvent(this, (byte)14);
            else
                successFX();
            this.remove(RemovalReason.DISCARDED);
        }

        return released;
    }

    @Override
    public void remove(RemovalReason reason) {
        if(!cast) {
            /*
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
             */
        }
        super.remove(reason);
    }

    @Override
    protected void makeSound() {
        if (this.tickCount == 1) {
            this.playSound(ModSounds.soft_sweep.get(), 0.15F, 1.0F + this.random.nextFloat());
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
    public void handleEntityEvent(byte id) {
        if(id == 14)
            successFX();
        else
            super.handleEntityEvent(id);
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
