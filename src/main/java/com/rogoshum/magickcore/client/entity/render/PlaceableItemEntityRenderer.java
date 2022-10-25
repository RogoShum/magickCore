package com.rogoshum.magickcore.client.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.render.RenderHelper;
import com.rogoshum.magickcore.entity.PlaceableItemEntity;
import com.rogoshum.magickcore.entity.base.ManaEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class PlaceableItemEntityRenderer extends EntityRenderer<PlaceableItemEntity> {
	public PlaceableItemEntityRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public void render(PlaceableItemEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		if(entityIn.getDirection().getAxis().isVertical() && entityIn.getDirection().getAxisDirection().getOffset() == -1) {
			matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180));
		} else if(entityIn.getDirection().getAxis().equals(Direction.Axis.X)) {
			matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(entityIn.getDirection().toVector3f().getX() * 90));
		} else if(entityIn.getDirection().getAxis().equals(Direction.Axis.Z)) {
			matrixStackIn.rotate(Vector3f.XP.rotationDegrees(entityIn.getDirection().toVector3f().getZ() * 90));
		}
		matrixStackIn.push();
		IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(entityIn.getItemStack(), entityIn.world, (LivingEntity)null);
		Minecraft.getInstance().getItemRenderer().renderItem(entityIn.getItemStack(), ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
		matrixStackIn.pop();
	}

	@Override
	public ResourceLocation getEntityTexture(PlaceableItemEntity entity) {
		return null;
	}
}
