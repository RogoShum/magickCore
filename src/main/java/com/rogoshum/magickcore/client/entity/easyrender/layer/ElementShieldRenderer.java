package com.rogoshum.magickcore.client.entity.easyrender.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.lib.LibShaders;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

import java.util.HashMap;
import java.util.function.Consumer;

public class ElementShieldRenderer extends EasyRenderer<LivingEntity> {
    Color color = Color.ORIGIN_COLOR;
    float alpha;
    boolean render;
    RenderType CIRCLE_TYPE = RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_bloom.png"));
    RenderType BLOOM_TYPE;

    public ElementShieldRenderer(LivingEntity entity) {
        super(entity);
    }

    @Override
    public void baseOffset(MatrixStack matrixStackIn) {
        Entity player = Minecraft.getInstance().player;
        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        Vector3d offset = entity == player ? Vector3d.ZERO : player.getEyePosition(Minecraft.getInstance().getRenderPartialTicks())
                .subtract(new Vector3d(x, y, z)).normalize().mul(entity.getWidth() * 1.3d, entity.getHeight() * 0.5, entity.getWidth() * 1.3d);
        matrixStackIn.translate(x - camX + offset.x, y - camY + entity.getHeight() * 0.5f + offset.y, z - camZ + offset.z);
    }

    @Override
    public void update() {
        super.update();
        if(entity == Minecraft.getInstance().player && !Minecraft.getInstance().gameRenderer.getActiveRenderInfo().isThirdPerson()) {
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
                color = state.getElement().getRenderer().getColor();
            } else
                render = false;
        });
        BLOOM_TYPE = RenderHelper.getTexedEntityGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/shield_2/element_shield_" + (entity.ticksExisted % 10) + ".png"));
        CIRCLE_TYPE = RenderHelper.getTexedEntityGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/shield/element_shield_" + (entity.ticksExisted % 10) + ".png"));
    }

    public void renderCircle(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.scale(entity.getWidth() * 1.68f, entity.getHeight() * 0.89f, entity.getWidth() * 1.68f);
        RenderHelper.renderParticle(
                BufferContext.create(matrixStackIn, params.buffer, CIRCLE_TYPE)
                , new RenderHelper.RenderContext(alpha, color, RenderHelper.renderLight));
    }

    public void renderBloom(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.scale(entity.getWidth() * 1.7f, entity.getHeight() * 0.9f, entity.getWidth() * 1.7f);
        RenderHelper.renderParticle(
                BufferContext.create(matrixStackIn, params.buffer, BLOOM_TYPE)
                , new RenderHelper.RenderContext(alpha * 0.8f, color, RenderHelper.renderLight));
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        if(!render) return null;
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        //map.put(new RenderMode(CIRCLE_TYPE, LibShaders.opacity), this::renderCircle);
        if(BLOOM_TYPE != null) {
            //map.put(new RenderMode(BLOOM_TYPE), this::renderBloom);
            map.put(new RenderMode(BLOOM_TYPE, RenderMode.ShaderList.OPACITY_SHADER), this::renderBloom);
            map.put(new RenderMode(CIRCLE_TYPE), this::renderCircle);
        }
        return map;
    }
}
