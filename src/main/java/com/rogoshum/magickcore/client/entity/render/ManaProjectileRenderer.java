package com.rogoshum.magickcore.client.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class ManaProjectileRenderer extends EntityRenderer<ManaProjectileEntity> {
	private static final ResourceLocation blankTex = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");

	public ManaProjectileRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager);
	}

	protected int getBlockLight(ManaEntity entityIn, BlockPos partialTicks) {
		return 15;
	}

	@Override
	public void render(ManaProjectileEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(ManaProjectileEntity entity) {
		return blankTex;
	}
}
