package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.projectile.ManaOrbEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Vector3d;

public class ManaOrbRenderer extends EasyRenderer<ManaOrbEntity>{

    @Override
    public void render(ManaOrbEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        matrixStackIn.scale(entityIn.getWidth() * 0.6f, entityIn.getWidth() * 0.6f, entityIn.getWidth() * 0.6f);
        entityIn.spellContext().element.getRenderer().renderOrb(matrixStackIn, bufferIn, 0.8f);
    }
}
