package com.rogoshum.magickcore.client.entity.easyrender.projectile;

import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Consumer;

public class ElementSoulRenderer extends EasyRenderer<ManaProjectileEntity> {
    private static final RenderType TYPE = RenderHelper.getTexturedSphereGlowNoise(sphere_rotate, 1.2f, 0f);
    private RenderType PARTICLE_TYPE;

    public ElementSoulRenderer(ManaProjectileEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        float scale = 0.05f;
        params.matrixStack.pushPose();
        params.matrixStack.scale(scale, scale, scale);
        RenderHelper.renderParticle(BufferContext.create(params.matrixStack, params.buffer, PARTICLE_TYPE).useShader(RenderMode.ShaderList.SLIME_SMALL_SHADER), new RenderHelper.RenderContext(1.0f, ModElements.ORIGIN.primaryColor(), RenderHelper.renderLight));
        params.matrixStack.popPose();
        scale = 0.3f;
        params.matrixStack.scale(scale, scale, scale);
        params.matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
        RenderHelper.renderSphereCache(
                BufferContext.create(params.matrixStack, params.buffer, TYPE).useShader(RenderMode.ShaderList.SLIME_SMALL_SHADER)
                , new RenderHelper.RenderContext(1.0f, entity.spellContext().element().primaryColor(), RenderHelper.renderLight), 1);
    }

    @Override
    public void update() {
        super.update();
        PARTICLE_TYPE = RenderHelper.getTexturedQuadsGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/wither/mist/mist_" + (entity.tickCount % 3) + ".png"));
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE), this::render);
        return map;
    }
}
