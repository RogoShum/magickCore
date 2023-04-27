package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.living.QuadrantCrystalEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Consumer;

public class QuadrantEntityRenderer extends EasyRenderer<QuadrantCrystalEntity> {
    float scale;
    private static final RenderType TYPE = RenderHelper.getEntityQuadrant(RenderHelper.blankTex);
    private static final RenderType CRYSTAL_TYPE = RenderHelper.getTexedOrbGlint(RenderHelper.SPHERE_ROTATE, 1.0f, 1.0f);
    private static final RenderType EYE_TYPE = RenderHelper.getTexedOrbTransparency(new ResourceLocation("textures/item/ender_eye.png"));

    public QuadrantEntityRenderer(QuadrantCrystalEntity entity) {
        super(entity);
    }

    public void renderQuadrant(RenderParams params) {
        PoseStack model = RenderHelper.getModelMatrix();
        model.pushPose();
        model.translate(x, y+ entity.getBbHeight() * 0.5, z);
        RenderHelper.renderCubeDynamic(
                BufferContext.create(model, params.buffer, TYPE)
                , new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight), scale);
        model.popPose();
    }

    public void render(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        float scale = entity.spellContext().force*0.5f;
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.scale(entity.getBbWidth() * 0.5f, entity.getBbWidth(), entity.getBbWidth() * 0.5f);
        matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(45));
        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(45));
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, buffer, CRYSTAL_TYPE)
                , new RenderHelper.RenderContext(0.8f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight));
        matrixStackIn.scale(0.8f, 0.8f, 0.8f);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, buffer, CRYSTAL_TYPE)
                , new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight));
    }

    public void renderEye(RenderParams params) {
        baseOffset(params.matrixStack);
        float scale = entity.spellContext().force*0.5f;
        params.matrixStack.scale(scale, scale, scale);
        params.matrixStack.scale(entity.getBbWidth() * 0.2f, entity.getBbWidth() * 0.2f, entity.getBbWidth() * 0.2f);
        RenderHelper.renderParticle(BufferContext.create(params.matrixStack, params.buffer, EYE_TYPE), new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight));
    }

    @Override
    public void update() {
        super.update();
        scale = entity.range()*2;
    }

    @Override
    public boolean forceRender() {
        return true;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE, RenderMode.ShaderList.BITS_SMALL_SHADER), this::renderQuadrant);
        map.put(new RenderMode(CRYSTAL_TYPE, RenderMode.ShaderList.BITS_SMALL_SHADER), this::render);
        map.put(new RenderMode(EYE_TYPE), this::renderEye);
        return map;
    }
}
