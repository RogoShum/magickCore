package com.rogoshum.magickcore.api.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.enums.EnumTargetType;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.magick.context.child.DirectionContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
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
        return findEntity(null);
    }

    default void lookAt(Vector3d direction) {
        double d0 = direction.x;
        double d1 = direction.y;
        double d2 = direction.z;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
        Entity entity = (Entity) this;
        entity.rotationPitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI))));
        entity.rotationYaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
        entity.setRotationYawHead(entity.rotationYaw);
        entity.prevRotationPitch = entity.rotationPitch;
        entity.prevRotationYaw = entity.rotationYaw;
    }

    default void releaseMagick() {
        if(!spellContext().valid()) return;

        List<Entity> livings = findEntity();
        for(Entity living : livings) {
            if(living != this && !suitableEntity(living)) continue;
            AtomicReference<Boolean> pass = new AtomicReference<>(true);
            if(spellContext().containChild(LibContext.CONDITION)) {
                ConditionContext context = spellContext().getChild(LibContext.CONDITION);
                context.conditions.forEach((condition -> {
                    if(condition.getType() == EnumTargetType.TARGET) {
                        if(!condition.test(living))
                            pass.set(false);
                    } else if(!condition.test(this.getOwner()))
                        pass.set(false);
                }));
            }
            if(pass.get()) {
                MagickContext context = MagickContext.create(((Entity)this).world, spellContext().postContext)
                        .<MagickContext>replenishChild(DirectionContext.create(((Entity) this).getPositionVec().subtract(living.getPositionVec())))
                        .caster(getOwner()).projectile((Entity) this)
                        .victim(living);
                MagickReleaseHelper.releaseMagick(context);
            }
        }
    }

    default boolean suitableEntity(Entity entity) {
        EnumApplyType applyType = spellContext().applyType;
        if(spellContext().postContext != null)
            applyType = spellContext().postContext.applyType;

        boolean sameLikeOwner = MagickReleaseHelper.sameLikeOwner(this.getOwner(), entity);
        if(!applyType.isBeneficial() && sameLikeOwner) {
            return false;
        }

        boolean refraction = false;
        if(entity instanceof IManaRefraction)
            refraction = ((IManaRefraction) entity).refraction(spellContext());
        return !refraction && (!applyType.isBeneficial() || sameLikeOwner);
    }

    default void beforeJoinWorld(MagickContext context) {

    }

    @OnlyIn(Dist.CLIENT)
    ResourceLocation getEntityIcon();

    @OnlyIn(Dist.CLIENT)
    default void renderFrame(float partialTicks) {}
}
