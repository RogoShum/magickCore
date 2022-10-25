package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.tileentity.MagickRepeaterTileEntity;
import com.rogoshum.magickcore.client.tileentity.easyrender.EasyTileRenderer;
import com.rogoshum.magickcore.event.RenderEvent;
import com.rogoshum.magickcore.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;

public class MagickRepeaterItemStackTileEntityRenderer extends ItemStackTileEntityRenderer {
    protected final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");
    private final BlockState state = ModBlocks.magick_repeater.get().getDefaultState();

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

        MagickRepeaterTileEntity tileEntity = (MagickRepeaterTileEntity) state.createTileEntity(Minecraft.getInstance().world);
        if(stack.hasTag())
        {
            CompoundNBT blockTag = getBlockTag(stack.getTag());

            if(!blockTag.isEmpty())
                tileEntity.read(state, blockTag);
        }
        tileEntity.setWorldAndPos(Minecraft.getInstance().world, BlockPos.ZERO);

        TileEntityRenderer<TileEntity> tileentityrenderer = TileEntityRendererDispatcher.instance.getRenderer(tileEntity);
        //EasyTileRenderer<MagickRepeaterTileEntity> tileRenderer = RenderEvent.getTileRenderer(tileEntity);

        tileentityrenderer.render(tileEntity, 0, matrixStackIn, bufferIn, combinedLight, combinedOverlay);

        matrixStackIn.translate(0.5, 0.5, 0.5);
        //if(tileEntity.getWorld() != null)
            //tileRenderer.render(tileEntity, matrixStackIn, (IRenderTypeBuffer.Impl) bufferIn, 0);

        matrixStackIn.pop();
    }

    public static CompoundNBT getBlockTag(CompoundNBT tag)
    {
        CompoundNBT blockTag = tag.getCompound("BlockEntityTag");
        return blockTag;
    }
}
