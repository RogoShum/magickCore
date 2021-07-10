package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.ManaOrbEntity;
import com.rogoshum.magickcore.entity.ManaRuneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class ManaRuneRenderer extends EasyRenderer<ManaRuneEntity>{

    @Override
    public void render(ManaRuneEntity entityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        matrixStackIn.scale(entityIn.getWidth() / 2, entityIn.getHeight() / 2, entityIn.getWidth() / 2);
        //matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90));
        matrixStackIn.translate(0, -entityIn.getHeight() * 0.9, 0);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            RenderHelper.renderStaticParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/rune.png"))), 1.0f, RenderHelper.ORIGIN,
                    false, "", 0);
        }
    }
}
