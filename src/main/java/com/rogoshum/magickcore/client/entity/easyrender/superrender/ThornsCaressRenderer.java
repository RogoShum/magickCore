package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.entity.superentity.ThornsCaressEntity;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ThornsCaressRenderer extends EasyRenderer<ThornsCaressEntity> {

    @Override
    public void render(ThornsCaressEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);

        if(entityIn.spellContext().element.getRenderer() != null) {
            ElementRenderer renderer = entityIn.spellContext().element.getRenderer();
            ElementRenderer originRender = ModElements.ORIGIN.getRenderer();
            //EasyRenderer.renderRift(matrixStackIn, bufferIn.getBuffer(RenderHelper.ORB), entityIn, 5.0f, renderer.getColor()
                    //, 1.0f, partialTicks, entityIn.world);
            matrixStackIn.scale(1.45f, 1.45f, 1.45f);
            matrixStackIn.push();
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            originRender.renderSphere(BufferContext.create(matrixStackIn, bufferIn,
                    RenderHelper.getTexedSphereGlow(blank, 1f, 0f)).useShader(LibShaders.slime)
                    , 6, 0.5f, entityIn.getHitReactions(), 2.10f, packedLightIn);
            matrixStackIn.pop();
            matrixStackIn.push();
            matrixStackIn.scale(1.45f, 1.45f, 1.45f);
            renderer.renderSphere(BufferContext.create(matrixStackIn, bufferIn,
                    RenderHelper.getTexedSphereGlow(sphere_rotate, 1f, 0f)).useShader(LibShaders.opacity)
                    , 6, 0.9f, entityIn.getHitReactions(), 2.10f, packedLightIn);
            matrixStackIn.pop();
            float c = entityIn.ticksExisted % 11;
            float degress = 360f * (c / 10);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(degress));
            RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(1.0f, 1.0f, 1, 1.0f, 8, 0.2f, 0.7f, 0.3f);
            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(cylinder_rotate, 1f, 0f)).useShader(LibShaders.slime)
                    , renderer.getColor()
                    , context, entityIn.getHitReactions(), 0.5f);
            matrixStackIn.scale(1.1f, 1.1f, 1.1f);
            context = new RenderHelper.CylinderContext(1.0f, 1.0f, 1, 1.0f, 8, 0.4f, 1.0f, 0.3f);
            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(cylinder_rotate, 1f, 0f)).useShader(LibShaders.opacity)
                    , originRender.getColor()
                    , context, entityIn.getHitReactions(), 0.5f);
        }
    }
}
