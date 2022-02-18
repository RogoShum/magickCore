package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.ManaOrbEntity;
import com.rogoshum.magickcore.entity.ManaStarEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Vector3d;

public class ManaStarRenderer extends EasyRenderer<ManaStarEntity>{

    @Override
    public void render(ManaStarEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        matrixStackIn.scale(entityIn.getWidth(), entityIn.getHeight(), entityIn.getWidth());
        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            entityIn.getElement().getRenderer().renderStar(matrixStackIn, bufferIn, 0.8f, Integer.toString(entityIn.getEntityId()), 2f);
            if (entityIn.getTrail() == null)
                entityIn.setTrail(new TrailParticle(entityIn, new Vector3d(0, entityIn.getHeight() / 2, 0), 25, 0.5d));
            else{
                matrixStackIn.scale(0.2f, 0.2f, 0.2f);
                entityIn.getTrail().tick();
                float alpha = (float) (entityIn.getMotion().length()) * 1.5f;
                for (Vector3d vec : entityIn.getTrail().getTrailPoint()) {
                    matrixStackIn.push();
                    matrixStackIn.translate( vec.x - entityIn.getPositionVec().x, vec.y - entityIn.getPositionVec().y, vec.z - entityIn.getPositionVec().z);
                    entityIn.getElement().getRenderer().renderTrail(matrixStackIn, bufferIn, alpha *= 0.9f, Integer.toString(entityIn.getEntityId()), 5f);
                    matrixStackIn.pop();
                    matrixStackIn.scale(0.99f, 0.99f, 0.99f);
                }
            }
        }
    }
}
