package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.client.BufferPackage;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.ManaOrbEntity;
import com.rogoshum.magickcore.entity.ManaSphereEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.ArrayList;

public class ManaSphereRenderer extends EasyRenderer<ManaSphereEntity>{

    @Override
    public void render(ManaSphereEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            //EasyRenderer.renderRift(matrixStackIn, bufferIn, RenderHelper.ORB, entityIn, 3.0f, entityIn.getElement().getRenderer().getColor()
                    //, 1.0f, partialTicks, entityIn.world);

            float scale = entityIn.getWidth() * 1.6f;
            if(entityIn.ticksExisted < 9)
                scale *= 1 - 1f / ((float)entityIn.ticksExisted + 1f);

            if(entityIn.getTickTime() - entityIn.ticksExisted <= 9)
                scale *= 1 - 1f / (float)(entityIn.getTickTime() - entityIn.ticksExisted);

            int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);
            matrixStackIn.scale(scale, scale, scale);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
            VectorHitReaction[] test = {};
            entityIn.getElement().getRenderer().renderSphere(BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f)), 6, 0.4f, entityIn.getHitReactions(), 2.10f, packedLightIn);
            //matrixStackIn.scale(0.48f, 0.48f, 0.48f);
            //entityIn.getElement().getRenderer().renderOrb(matrixStackIn, bufferIn, 0.5f, Integer.toString(entityIn.getEntityId()), 0.1f);
            matrixStackIn.scale(0.30f, 0.30f, 0.30f);
            entityIn.getElement().getRenderer().renderSphere(BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f)), 4, 0.9f, entityIn.getHitReactions(), 6.0f, packedLightIn);
        }
    }
}
