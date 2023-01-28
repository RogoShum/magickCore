/**
 * from forge
 */
package com.rogoshum.magickcore.api.event.living;

import com.rogoshum.magickcore.api.event.EntityEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class LivingEvent extends EntityEvent{
    private final LivingEntity entityLiving;

    public LivingEvent(LivingEntity entity) {
        super(entity);
        entityLiving = entity;
    }

    public LivingEntity getEntityLiving() {
        return entityLiving;
    }

    public static class LivingUpdateEvent extends LivingEvent {
        public LivingUpdateEvent(LivingEntity e) {
            super(e);
        }
    }

    public static class LivingJumpEvent extends LivingEvent {
        public LivingJumpEvent(LivingEntity e) {
            super(e);
        }
    }

    public static class LivingVisibilityEvent extends LivingEvent {
        private double visibilityModifier;
        @Nullable
        private final Entity lookingEntity;

        public LivingVisibilityEvent(LivingEntity livingEntity, @Nullable Entity lookingEntity, double originalMultiplier) {
            super(livingEntity);
            this.visibilityModifier = originalMultiplier;
            this.lookingEntity = lookingEntity;
        }

        /**
         * @param mod Is multiplied with the current modifier
         */
        public void modifyVisibility(double mod) {
            visibilityModifier *= mod;
        }

        /**
         * @return The current modifier
         */
        public double getVisibilityModifier() {
            return visibilityModifier;
        }

        /**
         * @return The entity trying to see this LivingEntity, if available
         */
        @Nullable
        public Entity getLookingEntity() {
            return lookingEntity;
        }
    }
}
