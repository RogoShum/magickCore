package com.rogoshum.magickcore.client.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.model.EntityHunterModel;
import com.rogoshum.magickcore.common.entity.living.ArtificialLifeEntity;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class ArtificialLifeEntityRenderer extends EntityRenderer<ArtificialLifeEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(MagickCore.MOD_ID + ":textures/entity/artificial_life.png");
	private static ItemStack ITEM;

	public ArtificialLifeEntityRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	protected int getBlockLight(ArtificialLifeEntity entityIn, BlockPos partialTicks) {
		return 15;
	}

	@Override
	public void render(ArtificialLifeEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		if(ITEM == null)
			ITEM = new ItemStack(ModItems.ARTIFICIAL_LIFE.get());
		ItemStack stack = new ItemStack(ModItems.MAGICK_CORE.get());
		ExtraDataUtil.itemManaData(stack).spellContext().copy(entityIn.spellContext());
		float f3 = ((float)entityIn.ticksExisted + partialTicks) / 20.0F;
		matrixStackIn.translate(0, entityIn.getHeight() * 0.25, 0);
		if(entityIn.deathTime > 0) {
			float f4 = (20 - entityIn.deathTime) * 0.05f;
			if(f4 < 0)
				f4 = 0;
			if(f4 > 1)
				f4 = 1;
			matrixStackIn.scale(1, f4, 1);
		}
		matrixStackIn.push();
		matrixStackIn.rotate(Vector3f.YP.rotation(f3));
		Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
		matrixStackIn.pop();
		matrixStackIn.push();
		matrixStackIn.translate(0, -entityIn.getHeight() * 0.5, 0);
		matrixStackIn.scale(4, 4, 4);
		Minecraft.getInstance().getItemRenderer().renderItem(ITEM, ItemCameraTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
		matrixStackIn.pop();

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
		matrixStackIn.push();
		matrixStackIn.translate(0, entityIn.getHeight()*2, 0);
		matrixStackIn.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
		matrixStackIn.scale(0.015f, 0.015f, 0.015f);
		matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180));
		matrixStackIn.translate(-contextLength, debugSpellContext.length * -4, 0);
		for (int i = 0; i < debugSpellContext.length; ++i) {
			String tip = debugSpellContext[i];
			if(!tip.isEmpty()) {
				matrixStackIn.push();
				Minecraft.getInstance().fontRenderer.drawString(matrixStackIn, tip, 0, i*8, 0);
				matrixStackIn.pop();
			}
		}
		matrixStackIn.pop();
	}

	@Override
	public ResourceLocation getEntityTexture(ArtificialLifeEntity entity) {
		return TEXTURE;
	}
}
