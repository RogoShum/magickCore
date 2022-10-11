package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.entity.superentity.ChaoReachEntity;
import com.rogoshum.magickcore.lib.LibShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ChaosReachRenderer extends EasyRenderer<ChaoReachEntity> {

    @Override
    public void render(ChaoReachEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);

        if(entityIn.spellContext().element != null && entityIn.spellContext().element.getRenderer() != null) {
            //EasyRenderer.renderRift(matrixStackIn, bufferIn.getBuffer(RenderHelper.ORB), entityIn, 5.0f, entityIn.getElement().getRenderer().getColor(), 1.0f, partialTicks, entityIn.world);
            ElementRenderer renderer = entityIn.spellContext().element.getRenderer();
            if(entityIn.initial) {
                float scale = Math.min(1f, (float) (entityIn.ticksExisted - 30) / 5f);
                matrixStackIn.scale(1.45f * scale, 1.45f * scale, 1.45f * scale);
                matrixStackIn.push();
                float c = entityIn.ticksExisted % 11;
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360f * (c / 10)));
                renderer.renderSphere(
                        BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f)).useShader(LibShaders.slime)
                        , 6, 0.5f, entityIn.getHitReactions(), 3.2f, packedLightIn);
                matrixStackIn.pop();
                matrixStackIn.push();
                matrixStackIn.scale(scale + 0.2f * MagickCore.rand.nextFloat(), scale + 0.2f * MagickCore.rand.nextFloat(), scale + 0.2f * MagickCore.rand.nextFloat());
                RenderHelper.renderParticle(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/shield/element_shield_" + Integer.toString(entityIn.ticksExisted % 10) + ".png"))), 1.0f, renderer.getColor());
                matrixStackIn.pop();

                matrixStackIn.scale(0.8f, 0.8f, 0.8f);
                renderer.renderSphere(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f))
                        .useShader(LibShaders.slime), 4, 0.9f, entityIn.getHitReactions(), 5.2f, packedLightIn);
                /*
                scale = 3.2f;
                matrixStackIn.scale(scale, scale, scale);
                renderer.renderSphere(
                        BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f)).useShader(LibShaders.slime)
                        , 8, 0.5f, entityIn.getHitReactions(), 3.2f, packedLightIn);

                 */
            }
        }
    }
}
