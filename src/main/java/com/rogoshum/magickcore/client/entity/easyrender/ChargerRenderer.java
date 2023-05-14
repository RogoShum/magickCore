package com.rogoshum.magickcore.client.entity.easyrender;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.pointed.ChargeEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.context.child.DirectionContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class ChargerRenderer extends EasyRenderer<ChargeEntity> {
    float scale;
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/charge_circle.png");
    private static final RenderType TYPE = RenderHelper.getTexturedQuadsGlow(ICON);

    public ChargerRenderer(ChargeEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        Vec3 dir = entity.getLookAngle();

        if(entity.getTarget() != null) {
            dir = getEntityRenderVector(entity.getTarget(), params.partialTicks).add(0, entity.getTarget().getBbHeight() * 0.5, 0)
                    .subtract(getEntityRenderVector(params.partialTicks).add(0, entity.getBbHeight() * 0.5, 0));
        } else if(entity.spellContext().containChild(LibContext.DIRECTION))
            dir = entity.spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize().scale(-1);
        Vec2 rota = getRotationFromVector(dir);
        params.matrixStack.mulPose(Vector3f.YP.rotationDegrees(rota.x));
        params.matrixStack.mulPose(Vector3f.ZP.rotationDegrees(rota.y));
        params.matrixStack.pushPose();
        params.matrixStack.translate(0, 0.5, 0);
        params.matrixStack.mulPose(Vector3f.YP.rotationDegrees(entity.tickCount % 60 * 6));
        params.matrixStack.scale(scale, scale, scale);
        BufferContext bufferContext = BufferContext.create(params.matrixStack, params.buffer, TYPE);
        RenderHelper.RenderContext renderContext = new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight);
        params.matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
        RenderHelper.renderStaticParticle(bufferContext, renderContext);
        params.matrixStack.popPose();
        params.matrixStack.mulPose(Vector3f.YP.rotationDegrees(entity.tickCount % 60 * -6));
        params.matrixStack.pushPose();
        params.matrixStack.translate(0, -0.5, 0);
        params.matrixStack.scale(scale, scale, scale);
        params.matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
        RenderHelper.renderStaticParticle(bufferContext, renderContext);
        params.matrixStack.popPose();

        if(entity.getTarget() != null) {
            double length = entity.getTarget().position().distanceTo(entity.position()) - entity.getTarget().getBbWidth();
            params.matrixStack.pushPose();
            params.matrixStack.translate(0, -length, 0);
            params.matrixStack.scale(scale, scale, scale);
            params.matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
            RenderHelper.renderStaticParticle(bufferContext, renderContext);
            params.matrixStack.popPose();

            for(double dis = length * 0.05; dis / length < scale; dis+=dis) {
                params.matrixStack.mulPose(Vector3f.YP.rotationDegrees(entity.tickCount % 10 * -36));
                params.matrixStack.pushPose();
                params.matrixStack.translate(0, -dis, 0);
                params.matrixStack.scale(scale, scale, scale);
                params.matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
                RenderHelper.renderStaticParticle(bufferContext, renderContext);
                params.matrixStack.popPose();
                params.matrixStack.scale(0.8f, 0.8f, 0.8f);
            }
        } else {
            int i = 1;
            for(double dis = 1.; dis < scale+3; dis+=1) {
                params.matrixStack.mulPose(Vector3f.XP.rotationDegrees(entity.tickCount % 1080));
                params.matrixStack.mulPose(Vector3f.ZP.rotationDegrees(entity.tickCount % 2160));
                params.matrixStack.mulPose(Vector3f.YP.rotationDegrees(entity.tickCount % 2160));
                params.matrixStack.scale(0.8f, 0.8f, 0.8f);
                params.matrixStack.pushPose();
                params.matrixStack.translate(0, -dis*i, 0);
                params.matrixStack.scale(scale, scale, scale);
                params.matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
                RenderHelper.renderStaticParticle(bufferContext, renderContext);
                params.matrixStack.popPose();
                i = i - 2*i;
            }
        }
    }

    @Override
    public boolean forceRender() {
        return entity.isAlive();
    }

    @Override
    public void update() {
        super.update();
        scale = Math.max(0.1f, entity.tickCount / (float)entity.spellContext().tick * 0.001f * entity.spellContext().force * entity.tickCount) * 1.5f;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE), this::render);
        return map;
    }
}
