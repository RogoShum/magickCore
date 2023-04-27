package com.rogoshum.magickcore.client.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class ManaEntityRenderer extends EntityRenderer<ManaEntity> {
	private static final ResourceLocation blankTex = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");

	public ManaEntityRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager);
	}

	@Override
	protected int getBlockLightLevel(ManaEntity entityIn, BlockPos partialTicks) {
		return 15;
	}

	@Override
	public void render(ManaEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public boolean shouldRender(ManaEntity livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
		livingEntityIn.cansee = super.shouldRender(livingEntityIn, camera, camX, camY, camZ);
		return livingEntityIn.cansee;
	}

	@Override
	public ResourceLocation getTextureLocation(ManaEntity entity) {
		return blankTex;
	}
}
