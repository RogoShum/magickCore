package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.entity.superentity.AscendantRealmEntity;

import java.util.HashMap;
import java.util.function.Consumer;

public class AscendantRealmRenderer extends EasyRenderer<AscendantRealmEntity> {

    public AscendantRealmRenderer(AscendantRealmEntity entity) {
        super(entity);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        return null;
    }
}
