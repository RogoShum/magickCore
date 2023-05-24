package com.rogoshum.magickcore.client.entity.easyrender;

import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.function.Consumer;

public class LightSourceRenderer extends EasyRenderer<Entity> {
    public LightSourceRenderer(Entity entity) {
        super(entity);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        return null;
    }
}
