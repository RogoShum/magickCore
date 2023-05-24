package com.rogoshum.magickcore.client.entity.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.common.entity.InteractiveItemEntity;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.phys.AABB;

import java.util.OptionalDouble;
import java.util.Random;

public class InteractiveItemEntityRenderer extends EntityRenderer<InteractiveItemEntity> {
	private static final RenderType TYPE = RenderHelper.getLineStripPC(5);
	public InteractiveItemEntityRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager);
	}

	@Override
	public void render(InteractiveItemEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		float scale = Math.min(entityIn.tickCount * 0.1f, 1);
		matrixStackIn.pushPose();
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((MagickCore.proxy.getRunTick() % 180)*2));
		BlockPos blockpos = new BlockPos(entityIn.getX(), entityIn.getBoundingBox().maxY, entityIn.getZ());
		matrixStackIn.scale(0.35f*scale, 0.15f*scale, 0.35f*scale);
		matrixStackIn.translate(-0.5D, -0.050D, -0.5D);
		BlockRenderDispatcher blockrenderdispatcher = Minecraft.getInstance().getBlockRenderer();
		for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType.chunkBufferLayers()) {
			if (ItemBlockRenderTypes.canRenderInLayer(Blocks.QUARTZ_SLAB.defaultBlockState(), type)) {
				net.minecraftforge.client.ForgeHooksClient.setRenderType(type);
				blockrenderdispatcher.getModelRenderer().tesselateBlock(entityIn.level, blockrenderdispatcher.getBlockModel(Blocks.QUARTZ_SLAB.defaultBlockState()), Blocks.QUARTZ_SLAB.defaultBlockState(), blockpos, matrixStackIn
						, bufferIn.getBuffer(type), false, new Random(), Blocks.QUARTZ_SLAB.defaultBlockState().getSeed(blockpos), OverlayTexture.NO_OVERLAY);
			}
		}
		net.minecraftforge.client.ForgeHooksClient.setRenderType(null);
		matrixStackIn.popPose();
		matrixStackIn.pushPose();
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((MagickCore.proxy.getRunTick() % 360)*1));
		matrixStackIn.translate(0, 0.15f, 0);
		matrixStackIn.scale(0.5f*scale, 0.5f*scale, 0.5f*scale);
		BakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getModel(entityIn.getItemStack(), entityIn.level, (LivingEntity)null, 0);
		Minecraft.getInstance().getItemRenderer().render(entityIn.getItemStack(), ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
		matrixStackIn.popPose();
	}

	public void renderCube(PoseStack matrixStackIn, VertexConsumer buffer, Vec3i offset, Color color, float alpha) {
		LevelRenderer.renderLineBox(matrixStackIn, buffer, new AABB(new BlockPos(offset)).deflate(0.25), color.r(), color.g(), color.b(), alpha);
	}

	@Override
	public ResourceLocation getTextureLocation(InteractiveItemEntity entity) {
		return null;
	}
}
