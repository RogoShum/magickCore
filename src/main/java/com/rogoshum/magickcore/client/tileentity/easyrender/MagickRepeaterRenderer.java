package com.rogoshum.magickcore.client.tileentity.easyrender;// Made with Blockbench 3.9.2
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.tileentity.MagickRepeaterTileEntity;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.function.Consumer;

public class MagickRepeaterRenderer extends EasyTileRenderer<MagickRepeaterTileEntity> {
    private ResourceLocation magick_repeater = new ResourceLocation(MagickCore.MOD_ID + ":textures/tileentity/magick_repeater.png");
    private final int textureWidth;
    private final int textureHeight;

    private final ModelRenderer bb_main;
    private final ModelRenderer down_r1;
    private final ModelRenderer north_r1;
    private final ModelRenderer west_r1;
    private final ModelRenderer up_r1;
    private final ModelRenderer south_r1;

    public MagickRepeaterRenderer() {
        super(null);
        textureWidth = 64;
        textureHeight = 32;

        bb_main = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
        bb_main.setTextureOffset(38, 0).addBox(-8.0F, -14.0F, -6.0F, 1.0F, 12.0F, 12.0F, 0.0F, false);

        down_r1 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        down_r1.setRotationPoint(0.0F, -8.0F, 0.0F);
        setRotationAngle(down_r1, 1.5708F, 0.0F, -1.5708F);
        down_r1.setTextureOffset(38, 0).addBox(-8.0F, -6.0F, -6.0F, 1.0F, 12.0F, 12.0F, 0.0F, false);

        north_r1 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        north_r1.setRotationPoint(0.0F, -8.0F, 0.0F);
        setRotationAngle(north_r1, 0.0F, -1.5708F, 0.0F);
        north_r1.setTextureOffset(38, 0).addBox(-8.0F, -6.0F, -6.0F, 1.0F, 12.0F, 12.0F, 0.0F, false);

        west_r1 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        west_r1.setRotationPoint(0.0F, -8.0F, 0.0F);
        setRotationAngle(west_r1, -3.1416F, 0.0F, 3.1416F);
        west_r1.setTextureOffset(38, 0).addBox(-8.0F, -6.0F, -6.0F, 1.0F, 12.0F, 12.0F, 0.0F, false);

        up_r1 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        up_r1.setRotationPoint(0.0F, -8.0F, 0.0F);
        setRotationAngle(up_r1, -1.5708F, 0.0F, 1.5708F);
        up_r1.setTextureOffset(38, 0).addBox(-8.0F, -6.0F, -6.0F, 1.0F, 12.0F, 12.0F, 0.0F, false);

        south_r1 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        south_r1.setRotationPoint(0.0F, -8.0F, 0.0F);
        setRotationAngle(south_r1, 0.0F, 1.5708F, 0.0F);
        south_r1.setTextureOffset(38, 0).addBox(-8.0F, -6.0F, -6.0F, 1.0F, 12.0F, 12.0F, 0.0F, false);
    }

    public void render(MagickRepeaterTileEntity entity, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        int light = RenderHelper.renderLight;
        if (entity.getLifeRepeater() != null && entity.getLifeRepeater().useDirection()) {
            matrixStackIn.push();
            matrixStackIn.translate(0, -1, 0);
            RenderType type = RenderHelper.getTexedEntityGlow(magick_repeater);
            IVertexBuilder bufferGlow = bufferIn.getBuffer(type);

            if (entity.isPortTurnOn(Direction.WEST))
                bb_main.render(matrixStackIn, bufferGlow, light, OverlayTexture.NO_OVERLAY);

            matrixStackIn.translate(0, 1.5, 0);
            if (entity.isPortTurnOn(Direction.DOWN))
                up_r1.render(matrixStackIn, bufferGlow, light, OverlayTexture.NO_OVERLAY);

            if (entity.isPortTurnOn(Direction.UP))
                down_r1.render(matrixStackIn, bufferGlow, light, OverlayTexture.NO_OVERLAY);

            if (entity.isPortTurnOn(Direction.EAST))
                west_r1.render(matrixStackIn, bufferGlow, light, OverlayTexture.NO_OVERLAY);

            if (entity.isPortTurnOn(Direction.SOUTH))
                south_r1.render(matrixStackIn, bufferGlow, light, OverlayTexture.NO_OVERLAY);

            if (entity.isPortTurnOn(Direction.NORTH))
                north_r1.render(matrixStackIn, bufferGlow, light, OverlayTexture.NO_OVERLAY);
            bufferIn.finish(type);
            matrixStackIn.pop();
        }
        Color color = ModElements.ORIGIN_COLOR;
        if (entity.getTouchMode() == MagickRepeaterTileEntity.TouchMode.INPUT)
            color = RenderHelper.GREEN;

        if (entity.getTouchMode() == MagickRepeaterTileEntity.TouchMode.OUTPUT)
            color = RenderHelper.RED;
        light = WorldRenderer.getCombinedLight(entity.getWorld(), entity.getPos());
        BufferBuilder buffer = BufferHelper.getBuffer(bufferIn);
        /*
        if (!entity.getDirection().equals(Vector3d.ZERO) && entity.getLifeRepeater() != null && entity.getLifeRepeater().useTileVector()) {
            matrixStackIn.push();
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-entity.getRotation().getY()));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(entity.getRotation().getX()));
            matrixStackIn.scale(0.2f, 0.5f, 0.2f);
            matrixStackIn.translate(0.0, -0.5, -0.0);
            RenderHelper.renderSphere(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedSphereGlow(blank, 0.2f, 0f)), 4, 0.5f, color, light);
            RenderHelper.renderSphere(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedSphereGlow(cylinder_rotate, 0.2f, 0f)), 4, 1.0f, color, RenderHelper.renderLight);
            matrixStackIn.pop();
        } else {
            matrixStackIn.push();
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            RenderHelper.renderSphere(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedSphereGlow(blank, 1f, 0f)), 4, 0.75f, color, RenderHelper.renderLight);
            matrixStackIn.pop();
        }

         */
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        return null;
    }
}