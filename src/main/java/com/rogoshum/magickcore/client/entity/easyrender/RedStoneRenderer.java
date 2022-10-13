package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.projectile.LampEntity;
import com.rogoshum.magickcore.entity.projectile.RedStoneEntity;
import com.rogoshum.magickcore.magick.Color;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class RedStoneRenderer extends EasyRenderer<RedStoneEntity>{
    public static final ResourceLocation TRAIL = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/trail.png");
    @Override
    public void render(RedStoneEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        matrixStackIn.rotate(Vector3f.XN.rotationDegrees((float) (entityIn.getMotion().x * 360) / entityIn.getWidth()));
        matrixStackIn.rotate(Vector3f.ZN.rotationDegrees((float) (entityIn.getMotion().z * 360) / entityIn.getWidth()));
        float scale = entityIn.getWidth();
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.push();
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedEntity(RenderHelper.blankTex)), Color.RED_COLOR, 0, 0.3f);
        scale = 1.01f;
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedEntity(TRAIL)), Color.RED_COLOR, RenderHelper.halfLight, 0.9f);
        matrixStackIn.pop();
        matrixStackIn.rotate(Vector3f.YP.rotation(45));
        matrixStackIn.rotate(Vector3f.ZP.rotation(45));
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedEntity(RenderHelper.blankTex)), Color.RED_COLOR, 0, 0.3f);
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedEntity(TRAIL)), Color.RED_COLOR, RenderHelper.halfLight, 0.9f);
    }
}
