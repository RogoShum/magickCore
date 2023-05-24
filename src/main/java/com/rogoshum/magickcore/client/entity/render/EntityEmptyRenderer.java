package com.rogoshum.magickcore.client.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class EntityEmptyRenderer extends EntityRenderer<Entity> {
	private static final ResourceLocation blankTex = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");

	public EntityEmptyRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager);
	}


	@Override
	public void render(Entity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity entity) {
		return blankTex;
	}
}
