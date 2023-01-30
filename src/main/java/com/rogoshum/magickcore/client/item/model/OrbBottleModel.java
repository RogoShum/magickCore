package com.rogoshum.magickcore.client.item.model;// Made with Blockbench 4.4.3
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity;

public class OrbBottleModel extends EntityModel<Entity> {
	private final ModelPart group;
	private final ModelPart bb_main;

	public OrbBottleModel() {
		texWidth = 64;
		texHeight = 64;

		group = new ModelPart(this);
		group.setPos(0.0F, 24.0F, 0.0F);
		group.texOffs(0, 0).addBox(-5.0F, -7.0F, -5.0F, 10.0F, 7.0F, 10.0F, 0.0F, false);
		group.texOffs(26, 24).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 3.0F, 6.0F, 0.0F, false);
		group.texOffs(30, 0).addBox(-4.0F, -12.0F, -4.0F, 2.0F, 2.0F, 8.0F, 0.0F, false);
		group.texOffs(0, 30).addBox(2.0F, -12.0F, -4.0F, 2.0F, 2.0F, 8.0F, 0.0F, false);
		group.texOffs(12, 30).addBox(-2.0F, -12.0F, 2.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);
		group.texOffs(24, 17).addBox(-2.0F, -12.0F, -4.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);

		bb_main = new ModelPart(this);
		bb_main.setPos(0.0F, 24.0F, 0.0F);
		bb_main.texOffs(0, 17).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 5.0F, 8.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		group.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		bb_main.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}