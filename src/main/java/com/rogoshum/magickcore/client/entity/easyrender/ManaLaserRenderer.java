package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.ManaLaserEntity;
import com.rogoshum.magickcore.entity.ManaStarEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Vector3d;

public class ManaLaserRenderer extends EasyRenderer<ManaLaserEntity>{

    @Override
    public void render(ManaLaserEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        matrixStackIn.scale(entityIn.getWidth() / 6, entityIn.getHeight() / 6, entityIn.getWidth() / 6);
        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            //entityIn.getElement().getRenderer().renderLaser(matrixStackIn, bufferIn, 0.8f);
            if (entityIn.getTrail() == null)
                entityIn.setTrail(new TrailParticle(entityIn, new Vector3d(0, entityIn.getHeight() / 2, 0), 100, 0.75d));
            else{
                entityIn.getTrail().tick();
                float alpha = 1.0f;
                for (Vector3d vec : entityIn.getTrail().getTrailPoint()) {
                    matrixStackIn.push();
                    matrixStackIn.translate( vec.x - entityIn.getPositionVec().x, vec.y - entityIn.getPositionVec().y, vec.z - entityIn.getPositionVec().z);
                    entityIn.getElement().getRenderer().renderLaser(matrixStackIn, bufferIn, alpha);
                    matrixStackIn.pop();
                }
            }
        }
    }
}
