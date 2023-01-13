package com.rogoshum.magickcore.client.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.model.EntityHunterModel;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.common.entity.living.ArtificialLifeEntity;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class ArtificialLifeEntityRenderer extends EntityRenderer<ArtificialLifeEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(MagickCore.MOD_ID + ":textures/entity/artificial_life.png");
	private static final ResourceLocation EYE_TEXTURE = new ResourceLocation(MagickCore.MOD_ID + ":textures/entity/artificial_life_eye.png");

	private static ItemStack ITEM;

	public ArtificialLifeEntityRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	protected int getBlockLightLevel(ArtificialLifeEntity entityIn, BlockPos partialTicks) {
		return 15;
	}

	@Override
	public void render(ArtificialLifeEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		if(entityIn.deathTime <= 0 && entityIn.isFocus()) {
			matrixStackIn.pushPose();
			matrixStackIn.translate(0, entityIn.getEyeHeight(), 0);
			if(entityIn.getVectorSet().isEmpty()) {
				matrixStackIn.scale(0.5f, 0.5f, 0.5f);
				Vector3d direction = Vector3d.atLowerCornerOf(entityIn.getDirection().getOpposite().getNormal());
				Vector2f rota = EasyRenderer.getRotationFromVector(direction);
				matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rota.x));
				matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(rota.y));
				RenderType type = RenderHelper.getTexedOrbSolid(EYE_TEXTURE);
				matrixStackIn.translate(0, 0.98f, 0);
				matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
				RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuilder(), type), new RenderHelper.RenderContext(1.0f, Color.ORIGIN_COLOR, packedLightIn));
			} else {
				matrixStackIn.scale(0.98f, 0.98f, 0.98f);
				RenderType type = RenderHelper.getTexedOrbSolid(EYE_TEXTURE);
				RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuilder(), type), new RenderHelper.RenderContext(1.0f, Color.ORIGIN_COLOR, packedLightIn));
			}
			matrixStackIn.popPose();
		}
		if(ITEM == null)
			ITEM = new ItemStack(ModItems.ARTIFICIAL_LIFE.get());
		ItemStack stack = new ItemStack(ModItems.MAGICK_CORE.get());
		ExtraDataUtil.itemManaData(stack).spellContext().copy(entityIn.spellContext());
		float f3 = ((float)entityIn.tickCount + partialTicks) / 20.0F;
		matrixStackIn.pushPose();
		matrixStackIn.translate(0, entityIn.getBbHeight() * 0.25, 0);
		if(entityIn.deathTime > 0) {
			float f4 = (20 - entityIn.deathTime) * 0.05f;
			if(f4 < 0)
				f4 = 0;
			if(f4 > 1)
				f4 = 1;
			matrixStackIn.scale(1, f4, 1);
		}
		matrixStackIn.pushPose();
		matrixStackIn.mulPose(Vector3f.YP.rotation(f3));
		Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.GROUND, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
		matrixStackIn.popPose();
		matrixStackIn.pushPose();
		matrixStackIn.translate(0, -entityIn.getBbHeight() * 0.5, 0);
		matrixStackIn.scale(4, 4, 4);
		Minecraft.getInstance().getItemRenderer().renderStatic(ITEM, ItemCameraTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
		matrixStackIn.popPose();
		matrixStackIn.popPose();

		if(!RenderHelper.showDebug()) return;
		String information = entityIn.spellContext().toString();
		if(information.isEmpty())  {
			return;
		}
		String[]  debugSpellContext = information.split("\n");
		int  contextLength = 0;
		if(debugSpellContext.length < 1) return;
		for (String s : debugSpellContext) {
			if (s.length() > contextLength)
				contextLength = s.length();
		}
		matrixStackIn.pushPose();
		matrixStackIn.translate(0, entityIn.getBbHeight()*2, 0);
		matrixStackIn.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
		matrixStackIn.scale(0.015f, 0.015f, 0.015f);
		matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));
		matrixStackIn.translate(-contextLength, debugSpellContext.length * -4, 0);
		for (int i = 0; i < debugSpellContext.length; ++i) {
			String tip = debugSpellContext[i];
			if(!tip.isEmpty()) {
				matrixStackIn.pushPose();
				Minecraft.getInstance().font.draw(matrixStackIn, tip, 0, i*8, 0);
				matrixStackIn.popPose();
			}
		}
		matrixStackIn.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(ArtificialLifeEntity entity) {
		return TEXTURE;
	}
}
