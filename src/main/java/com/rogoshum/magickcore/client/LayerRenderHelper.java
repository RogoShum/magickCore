package com.rogoshum.magickcore.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.*;
import java.util.List;

public class LayerRenderHelper extends LivingRenderer {
    private float[] color;
    private float alpha;
    public LayerRenderHelper(EntityRendererManager rendererManager, EntityModel entityModelIn) {
        super(rendererManager, entityModelIn, 0);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setColor(float[] color) {
        this.color = color;
    }

    public void setEntityModel(EntityModel model) {
        this.entityModel = model;
    }

    public void preRender(LivingEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime)
    {
        super.preRenderCallback(entitylivingbaseIn, matrixStackIn, partialTickTime);
    }

    public void render(LivingEntity entityIn, LivingRenderer renderer, ResourceLocation tex, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        this.entityModel.swingProgress = this.getSwingProgress(entityIn, partialTicks);

        boolean shouldSit = entityIn.isPassenger() && (entityIn.getRidingEntity() != null && entityIn.getRidingEntity().shouldRiderSit());
        this.entityModel.isSitting = shouldSit;
        this.entityModel.isChild = entityIn.isChild();
        float f = MathHelper.interpolateAngle(partialTicks, entityIn.prevRenderYawOffset, entityIn.renderYawOffset);
        float f1 = MathHelper.interpolateAngle(partialTicks, entityIn.prevRotationYawHead, entityIn.rotationYawHead);
        float f2 = f1 - f;
        if (shouldSit && entityIn.getRidingEntity() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entityIn.getRidingEntity();
            f = MathHelper.interpolateAngle(partialTicks, livingentity.prevRenderYawOffset, livingentity.renderYawOffset);
            f2 = f1 - f;
            float f3 = MathHelper.wrapDegrees(f2);
            if (f3 < -85.0F) {
                f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
                f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
                f += f3 * 0.2F;
            }

            f2 = f1 - f;
        }

        float f6 = MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch);
        if (entityIn.getPose() == Pose.SLEEPING) {
            Direction direction = entityIn.getBedDirection();
            if (direction != null) {
                float f4 = entityIn.getEyeHeight(Pose.STANDING) - 0.1F;
                matrixStackIn.translate((double)((float)(-direction.getXOffset()) * f4), 0.0D, (double)((float)(-direction.getZOffset()) * f4));
            }
        }

        float f7 = this.handleRotationFloat(entityIn, partialTicks);
        this.applyRotations(entityIn, matrixStackIn, f7, f, partialTicks);
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);

        Method me = ObfuscationReflectionHelper.findMethod(LivingRenderer.class, "preRenderCallback", LivingEntity.class,MatrixStack.class,float.class);
        try {
            me.invoke(renderer, entityIn, matrixStackIn, partialTicks);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        matrixStackIn.translate(0.0D, (double)-1.501F, 0.0D);
        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && entityIn.isAlive()) {
            f8 = MathHelper.lerp(partialTicks, entityIn.prevLimbSwingAmount, entityIn.limbSwingAmount);
            f5 = entityIn.limbSwing - entityIn.limbSwingAmount * (1.0F - partialTicks);
            if (entityIn.isChild()) {
                f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }

        this.entityModel.setLivingAnimations(entityIn, f5, f8, partialTicks);
        this.entityModel.setRotationAngles(entityIn, f5, f8, f7, f2, f6);
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = this.isVisible(entityIn);
        boolean flag1 = !flag && !entityIn.isInvisibleToPlayer(minecraft.player);
        boolean flag2 = minecraft.isEntityGlowing(entityIn);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderHelper.getTexedEntityGlint(tex));
        int i = getPackedOverlay(entityIn, this.getOverlayProgress(entityIn, partialTicks));
        this.entityModel.render(matrixStackIn, ivertexbuilder, packedLightIn, i, color[0], color[1], color[2], this.alpha);

        List<LayerRenderer> layerRenderers = ObfuscationReflectionHelper.getPrivateValue(LivingRenderer.class, renderer, "layerRenderers");
        for(int cc = 0; cc < layerRenderers.size(); ++cc)
        {
            LayerRenderer layerrenderer = layerRenderers.get(cc);
            layerrenderer.render(matrixStackIn, bufferIn, packedLightIn, entityIn, f5, f8, partialTicks, f7, f2, f6);
        }

        matrixStackIn.pop();
    }



    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
