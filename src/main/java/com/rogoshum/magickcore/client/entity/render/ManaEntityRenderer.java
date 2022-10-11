package com.rogoshum.magickcore.client.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.entity.base.ManaEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class ManaEntityRenderer extends EntityRenderer<ManaEntity> {
	private static ResourceLocation blankTex = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");

	public ManaEntityRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	protected int getBlockLight(ManaEntity entityIn, BlockPos partialTicks) {
		return 15;
	}

	@Override
	public void render(ManaEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public boolean shouldRender(ManaEntity livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
		livingEntityIn.cansee = super.shouldRender(livingEntityIn, camera, camX, camY, camZ);
		return livingEntityIn.cansee;
	}

	@Override
	public ResourceLocation getEntityTexture(ManaEntity entity) {
		return blankTex;
	}
}
