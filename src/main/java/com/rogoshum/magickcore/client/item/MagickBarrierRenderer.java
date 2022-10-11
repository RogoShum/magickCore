package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.tileentity.MagickBarrierTileEntity;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.init.ModBlocks;
import com.rogoshum.magickcore.magick.Color;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class MagickBarrierRenderer extends ItemStackTileEntityRenderer {
    protected final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");
    protected final ResourceLocation orbTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/magick_orb.png");
    protected final ResourceLocation ripple_4 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ripple/ripple_4.png");

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.push();

        if(transformType == ItemCameraTransforms.TransformType.GUI) {
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(47.5f));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(20));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(20));

            matrixStackIn.scale(0.65f, 0.65f, 0.65f);
            matrixStackIn.translate(0.1f, 0.4F, 0);
        }
        else if(transformType == ItemCameraTransforms.TransformType.GROUND) {
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            matrixStackIn.translate(0.5F, 0.5F, 0.5F);
        }
        else {
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            matrixStackIn.translate(0.5F, 0.5F, 0.5F);
        }
        IRenderTypeBuffer.Impl impl = (IRenderTypeBuffer.Impl) bufferIn;
        BlockState state = ModBlocks.magick_barrier.get().getDefaultState();
        MagickBarrierTileEntity tileEntityIn = (MagickBarrierTileEntity) state.createTileEntity(Minecraft.getInstance().world);

        if(stack.hasTag())
        {
            CompoundNBT blockTag = getBlockTag(stack.getTag());

            if(!blockTag.isEmpty())
                tileEntityIn.read(state, blockTag);
        }

        float alphaScale = tileEntityIn.mana / tileEntityIn.requiredMana;
        if(Float.isNaN(alphaScale))
            alphaScale = 0;
        matrixStackIn.push();
        matrixStackIn.translate(0.5F, 0.5F, 0.5F);
        Color color = tileEntityIn.spellContext().element.getRenderer().getColor();
        float alpha = 0.5f;
        float alphaAdd = 0.15f;
        float blockBB = 0.25f;
        int angle = 0;

        for(int i = 0; i < 3 ; ++i){
            if(i == 1 )
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90));
            else if(i == 2 )
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
            for(int c = 0; c < 4 ; ++c) {
                RenderType type = RenderHelper.getLineGlow(1.5 + alphaScale * 2);
                IVertexBuilder line = impl.getBuffer(type);

                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(angle));
                line.pos(matrixStackIn.getLast().getMatrix(), blockBB, blockBB, blockBB).color(color.r(), color.g(), color.b(), alphaAdd + alpha * alphaScale).
                        tex(0, 0).overlay(OverlayTexture.NO_OVERLAY).
                        lightmap(RenderHelper.renderLight).normal(0.5f, 0.5f, 0.5f).endVertex();

                line.pos(matrixStackIn.getLast().getMatrix(), blockBB, -blockBB, blockBB).color(color.r(), color.g(), color.b(), alphaAdd + alpha * alphaScale).
                        tex(0, 0).overlay(OverlayTexture.NO_OVERLAY).
                        lightmap(RenderHelper.renderLight).normal(0.5f, 0.5f, 0.5f).endVertex();

                impl.finish(type);
                angle += 90;
            }
        }
        matrixStackIn.pop();
        matrixStackIn.pop();
    }

    public static CompoundNBT getBlockTag(CompoundNBT tag)
    {
        CompoundNBT blockTag = tag.getCompound("BlockEntityTag");
        return blockTag;
    }
}
