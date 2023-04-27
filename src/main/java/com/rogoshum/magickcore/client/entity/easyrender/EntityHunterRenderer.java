package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.pointed.EntityHunterEntity;
import com.mojang.blaze3d.vertex.BufferBuilder;

import java.util.HashMap;
import java.util.function.Consumer;

public class EntityHunterRenderer extends EasyRenderer<EntityHunterEntity> {

    public EntityHunterRenderer(EntityHunterEntity entity) {
        super(entity);
    }

    public void render(EntityHunterEntity entityIn, PoseStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {

    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        return null;
    }
}
