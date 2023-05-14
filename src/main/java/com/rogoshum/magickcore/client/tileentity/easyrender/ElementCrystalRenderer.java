package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.tileentity.ElementCrystalTileEntity;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class ElementCrystalRenderer extends EasyTileRenderer<ElementCrystalTileEntity>{
    private Color color = Color.ORIGIN_COLOR;
    private RenderType TYPE;
    public ElementCrystalRenderer(ElementCrystalTileEntity tile) {
        super(tile);
    }

    public void render(RenderParams renderParams) {
        PoseStack matrixStackIn = renderParams.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        RenderHelper.RenderContext renderContext = new RenderHelper.RenderContext(1.0f, color);
        matrixStackIn.translate(0, -0.1, 0);
        matrixStackIn.scale(0.5f, 0.5f, 0.5f);

        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0, 0.0, 0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, TYPE), renderContext);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0, 0.0, -0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, TYPE), renderContext);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, -0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, TYPE), renderContext);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, 0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, TYPE), renderContext);
        matrixStackIn.popPose();
    }

    @Override
    public void update() {
        super.update();
        ResourceLocation crystal = new ResourceLocation(MagickCore.MOD_ID + ":textures/blocks/element_crystal_stage" + tile.age + ".png");
        TYPE = RenderHelper.getTexturedEntityGlowNoise(crystal);
        MagickElement element = MagickRegistry.getElement(tile.eType);
        if(element != null)
            color = element.getRenderer().getPrimaryColor();
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        if(TYPE != null)
            map.put(new RenderMode(TYPE), this::render);
        return map;
    }
}
