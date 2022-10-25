package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

public abstract class EasyTileRenderer<T extends TileEntity> {
    protected static final ResourceLocation sphereOrb = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_bloom.png");
    protected static final ResourceLocation cylinder_bloom = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_bloom.png");
    protected static final ResourceLocation sphere_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_rotate.png");
    protected static final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");
    protected static final ResourceLocation orbTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/magick_orb.png");
    protected static final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected static final ResourceLocation ripple_4 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ripple/ripple_4.png");

    public EasyTileRenderer(){};

    public void preRender(T entity, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks)
    {
        if(entity == null || entity.getWorld() == null || entity.getWorld().getBlockState(entity.getPos()) == null)
            return;

        matrixStackIn.push();
        double x = entity.getPos().getX() + 0.5;
        double y = entity.getPos().getY() + 0.5;
        double z = entity.getPos().getZ() + 0.5;
        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        matrixStackIn.translate(x - camX, y - camY, z - camZ);

        render(entity, matrixStackIn, bufferIn, partialTicks);
        postRender(entity, matrixStackIn, bufferIn, partialTicks);
    }

    public abstract void render(T entity, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks);

    public void postRender(T entity, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks)
    {
        matrixStackIn.pop();
        bufferIn.finish();
    }
}
