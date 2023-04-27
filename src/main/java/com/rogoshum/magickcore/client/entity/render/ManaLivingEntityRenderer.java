package com.rogoshum.magickcore.client.entity.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.common.entity.living.QuadrantCrystalEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class ManaLivingEntityRenderer extends EntityRenderer<LivingEntity> {
	private static final ResourceLocation blankTex = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
	private static final RenderType EYE = RenderHelper.getTexedEntityGlow(MagickCore.fromId("textures/entity/mana_eye.png"));

	public ManaLivingEntityRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager);
	}

	@Override
	protected int getBlockLightLevel(LivingEntity entityIn, BlockPos partialTicks) {
		return 15;
	}

	@Override
	public void render(LivingEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		if(entityIn instanceof QuadrantCrystalEntity quadrant) {
			BufferBuilder buffer = Tesselator.getInstance().getBuilder();
			matrixStackIn.pushPose();
			matrixStackIn.translate(0, 1.5, 0);
			matrixStackIn.scale(3, 3, 3);
			matrixStackIn.translate(-0.025, 0, 0);
			RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, EYE)
					, new RenderHelper.RenderContext(0.8f, quadrant.spellContext().element.primaryColor(), RenderHelper.renderLight));
			matrixStackIn.popPose();
		}
	}

	@Override
	public boolean shouldRender(LivingEntity p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
		return false;
	}

	@Override
	public ResourceLocation getTextureLocation(LivingEntity entity) {
		return blankTex;
	}
}
