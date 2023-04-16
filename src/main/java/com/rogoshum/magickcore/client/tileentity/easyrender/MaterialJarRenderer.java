package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.tileentity.ElementCrystalTileEntity;
import com.rogoshum.magickcore.common.tileentity.MaterialJarTileEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Consumer;

public class MaterialJarRenderer extends EasyTileRenderer<MaterialJarTileEntity>{
    private static final RenderType TYPE = RenderHelper.getTexedOrb(RenderHelper.blankTex);
    private int light;

    public MaterialJarRenderer(MaterialJarTileEntity tile) {
        super(tile);
    }

    public void render(RenderParams renderParams) {
        PoseStack matrixStackIn = renderParams.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        matrixStackIn.scale(0.6f, 0.99f, 0.6f);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, buffer, TYPE)
                , new RenderHelper.RenderContext(0.2f, Color.ORIGIN_COLOR, light));
        matrixStackIn.scale(0.9f, 0.9f, 0.9f);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, buffer, TYPE)
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
