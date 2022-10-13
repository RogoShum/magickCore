package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.projectile.BubbleEntity;
import com.rogoshum.magickcore.entity.projectile.LampEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Vector3d;

public class BubbleRenderer extends EasyRenderer<BubbleEntity>{

    @Override
    public void render(BubbleEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        matrixStackIn.scale(entityIn.getWidth() * 0.6f, entityIn.getWidth() * 0.6f, entityIn.getWidth() * 0.6f);
        RenderHelper.renderParticle(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedOrbGlow(BubbleEntity.ICON)), 1.0f, entityIn.spellContext().element.color());
    }
}
