package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.BufferPackage;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.VertexShakerHelper;
import com.rogoshum.magickcore.entity.ManaRiftEntity;
import com.rogoshum.magickcore.entity.baseEntity.ManaEntity;
import com.rogoshum.magickcore.entity.baseEntity.ManaProjectileEntity;
import com.rogoshum.magickcore.lib.LibShaders;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class EasyRenderer<T extends Entity> {
    protected final ResourceLocation sphereOrb = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_bloom.png");
    protected final ResourceLocation cylinder_bloom = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_bloom.png");
    protected final ResourceLocation sphere_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_rotate.png");
    protected final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");
    protected final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");

    public void preRender(T entity, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks)
    {
        if(entity instanceof ManaEntity && !((ManaEntity) entity).cansee)
            return;

        if(entity instanceof ManaProjectileEntity && !((ManaProjectileEntity) entity).cansee)
            return;

        matrixStackIn.push();
        double x = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * (double) partialTicks;
        double y = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * (double) partialTicks;
        double z = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * (double) partialTicks;

        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        matrixStackIn.translate(x - camX, y - camY + entity.getHeight() / 2, z - camZ);

        render(entity, matrixStackIn, bufferIn, partialTicks);
        matrixStackIn.pop();
    }

    public static Vector2f getRotationFromVector(Vector3d dirc)
    {
        float yaw = (float) (Math.atan2(dirc.x, dirc.z) * 180 / Math.PI);
        if (yaw < 0)
            yaw += 360;

        float tmp = (float) Math.sqrt (dirc.z * dirc.z + dirc.x * dirc.x);
        float pitch = (float) (Math.atan2(-dirc.y, tmp) * 180 / Math.PI);
        if (pitch < 0)
            pitch += 360;
        return new Vector2f(yaw + 90, pitch - 90);
    }

    public static Vector3d getEntityRenderVector(Entity entity, float partialTicks)
    {
        double x = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * (double) partialTicks;
        double y = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * (double) partialTicks;
        double z = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * (double) partialTicks;
        return new Vector3d(x, y, z);
    }

    public abstract void render(T entity, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks);

    public static void renderRift(MatrixStack matrixStackIn, BufferBuilder bufferIn, RenderType type, ManaEntity entityIn, float sizeIn, float[] color, float alphaIn, float partialTicks, IWorldReader worldIn) {
        double d2 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosX, entityIn.getPosX());
        double d0 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosY + entityIn.getHeight() / 2, entityIn.getPosY() + entityIn.getHeight() / 2);
        double d1 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosZ, entityIn.getPosZ());
        int i = MathHelper.floor(d2 - (double)sizeIn);
        int j = MathHelper.floor(d2 + (double)sizeIn);
        int k = MathHelper.floor(d0);
        int l = MathHelper.floor(d0);
        int i1 = MathHelper.floor(d1 - (double)sizeIn);
        int j1 = MathHelper.floor(d1 + (double)sizeIn);
        BufferPackage pack = BufferPackage.create(matrixStackIn, bufferIn, type);
        for(BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(i, k - sizeIn, i1), new BlockPos(j, l + sizeIn, j1))) {
            RenderHelper.setup(pack);
            RenderHelper.begin(pack);
            getBlockShadow(entityIn, matrixStackIn, bufferIn, worldIn, blockpos, d2, d0, d1, color, alphaIn, sizeIn);
            RenderHelper.finish(pack);
            RenderHelper.end(pack);
        }
    }

    private static float getAlpha(float distance, float scale, float oriAlpha)
    {
        float alpha = (1f - (distance / scale)) * oriAlpha;

        if(alpha > 1f)
            alpha = 1f;
        if(alpha < 0.0f)
            alpha = 0.0f;
        return alpha;
    }

    private static void getBlockShadow(ManaEntity entityIn, MatrixStack matrixStackIn, IVertexBuilder bufferIn, IWorldReader worldIn, BlockPos blockPosIn, double xIn, double yIn, double zIn, float[] color, float alphaIn, float sizeIn) {
        MatrixStack.Entry matrixEntryIn = matrixStackIn.getLast();
        BlockState blockstate = worldIn.getBlockState(blockPosIn);
        //if (worldIn.getLight(blockPosIn) > 3) {
            VoxelShape voxelshape = blockstate.getShape(worldIn, blockPosIn);
            VoxelShape renderShape = blockstate.getRenderShape(worldIn, blockPosIn);
            if (!voxelshape.isEmpty() && !renderShape.isEmpty()) {
                AxisAlignedBB axisalignedbb = voxelshape.getBoundingBox();

                boolean upNonFull = worldIn.getBlockState(blockPosIn.up()).getShape(worldIn, blockPosIn.up()).equals(VoxelShapes.fullCube());
                if (worldIn.isAirBlock(blockPosIn.up()) || axisalignedbb.maxY < 1.0 || !upNonFull) {
                    double d0 = (double)blockPosIn.getX() + axisalignedbb.minX;
                    double d1 = (double)blockPosIn.getX() + axisalignedbb.maxX;
                    double d2 = (double)blockPosIn.getY() + axisalignedbb.maxY;
                    double d3 = (double)blockPosIn.getZ() + axisalignedbb.minZ;
                    double d4 = (double)blockPosIn.getZ() + axisalignedbb.maxZ;
                    float f1 = (float)(d0 - xIn);
                    float f2 = (float)(d1 - xIn);
                    float f3 = (float)(d2 - yIn) + 0.001f;
                    float f4 = (float)(d3 - zIn);
                    float f5 = (float)(d4 - zIn);

                    float f6 = -f1 / 2.0F / sizeIn + 0.5F;
                    float f7 = -f2 / 2.0F / sizeIn + 0.5F;
                    float f8 = -f4 / 2.0F / sizeIn + 0.5F;
                    float f9 = -f5 / 2.0F / sizeIn + 0.5F;

                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f1, f3, f4, f6, f8);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f1, f3, f5, f6, f9);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f2, f3, f5, f7, f9);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f2, f3, f4, f7, f8);
                }

                boolean downNonFull = worldIn.getBlockState(blockPosIn.down()).getShape(worldIn, blockPosIn.down()).equals(VoxelShapes.fullCube());
                if (worldIn.isAirBlock(blockPosIn.down()) || axisalignedbb.minY > 0.0 || !downNonFull) {
                    double d0 = (double)blockPosIn.getX() + axisalignedbb.minX;
                    double d1 = (double)blockPosIn.getX() + axisalignedbb.maxX;
                    double d2 = (double)blockPosIn.getY() + axisalignedbb.minY;
                    double d3 = (double)blockPosIn.getZ() + axisalignedbb.minZ;
                    double d4 = (double)blockPosIn.getZ() + axisalignedbb.maxZ;
                    float f1 = (float)(d0 - xIn);
                    float f2 = (float)(d1 - xIn);
                    float f3 = (float)(d2 - yIn) - 0.001f;
                    float f4 = (float)(d3 - zIn);
                    float f5 = (float)(d4 - zIn);

                    float f6 = -f1 / 2.0F / sizeIn + 0.5F;
                    float f7 = -f2 / 2.0F / sizeIn + 0.5F;
                    float f8 = -f4 / 2.0F / sizeIn + 0.5F;
                    float f9 = -f5 / 2.0F / sizeIn + 0.5F;

                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f1, f3, f4, f6, f8);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f1, f3, f5, f6, f9);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f2, f3, f5, f7, f9);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f2, f3, f4, f7, f8);
                }

                boolean westNonFull = worldIn.getBlockState(blockPosIn.west()).getShape(worldIn, blockPosIn.west()).equals(VoxelShapes.fullCube());
                if (worldIn.isAirBlock(blockPosIn.west()) || axisalignedbb.minX > 0.0 || !westNonFull) {
                    double d0 = (double)blockPosIn.getY() + axisalignedbb.minY;
                    double d1 = (double)blockPosIn.getY() + axisalignedbb.maxY;
                    double d2 = (double)blockPosIn.getX() + axisalignedbb.minX;
                    double d3 = (double)blockPosIn.getZ() + axisalignedbb.minZ;
                    double d4 = (double)blockPosIn.getZ() + axisalignedbb.maxZ;
                    float f1 = (float)(d0 - yIn);
                    float f2 = (float)(d1 - yIn);
                    float f3 = (float)(d2 - xIn) - 0.001f;
                    float f4 = (float)(d3 - zIn);
                    float f5 = (float)(d4 - zIn);

                    float f6 = -f1 / 2.0F / sizeIn + 0.5F;
                    float f7 = -f2 / 2.0F / sizeIn + 0.5F;
                    float f8 = -f4 / 2.0F / sizeIn + 0.5F;
                    float f9 = -f5 / 2.0F / sizeIn + 0.5F;

                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f3, f1, f4, f6, f8);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f3, f1, f5, f6, f9);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f3, f2, f5, f7, f9);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f3, f2, f4, f7, f8);
                }

                boolean eastNonFull = worldIn.getBlockState(blockPosIn.east()).getShape(worldIn, blockPosIn.east()).equals(VoxelShapes.fullCube());
                if (worldIn.isAirBlock(blockPosIn.east()) || axisalignedbb.maxX < 1.0 || !eastNonFull) {
                    double d0 = (double)blockPosIn.getY() + axisalignedbb.minY;
                    double d1 = (double)blockPosIn.getY() + axisalignedbb.maxY;
                    double d2 = (double)blockPosIn.getX() + axisalignedbb.maxX;
                    double d3 = (double)blockPosIn.getZ() + axisalignedbb.minZ;
                    double d4 = (double)blockPosIn.getZ() + axisalignedbb.maxZ;
                    float f1 = (float)(d0 - yIn);
                    float f2 = (float)(d1 - yIn);
                    float f3 = (float)(d2 - xIn) + 0.001f;
                    float f4 = (float)(d3 - zIn);
                    float f5 = (float)(d4 - zIn);

                    float f6 = -f1 / 2.0F / sizeIn + 0.5F;
                    float f7 = -f2 / 2.0F / sizeIn + 0.5F;
                    float f8 = -f4 / 2.0F / sizeIn + 0.5F;
                    float f9 = -f5 / 2.0F / sizeIn + 0.5F;

                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f3, f1, f4, f6, f8);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f3, f1, f5, f6, f9);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f3, f2, f5, f7, f9);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f3, f2, f4, f7, f8);
                }

                boolean northNonFull = worldIn.getBlockState(blockPosIn.north()).getShape(worldIn, blockPosIn.north()).equals(VoxelShapes.fullCube());
                if (worldIn.isAirBlock(blockPosIn.north()) || axisalignedbb.minZ > 0.0 || !northNonFull) {
                    double d0 = (double)blockPosIn.getX() + axisalignedbb.minX;
                    double d1 = (double)blockPosIn.getX() + axisalignedbb.maxX;
                    double d2 = (double)blockPosIn.getZ() + axisalignedbb.minZ;
                    double d3 = (double)blockPosIn.getY() + axisalignedbb.minY;
                    double d4 = (double)blockPosIn.getY() + axisalignedbb.maxY;
                    float f1 = (float)(d0 - xIn);
                    float f2 = (float)(d1 - xIn);
                    float f3 = (float)(d2 - zIn) - 0.001f;
                    float f4 = (float)(d3 - yIn);
                    float f5 = (float)(d4 - yIn);

                    float f6 = -f1 / 2.0F / sizeIn + 0.5F;
                    float f7 = -f2 / 2.0F / sizeIn + 0.5F;
                    float f8 = -f4 / 2.0F / sizeIn + 0.5F;
                    float f9 = -f5 / 2.0F / sizeIn + 0.5F;

                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f1, f4, f3, f6, f8);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f2, f4, f3, f6, f9);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f2, f5, f3, f7, f9);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f1, f5, f3, f7, f8);
                }

                boolean southNonFull = worldIn.getBlockState(blockPosIn.south()).getShape(worldIn, blockPosIn.south()).equals(VoxelShapes.fullCube());
                if (worldIn.isAirBlock(blockPosIn.south()) || axisalignedbb.maxZ < 1.0 || !southNonFull) {
                    double d0 = (double)blockPosIn.getX() + axisalignedbb.minX;
                    double d1 = (double)blockPosIn.getX() + axisalignedbb.maxX;
                    double d2 = (double)blockPosIn.getZ() + axisalignedbb.maxZ;
                    double d3 = (double)blockPosIn.getY() + axisalignedbb.minY;
                    double d4 = (double)blockPosIn.getY() + axisalignedbb.maxY;
                    float f1 = (float)(d0 - xIn);
                    float f2 = (float)(d1 - xIn);
                    float f3 = (float)(d2 - zIn) + 0.001f;
                    float f4 = (float)(d3 - yIn);
                    float f5 = (float)(d4 - yIn);

                    float f6 = -f1 / 2.0F / sizeIn + 0.5F;
                    float f7 = -f2 / 2.0F / sizeIn + 0.5F;
                    float f8 = -f4 / 2.0F / sizeIn + 0.5F;
                    float f9 = -f5 / 2.0F / sizeIn + 0.5F;

                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f1, f4, f3, f6, f8);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f2, f4, f3, f7, f8);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f2, f5, f3, f7, f9);
                    shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, alphaIn, f1, f5, f3, f6, f9);
                }
            }
        //}
    }

    private static float calculateDistance(Vector3d shadowPos)
    {
        return (float) (shadowPos.add(0.5f, 0.5f, 0.5f).length());
    }

    private static void shadowVertex(MatrixStack.Entry matrixEntryIn, IVertexBuilder bufferIn, VectorHitReaction[] hitReaction, float[] color, float scale, float alphaIn, float xIn, float yIn, float zIn, float texU, float texV) {
        float maxAlhpa = 0;

        if(hitReaction != null)
            for(int c = 0; c < hitReaction.length; c++ )
            {
                VectorHitReaction reaction = hitReaction[c];
                float add = reaction.IsHit(new Vector3d(xIn * scale, yIn * scale, zIn * scale));
                if(add > maxAlhpa)
                    maxAlhpa = add;
            }
        float redScale = color[0] + (color[0] * maxAlhpa);
        float greenScale = color[1] + (color[1] * maxAlhpa);
        float blueScale = color[2] + (color[2] * maxAlhpa);

        if(redScale > 1.0f) redScale = 1.0f; if(greenScale > 1.0f) greenScale = 1.0f; if(blueScale > 1.0f) blueScale = 1.0f;

        alphaIn = getAlpha(calculateDistance(new Vector3d(xIn, yIn, zIn)), scale, alphaIn);

        maxAlhpa *= alphaIn;

        if(alphaIn + maxAlhpa >= 1.0f){
            maxAlhpa = 1.0f - alphaIn;
        }

        bufferIn.pos(matrixEntryIn.getMatrix(), xIn, yIn, zIn).color(redScale, greenScale, blueScale, alphaIn + maxAlhpa).tex(texU, texV).overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(matrixEntryIn.getNormal(), xIn, yIn, zIn).endVertex();
    }
}
