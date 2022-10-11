package com.rogoshum.magickcore.magick.lifestate;

import com.rogoshum.magickcore.entity.projectile.LifeStateEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.EntityRayTraceResult;

import java.util.List;

public class PotionLifeState extends ItemStackLifeState {

    @Override
    public void onHitEntity(LifeStateEntity lifeState, EntityRayTraceResult result) {
        super.onHitEntity(lifeState, result);
        List<EffectInstance> effects = PotionUtils.getEffectsFromStack(this.value);
        if(result.getEntity() instanceof LivingEntity){
            effects.forEach(effectinstance -> ((LivingEntity) result.getEntity())
                    .addPotionEffect(new EffectInstance(effectinstance.getPotion(), effectinstance.getDuration(), effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.doesShowParticles())));
            lifeState.remove();
        }
    }
}
