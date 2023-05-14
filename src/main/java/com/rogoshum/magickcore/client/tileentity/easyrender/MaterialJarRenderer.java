package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.tileentity.MaterialJarTileEntity;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.function.Consumer;

public class MaterialJarRenderer extends EasyTileRenderer<MaterialJarTileEntity>{
    private static final RenderType TYPE = RenderHelper.getTexturedQuadsTranslucent(RenderHelper.BLANK_TEX);
    private int light;

    public MaterialJarRenderer(MaterialJarTileEntity tile) {
        super(tile);
    }

    public void render(RenderParams renderParams) {
        PoseStack matrixStackIn = renderParams.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        matrixStackIn.scale(0.6f, 0.99f, 0.6f);
        RenderHelper.renderCubeCache(BufferContext.create(matrixStackIn, buffer, TYPE)
                , new RenderHelper.RenderContext(0.2f, Color.ORIGIN_COLOR, light));
        matrixStackIn.scale(0.9f, 0.9f, 0.9f);
        RenderHelper.renderCubeCache(BufferContext.create(matrixStackIn, buffer, TYPE)
                , new RenderHelper.RenderContext(0.05f, Color.ORIGIN_COLOR, light));
    }

    @Override
    public void update() {
        super.update();
        light = tile.getLevel().getLightEmission(tile.getBlockPos());
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE), this::render);
        return map;
    }
}
