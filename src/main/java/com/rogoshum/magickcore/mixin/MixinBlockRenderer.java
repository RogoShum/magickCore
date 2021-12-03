package com.rogoshum.magickcore.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.tool.EntityLightSourceHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.*;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@Mixin(BlockModelRenderer.class)

public abstract class MixinBlockRenderer {

    @Redirect(
            method = "net/minecraft/client/renderer/BlockModelRenderer.renderQuadSmooth (Lnet/minecraft/world/IBlockDisplayReader;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lcom/mojang/blaze3d/matrix/MatrixStack$Entry;Lnet/minecraft/client/renderer/model/BakedQuad;FFFFIIIII)V",
            at = @At(
                    value = "INVOKE",
                    target = "com/mojang/blaze3d/vertex/IVertexBuilder.addQuad (Lcom/mojang/blaze3d/matrix/MatrixStack$Entry;Lnet/minecraft/client/renderer/model/BakedQuad;[FFFF[IIZ)V"
            )
    )
    private void onRenderQuadSmooth(IVertexBuilder buffer1, MatrixStack.Entry matrixEntryIn, BakedQuad quadIn1, float[] colorMuls, float redIn, float greenIn, float blueIn, int[] combinedLightsIn, int combinedOverlayIn1, boolean mulColor, IBlockDisplayReader blockAccessIn, BlockState stateIn, BlockPos posIn, IVertexBuilder buffer, MatrixStack.Entry matrixEntry, BakedQuad quadIn, float colorMul0, float colorMul1, float colorMul2, float colorMul3, int brightness0, int brightness1, int brightness2, int brightness3, int combinedOverlayIn) {
        float f;
        float f1;
        float f2;
        if (quadIn.hasTintIndex()) {
            BlockColors blockColors = ObfuscationReflectionHelper.getPrivateValue(BlockModelRenderer.class, Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer()
            , "blockColors");
            int i = blockColors.getColor(stateIn, blockAccessIn, posIn, quadIn.getTintIndex());
            f = (float)(i >> 16 & 255) / 255.0F;
            f1 = (float)(i >> 8 & 255) / 255.0F;
            f2 = (float)(i & 255) / 255.0F;
        } else {
            f = 1.0F;
            f1 = 1.0F;
            f2 = 1.0F;
        }

        if(EntityLightSourceHandler.shouldRenderColor(Minecraft.getInstance().world, Vector3d.copy(posIn)))
            addQuad(posIn, buffer, matrixEntry, quadIn, new float[]{colorMul0, colorMul1, colorMul2, colorMul3}, f, f1, f2, new int[]{brightness0, brightness1, brightness2, brightness3}, combinedOverlayIn, true);
        else
            buffer.addQuad(matrixEntry, quadIn, new float[]{colorMul0, colorMul1, colorMul2, colorMul3}, f, f1, f2, new int[]{brightness0, brightness1, brightness2, brightness3}, combinedOverlayIn, true);
    }

    private void addQuad(BlockPos blockPos, IVertexBuilder buffer, MatrixStack.Entry matrixEntryIn, BakedQuad quadIn, float[] colorMuls, float redIn, float greenIn, float blueIn, int[] combinedLightsIn, int combinedOverlayIn, boolean mulColor) {
        int[] aint = quadIn.getVertexData();
        Vector3i vector3i = quadIn.getFace().getDirectionVec();
        Vector3f vector3f = new Vector3f((float)vector3i.getX(), (float)vector3i.getY(), (float)vector3i.getZ());
        Matrix4f matrix4f = matrixEntryIn.getMatrix();
        vector3f.transform(matrixEntryIn.getNormal());
        int i = 8;
        int j = aint.length / 8;

        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormats.BLOCK.getSize());
            IntBuffer intbuffer = bytebuffer.asIntBuffer();

            for(int k = 0; k < j; ++k) {
                ((Buffer)intbuffer).clear();
                intbuffer.put(aint, k * 8, 8);
                float f = bytebuffer.getFloat(0);
                float f1 = bytebuffer.getFloat(4);
                float f2 = bytebuffer.getFloat(8);
                float f3;
                float f4;
                float f5;
                if (mulColor) {
                    float f6 = (float)(bytebuffer.get(12) & 255) / 255.0F;
                    float f7 = (float)(bytebuffer.get(13) & 255) / 255.0F;
                    float f8 = (float)(bytebuffer.get(14) & 255) / 255.0F;
                    f3 = f6 * colorMuls[k] * redIn;
                    f4 = f7 * colorMuls[k] * greenIn;
                    f5 = f8 * colorMuls[k] * blueIn;
                } else {
                    f3 = colorMuls[k] * redIn;
                    f4 = colorMuls[k] * greenIn;
                    f5 = colorMuls[k] * blueIn;
                }

                int l = buffer.applyBakedLighting(combinedLightsIn[k], bytebuffer);
                float f9 = bytebuffer.getFloat(16);
                float f10 = bytebuffer.getFloat(20);
                Vector4f vector4f = new Vector4f(f, f1, f2, 1.0F);
                vector4f.transform(matrix4f);
                buffer.applyBakedNormals(vector3f, bytebuffer, matrixEntryIn.getNormal());

                float[] color = EntityLightSourceHandler.getLightColor(Minecraft.getInstance().world,
                        Vector3d.copy(blockPos).add(f, f1, f2),
                        f3, f4, f5, l);
                buffer.addVertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), color[0], color[1], color[2], 1.0F, f9, f10, combinedOverlayIn, l, vector3f.getX(), vector3f.getY(), vector3f.getZ());
            }
        }
    }
}
