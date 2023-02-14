package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.common.tileentity.ElementWoolTileEntity;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Consumer;

public class ElementWoolRenderer extends EasyTileRenderer<ElementWoolTileEntity>{
    protected static final ResourceLocation wool = new ResourceLocation("textures/block/white_wool.png");

    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexedOrbSolid(wool);
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexedOrbGlint(RenderHelper.SPHERE_ROTATE, 0.1f, 0f);
    private Color color = Color.ORIGIN_COLOR;

    public ElementWoolRenderer(ElementWoolTileEntity tile) {
        super(tile);
    }

    public void render(RenderParams renderParams) {
        PoseStack matrixStackIn = renderParams.matrixStack;
        baseOffset(matrixStackIn);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, renderParams.buffer, RENDER_TYPE_0), new RenderHelper.RenderContext(1.0f, color, RenderHelper.renderLight));
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, renderParams.buffer, RENDER_TYPE_1), new RenderHelper.RenderContext(1.0f, color, RenderHelper.renderLight));
    }

    @Override
    public void update() {
        super.update();
        MagickElement element = MagickRegistry.getElement(tile.eType);
        if(element != null)
            color = element.getRenderer().getColor();
    }

    public void renderLight(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(RenderMode.ORIGIN_RENDER, this::render);
        return map;
    }
}
