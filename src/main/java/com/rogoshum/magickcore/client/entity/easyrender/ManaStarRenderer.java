package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.projectile.ManaStarEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Vector3d;

public class ManaStarRenderer extends EasyRenderer<ManaStarEntity>{

    @Override
    public void render(ManaStarEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        matrixStackIn.scale(entityIn.getWidth(), entityIn.getHeight(), entityIn.getWidth());
        entityIn.spellContext().element.getRenderer().renderStar(matrixStackIn, bufferIn, 0.8f, Integer.toString(entityIn.getEntityId()), 2f);
    }
}
