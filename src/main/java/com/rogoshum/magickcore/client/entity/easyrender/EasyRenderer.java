package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.VertexShakerHelper;
import com.rogoshum.magickcore.entity.ManaRiftEntity;
import com.rogoshum.magickcore.entity.baseEntity.ManaEntity;
import com.rogoshum.magickcore.entity.baseEntity.ManaProjectileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

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
    public EasyRenderer(){};

    public void preRender(T entity, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks)
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
        postRender(entity, matrixStackIn, bufferIn, partialTicks);
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
        return new Vector2f(yaw, pitch);
    }

    public abstract void render(T entity, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks);

    public void postRender(T entity, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks)
    {
        matrixStackIn.pop();
        bufferIn.finish();
    }

    public static void renderRift(MatrixStack matrixStackIn, IVertexBuilder bufferIn, ManaEntity entityIn, float sizeIn, float[] color, float alphaIn, float partialTicks, IWorldReader worldIn, String shakeName, float limit) {
        MatrixStack.Entry matrixEntryIn = matrixStackIn.getLast();
        float f = sizeIn * 1.0f;

        double d2 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosX, entityIn.getPosX());
        double d0 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosY, entityIn.getPosY());
        double d1 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosZ, entityIn.getPosZ());
        int i = MathHelper.floor(d2 - (double)f);
        int j = MathHelper.floor(d2 + (double)f);
        int k = MathHelper.floor(d0 - (double)f);
        int l = MathHelper.floor(d0);
        int i1 = MathHelper.floor(d1 - (double)f);
        int j1 = MathHelper.floor(d1 + (double)f);

        List<Vector3d> riftPoint = new ArrayList<>();

        for(BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(i, k , i1), new BlockPos(j, l + sizeIn, j1))) {
            getBlockShadow(riftPoint, worldIn, blockpos, d2, d0, d1);
        }

        VertexShakerHelper.VertexGroup group = VertexShakerHelper.getGroup(shakeName);
        for (int c = 0; c < riftPoint.size(); ++c)
        {
            Vector3d vec1 = riftPoint.get(c);
            for (int m = 0; m < riftPoint.size(); ++m) {
                Vector3d vec2 = riftPoint.get(m);
                double xs = Math.abs(vec1.x - vec2.x);
                double zs = Math.abs(vec1.z - vec2.z);
                double ys = Math.abs(vec1.y - vec2.y);
                boolean zl = zs < 0.1;
                boolean xl = xs < 0.1;
                if(c != m && (zl || xl) && !(zl && xl)) {
                    if (vec1.y - vec2.y > 0.1 && zl && xs < 1.1) {
                        Vector3d newVec = vec1.add(vec2).scale(0.5d);
                        float f1 = (float)(newVec.y - ys / 2);
                        float f2 = (float)(newVec.y + ys / 2);
                        float f3 = (float)(newVec.x);
                        float f4 = (float)(newVec.z - 0.5f);
                        float f5 = (float)(newVec.z + 0.5f);

                        float f6 = -f1 / 2.0F / sizeIn + 0.5F;
                        float f7 = -f2 / 2.0F / sizeIn + 0.5F;
                        float f8 = -f4 / 2.0F / sizeIn + 0.5F;
                        float f9 = -f5 / 2.0F / sizeIn + 0.5F;

                        group.putVertex(f3, f1, f4, limit);
                        group.putVertex(f3, f1, f5, limit);
                        group.putVertex(f3, f2, f5, limit);
                        group.putVertex(f3, f2, f4, limit);

                        Vector3d V0 = group.getVertex(f3, f1, f4).getPositionVec();
                        Vector3d V1 = group.getVertex(f3, f1, f5).getPositionVec();
                        Vector3d V2 = group.getVertex(f3, f2, f5).getPositionVec();
                        Vector3d V3 = group.getVertex(f3, f2, f4).getPositionVec();

                        shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, getAlpha(entityIn.getWidth(), (float) V0.length(), sizeIn, alphaIn), (float) V0.x, (float) V0.y, (float) V0.z, f6, f8);
                        shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, getAlpha(entityIn.getWidth(), (float) V1.length(), sizeIn, alphaIn), (float) V1.x, (float) V1.y, (float) V1.z, f6, f9);
                        shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, getAlpha(entityIn.getWidth(), (float) V2.length(), sizeIn, alphaIn), (float) V2.x, (float) V2.y, (float) V2.z, f7, f9);
                        shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, getAlpha(entityIn.getWidth(), (float) V3.length(), sizeIn, alphaIn), (float) V3.x, (float) V3.y, (float) V3.z, f7, f8);
                    }

                    if (vec1.y - vec2.y > 0.1 && xl && zs < 1.1) {
                        Vector3d newVec = vec1.add(vec2).scale(0.5d);
                        float f1 = (float)(newVec.x - 0.5f);
                        float f2 = (float)(newVec.x + 0.5f);
                        float f3 = (float)(newVec.z);
                        float f4 = (float)(newVec.y - ys / 2);
                        float f5 = (float)(newVec.y + ys / 2);

                        float f6 = -f1 / 2.0F / sizeIn + 0.5F;
                        float f7 = -f2 / 2.0F / sizeIn + 0.5F;
                        float f8 = -f4 / 2.0F / sizeIn + 0.5F;
                        float f9 = -f5 / 2.0F / sizeIn + 0.5F;

                        group.putVertex(f1, f4, f3, limit);
                        group.putVertex(f2, f4, f3, limit);
                        group.putVertex(f2, f5, f3, limit);
                        group.putVertex(f1, f5, f3, limit);

                        Vector3d V0 = group.getVertex(f1, f4, f3).getPositionVec();
                        Vector3d V1 = group.getVertex(f2, f4, f3).getPositionVec();
                        Vector3d V2 = group.getVertex(f2, f5, f3).getPositionVec();
                        Vector3d V3 = group.getVertex(f1, f5, f3).getPositionVec();

                        shadowVertex(matrixEntryIn, bufferIn ,entityIn.getHitReactions(), color, sizeIn, getAlpha(entityIn.getWidth(), (float) V0.length(), sizeIn, alphaIn), (float) V0.x, (float) V0.y, (float) V0.z, f6, f8);
                        shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, getAlpha(entityIn.getWidth(), (float) V1.length(), sizeIn, alphaIn), (float) V1.x, (float) V1.y, (float) V1.z, f7, f8);
                        shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, getAlpha(entityIn.getWidth(), (float) V2.length(), sizeIn, alphaIn), (float) V2.x, (float) V2.y, (float) V2.z, f7, f9);
                        shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, getAlpha(entityIn.getWidth(), (float) V3.length(), sizeIn, alphaIn), (float) V3.x, (float) V3.y, (float) V3.z, f6, f9);
                    }
                }
            }

            float f1 = (float)(vec1.x - 0.5f);
            float f2 = (float)(vec1.x + 0.5f);
            float f3 = (float)(vec1.y);
            float f4 = (float)(vec1.z - 0.5f);
            float f5 = (float)(vec1.z + 0.5f);

            float f6 = -f1 / 2.0F / sizeIn + 0.5F;
            float f7 = -f2 / 2.0F / sizeIn + 0.5F;
            float f8 = -f4 / 2.0F / sizeIn + 0.5F;
            float f9 = -f5 / 2.0F / sizeIn + 0.5F;

            group.putVertex(f1, f3, f4, limit);
            group.putVertex(f1, f3, f5, limit);
            group.putVertex(f2, f3, f5, limit);
            group.putVertex(f2, f3, f4, limit);

            Vector3d V0 = group.getVertex(f1, f3, f4).getPositionVec();
            Vector3d V1 = group.getVertex(f1, f3, f5).getPositionVec();
            Vector3d V2 = group.getVertex(f2, f3, f5).getPositionVec();
            Vector3d V3 = group.getVertex(f2, f3, f4).getPositionVec();

            shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, getAlpha(entityIn.getWidth(), (float) V0.length(), sizeIn, alphaIn), (float) V0.x, (float) V0.y, (float) V0.z, f6, f8);
            shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, getAlpha(entityIn.getWidth(), (float) V1.length(), sizeIn, alphaIn), (float) V1.x, (float) V1.y, (float) V1.z, f6, f9);
            shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, getAlpha(entityIn.getWidth(), (float) V2.length(), sizeIn, alphaIn), (float) V2.x, (float) V2.y, (float) V2.z, f7, f9);
            shadowVertex(matrixEntryIn, bufferIn, entityIn.getHitReactions(), color, sizeIn, getAlpha(entityIn.getWidth(), (float) V3.length(), sizeIn, alphaIn), (float) V3.x, (float) V3.y, (float) V3.z, f7, f8);
        }
    }

    private static float getAlpha(float width, float length, float scale, float oriAlpha)
    {
        float alpha = width / oriAlpha * scale - length;
        if(alpha > 0.3f)
            alpha = 0.3f;
        if(alpha < 0.0f)
            alpha = 0.0f;
        return alpha;
    }

    private static void getBlockShadow(List<Vector3d> riftPoint, IWorldReader worldIn, BlockPos blockPosIn, double xIn, double yIn, double zIn) {
        BlockPos blockpos = blockPosIn.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (blockstate.getRenderType() != BlockRenderType.INVISIBLE && worldIn.getLight(blockPosIn) > 3) {
            if (blockstate.hasOpaqueCollisionShape(worldIn, blockpos)) {
                VoxelShape voxelshape = blockstate.getShape(worldIn, blockPosIn.down());
                if (!voxelshape.isEmpty()) {
                    AxisAlignedBB axisalignedbb = voxelshape.getBoundingBox();
                    double d0 = (double)blockPosIn.getX() + axisalignedbb.minX;
                    double d1 = (double)blockPosIn.getX() + axisalignedbb.maxX;
                    double d2 = (double)blockPosIn.getY() + axisalignedbb.minY;
                    double d3 = (double)blockPosIn.getZ() + axisalignedbb.minZ;
                    double d4 = (double)blockPosIn.getZ() + axisalignedbb.maxZ;
                    float f1 = (float)(d0 - xIn);
                    float f2 = (float)(d1 - xIn);
                    float f3 = (float)(d2 - yIn);
                    float f4 = (float)(d3 - zIn);
                    float f5 = (float)(d4 - zIn);

                    riftPoint.add(new Vector3d((f1 + f2) / 2f, f3, (f4 + f5) / 2f));

                }
            }
        }
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

        if(alphaIn + maxAlhpa >= 1.0f){
            maxAlhpa = 1.0f - alphaIn;
        }

        bufferIn.pos(matrixEntryIn.getMatrix(), xIn, yIn, zIn).color(redScale, greenScale, blueScale, alphaIn + maxAlhpa).tex(texU, texV).overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(matrixEntryIn.getNormal(), xIn, yIn, zIn).endVertex();
    }
}
