package com.rogoshum.magickcore.client.entity.easyrender.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.entity.ManaRiftEntity;
import com.rogoshum.magickcore.entity.superentity.RadianceWellEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

public class ManaRiftOutlineRenderer extends EasyOutlineRender<RadianceWellEntity> {

    @Override
    public void render(RadianceWellEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        /*matrixStackIn.translate(0, -entityIn.getHeight() / 2, 0);
        //matrixStackIn.scale(1.002f, 1.002f, 1.002f);
        Matrix4f positionMatrix = matrixStackIn.getLast().getMatrix();
        int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);
        //MagickCore.LOGGER.debug("qwq");
        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            EasyRenderer.renderRift(matrixStackIn, bufferIn, entityIn, 4.0f, entityIn.getElement().getRenderer().getColor() , 9.0f, partialTicks, entityIn.world, entityIn.getUniqueID().toString() + this.toString(), 0.0f);
            matrixStackIn.scale(0.99f, 0.99f, 0.99f);
            EasyRenderer.renderRift(matrixStackIn, bufferIn, entityIn, 4.0f, entityIn.getElement().getRenderer().getColor(), 9.0f, partialTicks, entityIn.world, entityIn.getUniqueID().toString() + this.toString(), 0.0f);
        }*/
    }
}
