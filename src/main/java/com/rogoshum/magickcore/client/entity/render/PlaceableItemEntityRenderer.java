package com.rogoshum.magickcore.client.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.common.entity.PlaceableItemEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;

public class PlaceableItemEntityRenderer extends EntityRenderer<PlaceableItemEntity> {
	public PlaceableItemEntityRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager);
	}

	@Override
	public void render(PlaceableItemEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		if(entityIn.getDirection().getAxis().isVertical() && entityIn.getDirection().getAxisDirection().getStep() == -1) {
			matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
		} else if(entityIn.getDirection().getAxis().equals(Direction.Axis.X)) {
			matrixStackIn.mulPose(Vector3f.ZN.rotationDegrees(entityIn.getDirection().step().x() * 90));
		} else if(entityIn.getDirection().getAxis().equals(Direction.Axis.Z)) {
			matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(entityIn.getDirection().step().z() * 90));
		}
		matrixStackIn.pushPose();
		BakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getModel(entityIn.getItemStack(), entityIn.level, (LivingEntity)null, 0);
		Minecraft.getInstance().getItemRenderer().render(entityIn.getItemStack(), ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
		matrixStackIn.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(PlaceableItemEntity entity) {
		return null;
	}
}
