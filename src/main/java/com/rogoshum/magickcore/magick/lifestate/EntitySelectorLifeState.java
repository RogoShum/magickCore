package com.rogoshum.magickcore.magick.lifestate;

import com.rogoshum.magickcore.entity.projectile.LifeStateEntity;
import com.rogoshum.magickcore.lib.LibContext;

public class EntitySelectorLifeState extends EntityLifeState {
    @Override
    public void tick(LifeStateEntity lifeState) {
        if(this.value != null && lifeState.spellContext().containChild(LibContext.TRACE))
            lifeState.setMotion(this.value.getPositionVec().add(0, this.value.getHeight() / 2, 0).subtract(lifeState.getPositionVec()).normalize().scale(0.1));
    }
}
