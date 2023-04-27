package com.rogoshum.magickcore.client.entity.easyrender.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.function.Consumer;

public class ElementShieldRenderer extends EasyRenderer<LivingEntity> {
    Color color = Color.ORIGIN_COLOR;
    Color secColor = Color.ORIGIN_COLOR;
    float alpha;
    boolean render;
    RenderType CIRCLE_TYPE = RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_bloom.png"));
    RenderType BLOOM_TYPE;
    ResourceLocation NOISE = new ResourceLocation(MagickCore.MOD_ID + ":textures/noise.png");

    public ElementShieldRenderer(LivingEntity entity) {
        super(entity);
    }

    @Override
    public void baseOffset(PoseStack matrixStackIn) {
        Entity player = Minecraft.getInstance().player;
        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        Vec3 offset = entity == player ? Vec3.ZERO : player.getEyePosition(Minecraft.getInstance().getFrameTime())
                .subtract(new Vec3(x, y, z)).normalize().multiply(entity.getBbWidth() * 1.3d, entity.getBbHeight() * 0.5, entity.getBbWidth() * 1.3d);
        matrixStackIn.translate(x - camX + offset.x, y - camY + entity.getBbHeight() * 0.5f + offset.y, z - camZ + offset.z);
    }

    @Override
    public void update() {
        super.update();
        if(entity == Minecraft.getInstance().player && !Minecraft.getInstance().gameRenderer.getMainCamera().isDetached()) {
            render = false;
            return;
        }
        ExtraDataUtil.entityData(entity).<EntityStateData>execute(LibEntityData.ENTITY_STATE, state -> {
            float value = state.getElementShieldMana();
            if(value > 0.0f) {
                render = true;
                alpha = value / Math.max(1, state.getMaxElementShieldMana());
                if(alpha > 1.0)
                    alpha = 1.0f;
                color = state.getElement().primaryColor();
                secColor = state.getElement().secondaryColor();
            } else
                render = false;
        });
        BLOOM_TYPE = RenderHelper.getTexedEntityGlowNoise(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/shield_2/element_shield_" + (entity.tickCount % 10) + ".png"));
        CIRCLE_TYPE = RenderHelper.getTexedEntityGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/shield/element_shield_" + (entity.tickCount % 10) + ".png"));
    }

    public void renderCircle(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.scale(entity.getBbWidth() * 1.7f, entity.getBbHeight() * 0.9f, entity.getBbWidth() * 1.7f);
        RenderHelper.renderParticle(
                BufferContext.create(matrixStackIn, params.buffer, CIRCLE_TYPE)
                , new RenderHelper.RenderContext(alpha, color, RenderHelper.renderLight));
    }

    public void renderBloom(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.scale(entity.getBbWidth() * 1.65f, entity.getBbHeight() * 0.85f, entity.getBbWidth() * 1.65f);
        RenderHelper.renderParticle(
                BufferContext.create(matrixStackIn, params.buffer, BLOOM_TYPE)
                , new RenderHelper.RenderContext(alpha, secColor, RenderHelper.renderLight));
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        if(!render) return null;
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        //map.put(new RenderMode(CIRCLE_TYPE, LibShaders.opacity), this::renderCircle);
        if(BLOOM_TYPE != null) {
            //map.put(new RenderMode(BLOOM_TYPE), this::renderBloom);
            map.put(new RenderMode(BLOOM_TYPE, RenderMode.ShaderList.BITS_SMALL_SHADER), this::renderBloom);
            map.put(new RenderMode(CIRCLE_TYPE, RenderMode.ShaderList.SLIME_SMALL_SHADER), this::renderCircle);
        }
        return map;
    }
}
