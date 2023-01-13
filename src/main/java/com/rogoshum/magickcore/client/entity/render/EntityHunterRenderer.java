package com.rogoshum.magickcore.client.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.model.EntityHunterModel;
import com.rogoshum.magickcore.common.entity.pointed.EntityHunterEntity;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class EntityHunterRenderer extends EntityRenderer<EntityHunterEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(MagickCore.MOD_ID + ":textures/entity/entity_hunter_texture.png");
	private final EntityHunterModel model = new EntityHunterModel();

	public EntityHunterRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	protected int getBlockLightLevel(EntityHunterEntity entityIn, BlockPos partialTicks) {
		return 15;
	}

	@Override
	public void render(EntityHunterEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		matrixStackIn.pushPose();
		matrixStackIn.scale(entityIn.getBbWidth(), entityIn.getBbHeight(), entityIn.getBbWidth());
		matrixStackIn.translate(0, -0.5, 0);
		Color color = entityIn.spellContext().element.color();
		this.model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedEntityGlow(TEXTURE)), packedLightIn, OverlayTexture.NO_OVERLAY, color.r(), color.g(), color.b(), 1.0F);
		matrixStackIn.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(EntityHunterEntity entity) {
		return TEXTURE;
	}
}
