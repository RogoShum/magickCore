package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.BufferPackage;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.ManaRiftEntity;
import com.rogoshum.magickcore.entity.superentity.RadianceWellEntity;
import com.rogoshum.magickcore.lib.LibShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.Iterator;

public class RadianceWellRenderer extends EasyRenderer<RadianceWellEntity> {

    @Override
    public void render(RadianceWellEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        Matrix4f positionMatrix = matrixStackIn.getLast().getMatrix();
        int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);

        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            matrixStackIn.push();
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(225));
            matrixStackIn.translate(0.4, 0.1, 0);
            matrixStackIn.scale(2.5f, 2.5f, 2.5f);
            ItemStack stack = new ItemStack(Items.GOLDEN_SWORD);
            IBakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, null, null);
            IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(bufferIn);
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, renderTypeBuffer, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
            renderTypeBuffer.finish();
            matrixStackIn.pop();

            EasyRenderer.renderRift(matrixStackIn, bufferIn, RenderHelper.ORB, entityIn, 7.0f, entityIn.getElement().getRenderer().getColor()
                    , 1.0f, partialTicks, entityIn.world);
            matrixStackIn.translate(0, -entityIn.getHeight() / 2 + 0.005, 0);
            matrixStackIn.translate(0, entityIn.getHeight(), 0);
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            float alphaS = Math.min(1f, (float)entityIn.ticksExisted / 5f);
            matrixStackIn.push();
            entityIn.getElement().getRenderer().renderSphere(
                    BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f))
                    , 12, 0.9f * alphaS, entityIn.getHitReactions(), 0.0f, packedLightIn);
            matrixStackIn.pop();
            matrixStackIn.scale(1.4f, 1.4f, 1.4f);
            Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
            if(entityIn.initial) {
                /*entityIn.setMotion(0, 1, 0);
                if (entityIn.getTrail() == null)
                    entityIn.setTrail(new TrailParticle(entityIn, new Vector3d(0, entityIn.getHeight() / 2, 0), 30, 0.8d));
                else {
                    matrixStackIn.push();
                    matrixStackIn.scale(0.3f, 0.3f, 0.3f);
                    entityIn.getTrail().tick();
                    float alpha = (float) (entityIn.getMotion().length()) * 1.5f;
                    for (Vector3d vec : entityIn.getTrail().getTrailPoint()) {
                        matrixStackIn.push();
                        matrixStackIn.translate(vec.x - entityIn.getPositionVec().x, vec.y - entityIn.getPositionVec().y, vec.z - entityIn.getPositionVec().z);
                        entityIn.getElement().getRenderer().renderTrail(matrixStackIn, bufferIn, alpha *= 0.9f, Integer.toString(entityIn.getEntityId()), 0f);
                        matrixStackIn.pop();
                        matrixStackIn.scale(0.98f, 0.98f, 0.98f);
                    }
                    matrixStackIn.pop();
                }
                matrixStackIn.scale(0.7142f, 0.7142f, 0.7142f);
                float alphaC = Math.min(1f, ((float) entityIn.ticksExisted - 20f) / 20f);
                matrixStackIn.push();
                matrixStackIn.translate(0, -entityIn.getHeight() * 2, 0);
                matrixStackIn.scale(entityIn.getWidth() * 1.45f, 0, entityIn.getWidth() * 1.45f);

                BufferPackage pack = BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedOrbGlow(sphereOrb));
                //RenderHelper.begin(pack);
                float[] color = entityIn.getElement().getRenderer().getColor();
                float alpha = 0.8f * alphaC;*/
                float[] color = {1, 1, 1};
                float alpha = 1;
                matrixStackIn.scale(5.4f, 5.4f, 5.4f);
                bufferIn.begin(7, DefaultVertexFormats.ENTITY);
                GlStateManager.bindTexture(33986);
                bufferIn.pos(matrix4f, -1.0f, 0.0f, -1.0f).color(color[0], color[1], color[2], alpha).tex(1.0f, 1.0f)
                        .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(-1.0f, 0.0f, -1.0f).endVertex();
                bufferIn.pos(matrix4f, -1.0f, 0.0f, 1.0f).color(color[0], color[1], color[2], alpha).tex(1.0f, 0.0f)
                        .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(-1.0f, 0.0f, 1.0f).endVertex();
                bufferIn.pos(matrix4f, 1.0f, 0.0f, 1.0f).color(color[0], color[1], color[2], alpha).tex(0.0f, 0.0f)
                        .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(1.0f, 0.0f, 1.0f).endVertex();
                bufferIn.pos(matrix4f, 1.0f, 0.0f, -1.0f).color(color[0], color[1], color[2], alpha).tex(0.0f, 1.0f)
                        .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(1.0f, 0.0f, -1.0f).endVertex();
                bufferIn.finishDrawing();
                WorldVertexBufferUploader.draw(bufferIn);
                /*//GlStateManager.bindTexture(0);
                //RenderHelper.finish(pack);
                matrixStackIn.pop();

                matrixStackIn.push();
                matrixStackIn.translate(0, -entityIn.getHeight() * 2, 0);
                matrixStackIn.scale(6.05f, 1.45f, 6.05f);
                RenderHelper.renderCylinder(BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(cylinder_bloom, 1f, 0f)), 0.0f, alphaC, entityIn.getElement().getRenderer().getColor()
                        , 2f, 6.0f, 16, entityIn.getHitReactions(), 0.2f);
                matrixStackIn.scale(1.1f, 1.1f, 1.1f);
                RenderHelper.renderCylinder(BufferPackage.create(matrixStackIn, bufferIn,
                        RenderHelper.getTexedCylinderGlint(cylinder_bloom, 1f, 0f)).useShader(LibShaders.slime),
                        0.0f, alphaC, entityIn.getElement().getRenderer().getColor()
                        , 2f, 6.0f, 16, entityIn.getHitReactions(), 0.2f);
                matrixStackIn.pop();*/
            }
        }
    }
}
