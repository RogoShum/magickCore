package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.tileentity.CanSeeTileEntity;
import com.rogoshum.magickcore.block.tileentity.MagickContainerTileEntity;
import com.rogoshum.magickcore.block.tileentity.MagickCraftingTileEntity;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.VertexShakerHelper;
import com.rogoshum.magickcore.entity.baseEntity.ManaEntity;
import com.rogoshum.magickcore.entity.baseEntity.ManaProjectileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;

import java.util.ArrayList;
import java.util.List;

public abstract class EasyTileRenderer<T extends TileEntity> {
    protected final ResourceLocation sphereOrb = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_bloom.png");
    protected final ResourceLocation cylinder_bloom = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_bloom.png");
    protected final ResourceLocation sphere_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_rotate.png");
    protected final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");
    protected final ResourceLocation orbTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/magick_orb.png");
    protected final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected final ResourceLocation ripple_4 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ripple/ripple_4.png");

    public EasyTileRenderer(){};

    public void preRender(T entity, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks)
    {
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
