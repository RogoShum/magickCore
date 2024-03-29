package com.rogoshum.magickcore.client.entity.easyrender.base;

import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;

import java.util.HashMap;
import java.util.function.Consumer;

public class ManaEntityRenderer extends EasyRenderer<ManaEntity>{
    public ManaEntityRenderer(ManaEntity entity) {
        super(entity);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        return null;
    }
}
