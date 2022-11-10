package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.projectile.ManaLaserEntity;
import net.minecraft.client.renderer.BufferBuilder;

import java.util.HashMap;
import java.util.function.Consumer;

public class ManaLaserRenderer extends EasyRenderer<ManaLaserEntity> {

    public ManaLaserRenderer(ManaLaserEntity entity) {
        super(entity);
    }

    public void render(ManaLaserEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {

    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        return null;
    }
}
