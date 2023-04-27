package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.ElementRenderer;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.superentity.SilenceSquallEntity;
import com.rogoshum.magickcore.common.lib.LibElements;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.function.Consumer;

public class SilenceSqualRenderer extends EasyRenderer<SilenceSquallEntity> {
    protected static final ResourceLocation wind = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/wind.png");
    float alpha;
    final ElementRenderer STASIS_RENDER = MagickCore.proxy.getElementRender(LibElements.STASIS);
    RenderType TYPE = RenderHelper.getTexedOrbGlow(STASIS_RENDER.getCycleTexture());
    RenderType SPHERE = RenderHelper.getTexedCylinderGlint(sphere_rotate, 1f, 0f, 0.5f, 10.0f);
    float preRotate;
    float postRotate;
    float rotate;
    ElementRenderer renderer;

    public SilenceSqualRenderer(SilenceSquallEntity entity) {
        super(entity);
    }

    @Override
    public void update() {
        super.update();
        alpha = Math.min(1.0f, (float)entity.tickCount / 100f);
        preRotate = entity.tickCount % 50;
        postRotate = (entity.tickCount + 1) % 50;
        rotate = Mth.lerp(Minecraft.getInstance().getFrameTime(), preRotate, postRotate);
        renderer = entity.spellContext().element.getRenderer();
    }

    public void renderCore(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;

        matrixStackIn.scale(1.0f, 3.6f, 1.0f);
        RenderHelper.renderParticle(BufferContext.create(matrixStackIn, bufferIn, TYPE)
                , new RenderHelper.RenderContext(alpha, renderer.getPrimaryColor(), RenderHelper.renderLight));
        matrixStackIn.scale(0.9f, 0.9f, 0.9f);
        RenderHelper.renderParticle(BufferContext.create(matrixStackIn, bufferIn, TYPE)
                , new RenderHelper.RenderContext(0.5f * alpha, renderer.getSecondaryColor(), RenderHelper.renderLight));
    }

    public void render(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        BufferBuilder bufferIn = params.buffer;
        baseOffset(matrixStackIn);
        //matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(360f * (rotate / 4)));

        RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(7.0f, 4.0f, 2
                , 4f, 16
                , 0.0f, 0.8f * alpha, 0.8f, renderer.getPrimaryColor());

        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, SPHERE)
                , context);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE, RenderMode.ShaderList.BITS_SHADER), this::renderCore);
        map.put(new RenderMode(SPHERE, RenderMode.ShaderList.BITS_SHADER), this::render);
        return map;
    }
}
