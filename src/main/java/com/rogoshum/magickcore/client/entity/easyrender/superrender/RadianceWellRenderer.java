package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.superentity.RadianceWellEntity;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class RadianceWellRenderer extends EasyRenderer<RadianceWellEntity> {

    @Override
    public void render(RadianceWellEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        Matrix4f positionMatrix = matrixStackIn.getLast().getMatrix();
        int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);

        if(entityIn.spellContext().element != null && entityIn.spellContext().element.getRenderer() != null) {
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
            matrixStackIn.translate(0, -entityIn.getHeight() / 2 + 0.005, 0);
            matrixStackIn.translate(0, entityIn.getHeight(), 0);
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            float alphaS = Math.min(1f, (float)entityIn.ticksExisted / 5f);
            matrixStackIn.push();

            entityIn.spellContext().element.getRenderer().renderSphere(
                    BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f))
                    , 12, 0.7f * alphaS, entityIn.getHitReactions(), 0.0f, packedLightIn);

            matrixStackIn.push();
            float scale = 1.1f;
            matrixStackIn.scale(scale, scale, scale);
            entityIn.spellContext().element.getRenderer().renderSphere(
                    BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f)).useShader(LibShaders.slime)
                    , 12, 0.6f * alphaS, entityIn.getHitReactions(), 0.0f, packedLightIn);
            scale = 0.6f;
            matrixStackIn.scale(scale, scale, scale);
            ModElements.ORIGIN.getRenderer().renderSphere(
                    BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f)).useShader(LibShaders.slime)
                    , 12, 0.4f * alphaS, entityIn.getHitReactions(), 0.0f, packedLightIn);
            matrixStackIn.pop();

            matrixStackIn.pop();
            matrixStackIn.scale(1.4f, 1.4f, 1.4f);
            if(entityIn.initial) {
                matrixStackIn.push();
                matrixStackIn.scale(0.25f, 0.25f, 0.25f);
                entityIn.getTrail().tick();
                float alpha = (float) (entityIn.getMotion().length()) * 1.5f;

                for (int i = 0; i < 40; ++i) {
                    matrixStackIn.push();
                    matrixStackIn.translate(entityIn.getPositionVec().x, entityIn.getPositionVec().y + i * 0.01, entityIn.getPositionVec().z);
                    entityIn.spellContext().element.getRenderer().renderTrail(matrixStackIn, bufferIn, alpha *= 0.9f, Integer.toString(entityIn.getEntityId()), 0f);
                    matrixStackIn.pop();
                    matrixStackIn.scale(0.98f, 0.98f, 0.98f);
                }
                matrixStackIn.pop();
                matrixStackIn.scale(0.7142f, 0.7142f, 0.7142f);
                float alphaC = Math.min(1f, (float) entityIn.ticksExisted / 20f);

                matrixStackIn.push();
                matrixStackIn.translate(0, -entityIn.getHeight() * 2, 0);
                matrixStackIn.scale(6.05f, 1.45f, 6.05f);
                //RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(cylinder_bloom, 1f, 0f)), 0.0f, alphaC, entityIn.spellContext().element.getRenderer().getColor()
                        //, 2f, 6.0f, 16, entityIn.getHitReactions(), 0.2f);
                RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn,
                        RenderHelper.getTexedCylinderGlint(cylinder_bloom, 1f, 0f)).useShader(LibShaders.slime),
                        0f, alphaC, ModElements.ORIGIN.getRenderer().getColor()
                        , 2f, 6.0f, 16, entityIn.getHitReactions(), 0.2f);
                scale = 1.01f;
                matrixStackIn.scale(scale, scale, scale);
                RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn,
                        RenderHelper.getTexedCylinderGlint(cylinder_bloom, 1f, 0f)).useShader(LibShaders.slime),
                        0f, alphaC, entityIn.spellContext().element.getRenderer().getColor()
                        , 2f, 6.0f, 16, entityIn.getHitReactions(), 0.2f);
                matrixStackIn.pop();
            }
        }
    }
}
