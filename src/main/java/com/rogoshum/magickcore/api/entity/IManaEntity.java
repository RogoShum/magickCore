package com.rogoshum.magickcore.api.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.TargetType;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.network.EntityCompoundTagPack;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public interface IManaEntity extends ISpellContext, IOwnerEntity {
    ResourceLocation orbTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/magick_orb.png");

    @Nonnull
    List<Entity> findEntity(@Nullable Predicate<Entity> predicate);

    @Nonnull
    default List<Entity> findEntity() {
        return findEntity((entity)-> true);
    }

    default void lookAt(Vec3 direction) {
        double d0 = direction.x;
        double d1 = direction.y;
        double d2 = direction.z;
        double d3 = (double) Mth.sqrt((float) (d0 * d0 + d2 * d2));
        Entity entity = (Entity) this;
        entity.setXRot(Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)))));
        entity.setYRot(Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F));
        entity.setYHeadRot(entity.getYRot());
        entity.xRotO = entity.getXRot();
        entity.yRotO = entity.getYRot();
    }

    default boolean releaseMagick() {
        if(!spellContext().valid()) return false;
        ConditionContext condition = null;
        if(spellContext().containChild(LibContext.CONDITION))
            condition = spellContext().getChild(LibContext.CONDITION);
        List<Entity> livings = findEntity();
        boolean released = false;
        for(Entity living : livings) {
            if(living != this && !suitableEntity(living)) continue;
            boolean pass = true;
            if(condition != null) {
                if(!condition.test((Entity) this, living))
                    pass = false;
            }
            if(pass) {
                MagickContext context = MagickContext.create(((Entity)this).level, spellContext().postContext)
                        .<MagickContext>replenishChild(DirectionContext.create(getPostDirection(living)))
                        .caster(getOwner()).projectile((Entity) this)
                        .victim(living).noCost();
                if(MagickReleaseHelper.releaseMagick(beforeCast(context)))
                    released = true;
            }
        }
        return released;
    }

    default boolean suitableEntity(Entity entity) {
        if(!entity.isAlive()) return false;
        ApplyType applyType = spellContext().applyType;
        if(spellContext().postContext != null)
            applyType = spellContext().postContext.applyType;

        boolean sameLikeOwner = MagickReleaseHelper.sameLikeOwner(this.getOwner(), entity);
        if(applyType.getBeneficial() == ApplyType.Beneficial.HARMFUL && sameLikeOwner) {
            return false;
        }

        if(applyType.getBeneficial() == ApplyType.Beneficial.BENEFICIAL && !sameLikeOwner) {
            return false;
        }

        boolean refraction = false;
        if(entity instanceof IManaRefraction)
            refraction = ((IManaRefraction) entity).refraction(spellContext());
        return !refraction;
    }

    default void beforeJoinWorld(MagickContext context) {

    }

    default MagickContext beforeCast(MagickContext context) {
        return context;
    }

    default Vec3 getPostDirection(Entity entity) {
        return entity.position().add(0, entity.getBbHeight() * 0.75, 0).subtract(((Entity) this).position().add(0, ((Entity) this).getBbHeight() * 0.5, 0));
    }

    @OnlyIn(Dist.CLIENT)
    ResourceLocation getEntityIcon();

    @OnlyIn(Dist.CLIENT)
    default void renderFrame(float partialTicks) {}

    ManaFactor getManaFactor();

    default void doNetworkUpdate() {
        EntityCompoundTagPack.updateEntity((Entity) this);
    }
}
