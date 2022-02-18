package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.BufferPackage;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.superentity.ChaoReachEntity;
import com.rogoshum.magickcore.entity.superentity.ThornsCaressEntity;
import com.rogoshum.magickcore.lib.LibShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.Iterator;

public class ThornsCaressRenderer extends EasyRenderer<ThornsCaressEntity> {

    @Override
    public void render(ThornsCaressEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);

        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            //EasyRenderer.renderRift(matrixStackIn, bufferIn.getBuffer(RenderHelper.ORB), entityIn, 5.0f, entityIn.getElement().getRenderer().getColor()
                    //, 1.0f, partialTicks, entityIn.world);
            matrixStackIn.scale(1.45f, 1.45f, 1.45f);
            matrixStackIn.push();
            matrixStackIn.scale(0.25f, 0.25f, 0.25f);
            entityIn.getElement().getRenderer().renderOrb(matrixStackIn, bufferIn, 1.0f, Integer.toString(entityIn.getEntityId()), 0.1f);
            entityIn.getElement().getRenderer().renderOrb(matrixStackIn, bufferIn, 1.0f, Integer.toString(entityIn.getEntityId()), 2f);
            matrixStackIn.pop();
            matrixStackIn.push();
            matrixStackIn.scale(1.45f, 1.45f, 1.45f);
            entityIn.getElement().getRenderer().renderSphere(BufferPackage.create(matrixStackIn, bufferIn,
                    RenderHelper.getTexedSphereGlow(blank, 1f, 0f))
                    , 6, 1f, entityIn.getHitReactions(), 2.10f, packedLightIn);
            matrixStackIn.scale(1.25f, 1.25f, 1.25f);
            entityIn.getElement().getRenderer().renderSphere(BufferPackage.create(matrixStackIn, bufferIn,
                    RenderHelper.getTexedSphereGlow(blank, 1f, 0f)).useShader(LibShaders.slime)
                    , 6, 1f, entityIn.getHitReactions(), 2.10f, packedLightIn);
            matrixStackIn.pop();
            float c = entityIn.ticksExisted % 11;
            float degress = 360f * (c / 10);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(degress));
            //matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(360f * (c / 10)));
            RenderHelper.renderCylinder(BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(cylinder_rotate, 1f, 0f)), 0.0f, 1.0f, entityIn.getElement().getRenderer().getColor()
                    , 1.0f, 1f, 8, entityIn.getHitReactions(), 0.5f);
            matrixStackIn.scale(1.25f, 1.25f, 1.25f);
        }
    }
}
