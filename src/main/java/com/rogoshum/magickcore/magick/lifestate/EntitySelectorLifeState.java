package com.rogoshum.magickcore.magick.lifestate;

import com.rogoshum.magickcore.entity.LifeStateEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;

import java.util.List;

public class EntitySelectorLifeState extends EntityLifeState {
    @Override
    public void tick(LifeStateEntity lifeState) {
        if(this.value != null && lifeState.getElementData().getTrace())
            lifeState.setMotion(this.value.getPositionVec().add(0, this.value.getHeight() / 2, 0).subtract(lifeState.getPositionVec()).normalize().scale(0.1));
    }
}
