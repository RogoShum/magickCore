package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.entity.pointed.EntityHunterEntity;
import net.minecraft.client.renderer.BufferBuilder;

import java.util.HashMap;
import java.util.function.Consumer;

public class EntityHunterRenderer extends EasyRenderer<EntityHunterEntity> {

    public EntityHunterRenderer(EntityHunterEntity entity) {
        super(entity);
    }

    public void render(EntityHunterEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {

    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        return null;
    }
}
