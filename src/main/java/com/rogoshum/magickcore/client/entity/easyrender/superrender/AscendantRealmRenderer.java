package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.superentity.AscendantRealmEntity;
import com.rogoshum.magickcore.entity.superentity.RadianceWellEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;

import java.util.HashMap;
import java.util.Iterator;

public class AscendantRealmRenderer extends EasyRenderer<AscendantRealmEntity> {

    @Override
    public void render(AscendantRealmEntity entityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        matrixStackIn.translate(0, -entityIn.getHeight() / 2 + 0.005, 0);
        matrixStackIn.scale(1.002f, 1.002f, 1.002f);
        Matrix4f positionMatrix = matrixStackIn.getLast().getMatrix();
        int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);
        //entityIn.setGlowing(true);
        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            //EasyRenderer.renderRift(matrixStackIn, bufferIn.getBuffer(RenderHelper.OUTLINE), entityIn, 7.0f, entityIn.getElement().getRenderer().getColor()
                    //, 10.0f, partialTicks, entityIn.world, entityIn.getUniqueID().toString(), 0.0f);
            EasyRenderer.renderRift(matrixStackIn, bufferIn.getBuffer(RenderHelper.ORB), entityIn, 7.0f, entityIn.getElement().getRenderer().getColor()
                    , 10.0f, partialTicks, entityIn.world, entityIn.getUniqueID().toString(), 0.0f);
            matrixStackIn.scale(0.997f, 0.997f, 0.997f);
            EasyRenderer.renderRift(matrixStackIn, bufferIn.getBuffer(RenderHelper.CRUMBLING), entityIn, 7.0f, entityIn.getElement().getRenderer().getColor()
                    , 10.0f, partialTicks, entityIn.world, entityIn.getUniqueID().toString(), 0.0f);
        }
    }
}
