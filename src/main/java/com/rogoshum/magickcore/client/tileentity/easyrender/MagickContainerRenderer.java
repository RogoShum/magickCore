package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.block.tileentity.MagickContainerTileEntity;
import com.rogoshum.magickcore.block.tileentity.MagickCraftingTileEntity;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class MagickContainerRenderer extends EasyTileRenderer<MagickContainerTileEntity>{
    @Override
    public void render(MagickContainerTileEntity tileEntityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        float[] color = {1, 1, 1};
        matrixStackIn.push();
        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
        IManaElement element = ModElements.getElement(tileEntityIn.eType);
        if(element != null)
            color = element.getRenderer().getColor();
        matrixStackIn.push();
        float scale = (float) tileEntityIn.getManaCapacity() / (float)tileEntityIn.maxManaCapacity;
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderSphere(matrixStackIn.getLast().getMatrix(), bufferIn, RenderHelper.getTexedSphereGlow(blank), 4, 0.3f, color, RenderHelper.renderLight);
        matrixStackIn.pop();

        RenderHelper.renderSphere(matrixStackIn.getLast().getMatrix(), bufferIn, RenderHelper.getTexedSphereGlow(cylinder_rotate), 4, 1.0f, RenderHelper.ORIGIN, RenderHelper.renderLight);
        matrixStackIn.pop();
        matrixStackIn.scale(0.6f, 0.6f, 0.6f);
        //RenderHelper.renderParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlow(orbTex)), 0.5f, color);
        if(tileEntityIn.getMaterialItem() != null) {
            matrixStackIn.scale(0.8f, 0.8f, 0.8f);
            IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(tileEntityIn.getMaterialItem(), tileEntityIn.getWorld(), (LivingEntity) null);
            Minecraft.getInstance().getItemRenderer().renderItem(tileEntityIn.getMaterialItem(), ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, ibakedmodel);
        }
        matrixStackIn.pop();
    }
}
