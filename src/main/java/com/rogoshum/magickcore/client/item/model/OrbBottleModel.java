package com.rogoshum.magickcore.client.item.model;// Made with Blockbench 4.4.3
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class OrbBottleModel extends EntityModel<Entity> {
	private final ModelRenderer group;
	private final ModelRenderer bb_main;

	public OrbBottleModel() {
		textureWidth = 64;
		textureHeight = 64;

		group = new ModelRenderer(this);
		group.setRotationPoint(0.0F, 24.0F, 0.0F);
		group.setTextureOffset(0, 0).addBox(-5.0F, -7.0F, -5.0F, 10.0F, 7.0F, 10.0F, 0.0F, false);
		group.setTextureOffset(26, 24).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 3.0F, 6.0F, 0.0F, false);
		group.setTextureOffset(30, 0).addBox(-4.0F, -12.0F, -4.0F, 2.0F, 2.0F, 8.0F, 0.0F, false);
		group.setTextureOffset(0, 30).addBox(2.0F, -12.0F, -4.0F, 2.0F, 2.0F, 8.0F, 0.0F, false);
		group.setTextureOffset(12, 30).addBox(-2.0F, -12.0F, 2.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);
		group.setTextureOffset(24, 17).addBox(-2.0F, -12.0F, -4.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);

		bb_main = new ModelRenderer(this);
		bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
		bb_main.setTextureOffset(0, 17).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 5.0F, 8.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		group.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		bb_main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}