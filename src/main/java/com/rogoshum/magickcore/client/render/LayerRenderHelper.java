package com.rogoshum.magickcore.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.*;
import java.util.List;

public class LayerRenderHelper extends LivingEntityRenderer {
    private Color color;
    private float alpha;
    public LayerRenderHelper(EntityRendererProvider.Context rendererManager, EntityModel entityModelIn) {
        super(rendererManager, entityModelIn, 0);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setEntityModel(EntityModel model) {
        this.model = model;
    }

    public void preRender(LivingEntity entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime)
    {
        super.scale(entitylivingbaseIn, matrixStackIn, partialTickTime);
    }

    public void render(LivingEntity entityIn, EntityRenderer renderer, ResourceLocation tex, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        this.model.attackTime = this.getAttackAnim(entityIn, partialTicks);

        boolean shouldSit = entityIn.isPassenger() && (entityIn.getVehicle() != null && entityIn.getVehicle().shouldRiderSit());
        this.model.riding = shouldSit;
        this.model.young = entityIn.isBaby();
        float f = Mth.rotLerp(partialTicks, entityIn.yBodyRotO, entityIn.yBodyRot);
        float f1 = Mth.rotLerp(partialTicks, entityIn.yHeadRotO, entityIn.yHeadRot);
        float f2 = f1 - f;
        if (shouldSit && entityIn.getVehicle() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entityIn.getVehicle();
            f = Mth.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
            f2 = f1 - f;
            float f3 = Mth.wrapDegrees(f2);
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

        float f6 = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
        if (entityIn.getPose() == Pose.SLEEPING) {
            Direction direction = entityIn.getBedOrientation();
            if (direction != null) {
                float f4 = entityIn.getEyeHeight(Pose.STANDING) - 0.1F;
                matrixStackIn.translate((double)((float)(-direction.getStepX()) * f4), 0.0D, (double)((float)(-direction.getStepZ()) * f4));
            }
        }

        float f7 = this.getBob(entityIn, partialTicks);
        this.setupRotations(entityIn, matrixStackIn, f7, f, partialTicks);
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);

        Method me = ObfuscationReflectionHelper.findMethod(LivingEntityRenderer.class, "scale", LivingEntity.class,PoseStack.class,float.class);
        try {
            me.invoke(renderer, entityIn, matrixStackIn, partialTicks);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        matrixStackIn.translate(0.0D, (double)-1.501F, 0.0D);
        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && entityIn.isAlive()) {
            f8 = Mth.lerp(partialTicks, entityIn.animationSpeedOld, entityIn.animationSpeed);
            f5 = entityIn.animationPosition - entityIn.animationSpeed * (1.0F - partialTicks);
            if (entityIn.isBaby()) {
                f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }

        RenderType type = RenderHelper.getTexedEntityGlint(tex, 0.32f, 10f);
        if(!tex.toString().contains(MagickCore.MOD_ID))
            type = getEntityRenderType(renderer, entityIn);
        if(type != null && this.model != null) {
            this.model.prepareMobModel(entityIn, f5, f8, partialTicks);
            this.model.setupAnim(entityIn, f5, f8, f7, f2, f6);
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(type);
            int i = getOverlayCoords(entityIn, this.getWhiteOverlayProgress(entityIn, partialTicks));
            this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, i, color.r(), color.g(), color.b(), this.alpha);
        }

        if(renderer instanceof LivingEntityRenderer) {
            List<RenderLayer> layerRenderers = ObfuscationReflectionHelper.getPrivateValue(LivingEntityRenderer.class, (LivingEntityRenderer)renderer, "f_115291_");
            for (int cc = 0; cc < layerRenderers.size(); ++cc) {
                RenderLayer layerrenderer = layerRenderers.get(cc);
                layerrenderer.render(matrixStackIn, bufferIn, packedLightIn, entityIn, f5, f8, partialTicks, f7, f2, f6);
            }
        }
        matrixStackIn.popPose();
    }

    public RenderType getEntityRenderType(EntityRenderer renderer, LivingEntity entityIn) {
        boolean flag = this.isBodyVisible(entityIn);
        boolean flag1 = !flag && !entityIn.isInvisibleTo(Minecraft.getInstance().player);
        boolean flag2 = Minecraft.getInstance().shouldEntityAppearGlowing(entityIn);
        ResourceLocation resourcelocation = renderer.getTextureLocation(entityIn);
        if (flag1) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (flag) {
            return this.model.renderType(resourcelocation);
        } else {
            return flag2 ? RenderType.outline(resourcelocation) : null;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return RenderHelper.blankTex;
    }
}
