package com.rogoshum.magickcore.client.item.model;// Made with Blockbench 4.4.3
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rogoshum.magickcore.MagickCore;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class OrbBottleModel extends EntityModel<Entity> {
	private final ModelPart group;
	private final ModelPart bb_main;

	public OrbBottleModel() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		CubeListBuilder cubeListBuilder = CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -7.0F, -5.0F, 10.0F, 7.0F, 10.0F, false);
		cubeListBuilder.texOffs(0, 0).addBox(-5.0F, -7.0F, -5.0F, 10.0F, 7.0F, 10.0F, false);
		cubeListBuilder.texOffs(26, 24).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 3.0F, 6.0F, false);
		cubeListBuilder.texOffs(30, 0).addBox(-4.0F, -12.0F, -4.0F, 2.0F, 2.0F, 8.0F, false);
		cubeListBuilder.texOffs(0, 30).addBox(2.0F, -12.0F, -4.0F, 2.0F, 2.0F, 8.0F, false);
		cubeListBuilder.texOffs(12, 30).addBox(-2.0F, -12.0F, 2.0F, 4.0F, 2.0F, 2.0F, false);
		cubeListBuilder.texOffs(24, 17).addBox(-2.0F, -12.0F, -4.0F, 4.0F, 2.0F, 2.0F, false);
		partdefinition.addOrReplaceChild("group", cubeListBuilder, PartPose.offset(0.0F, 24.0F, 0.0F));
		partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 17).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 5.0F, 8.0F, false), PartPose.offset(0.0F, 24.0F, 0.0F));
		LayerDefinition layerDefinition = LayerDefinition.create(meshdefinition, 64, 64);
		ModelPart root = layerDefinition.bakeRoot();
		this.group = root.getChild("group");
		this.bb_main = root.getChild("bb_main");
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