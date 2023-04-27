package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.superentity.ChaoReachEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

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
            TYPE = RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/shield/element_shield_" + (entity.tickCount % 10) + ".png"));
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        float scale = Math.min(1f, (float) (entity.tickCount - 30) / 5f) * 0.75f;
        PoseStack matrixStackIn = params.matrixStack;
        matrixStackIn.scale(entity.getBbWidth() * scale, entity.getBbWidth() * scale, entity.getBbWidth() * scale);
        matrixStackIn.pushPose();
        matrixStackIn.scale(scale + 0.2f * MagickCore.rand.nextFloat(), scale + 0.2f * MagickCore.rand.nextFloat(), scale + 0.2f * MagickCore.rand.nextFloat());
        RenderHelper.renderParticle(BufferContext.create(matrixStackIn, params.buffer, TYPE), new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight));
        matrixStackIn.popPose();
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        if(TYPE != null)
            map.put(new RenderMode(TYPE), this::render);
        return map;
    }
}
