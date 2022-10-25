package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderHelper;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.entity.superentity.ChaoReachEntity;
import com.rogoshum.magickcore.lib.LibShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.function.Consumer;

public class ChaosReachRenderer extends EasyRenderer<ChaoReachEntity> {
    RenderType TYPE;

    public ChaosReachRenderer(ChaoReachEntity entity) {
        super(entity);
    }

    @Override
    public void update() {
        super.update();
        if(entity.initial)
            TYPE = RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/shield/element_shield_" + (entity.ticksExisted % 10) + ".png"));
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        float scale = Math.min(1f, (float) (entity.ticksExisted - 30) / 5f) * 0.75f;
        MatrixStack matrixStackIn = params.matrixStack;
        matrixStackIn.scale(entity.getWidth() * scale, entity.getWidth() * scale, entity.getWidth() * scale);
        matrixStackIn.push();
        matrixStackIn.scale(scale + 0.2f * MagickCore.rand.nextFloat(), scale + 0.2f * MagickCore.rand.nextFloat(), scale + 0.2f * MagickCore.rand.nextFloat());
        RenderHelper.renderParticle(BufferContext.create(matrixStackIn, params.buffer, TYPE), new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight));
        matrixStackIn.pop();
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        if(TYPE != null)
            map.put(new RenderMode(TYPE), this::render);
        return map;
    }
}
