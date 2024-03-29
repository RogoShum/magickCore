package com.rogoshum.magickcore.client.entity.easyrender.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.projectile.ManaArrowEntity;
import com.rogoshum.magickcore.common.entity.projectile.ManaStarEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.function.Consumer;

public class ManaArrowRenderer extends EasyRenderer<ManaArrowEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MagickCore.MOD_ID, "textures/laser/arrow_up.png");
    private static final ResourceLocation TEXTURE_PART = new ResourceLocation(MagickCore.MOD_ID, "textures/laser/arrow_part.png");
    private static final RenderType TYPE = RenderHelper.getTexedOrbGlow(TEXTURE);
    private static final RenderType PART = RenderHelper.getTexedOrbGlow(TEXTURE_PART);

    public ManaArrowRenderer(ManaArrowEntity entity) {
        super(entity);
    }

    @Override
    public void baseOffset(PoseStack matrixStackIn) {
        super.baseOffset(matrixStackIn);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        Vec3 dir = entity.getDeltaMovement().scale(-1).normalize();
        Vec2 rota = getRotationFromVector(dir);
        float scale = 0.75f * entity.getBbWidth();
        params.matrixStack.translate(dir.x * entity.getBbWidth() * 2, dir.y * entity.getBbWidth() * 2, dir.z * entity.getBbWidth() * 2);
        params.matrixStack.mulPose(Vector3f.YP.rotationDegrees(rota.x));
        params.matrixStack.mulPose(Vector3f.ZP.rotationDegrees(rota.y));
        params.matrixStack.scale(scale, scale, scale);
        RenderHelper.renderLaserMid(BufferContext.create(params.matrixStack, params.buffer, TYPE)
        , new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight)
        , 5);
    }

    public void renderPart(RenderParams params) {
        baseOffset(params.matrixStack);
        Vec3 dir = entity.getDeltaMovement().scale(-1).normalize();
        Vec2 rota = getRotationFromVector(dir);
        float scale = 0.5f * entity.getBbWidth();
        params.matrixStack.pushPose();
        params.matrixStack.translate(-dir.x * entity.getBbWidth() * 0.3, -dir.y * entity.getBbWidth() * 0.3, -dir.z * entity.getBbWidth() * 0.3);
        params.matrixStack.mulPose(Vector3f.YP.rotationDegrees(rota.x));
        params.matrixStack.mulPose(Vector3f.ZP.rotationDegrees(rota.y));
        params.matrixStack.mulPose(Vector3f.YP.rotationDegrees(45));
        params.matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
        params.matrixStack.scale(scale, scale, scale);
        RenderHelper.renderStaticParticle(BufferContext.create(params.matrixStack, params.buffer, PART)
                , new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight));
        params.matrixStack.popPose();

        scale = 0.3f * entity.getBbWidth();
        params.matrixStack.translate(dir.x * entity.getBbWidth(), dir.y * entity.getBbWidth(), dir.z * entity.getBbWidth());
        params.matrixStack.mulPose(Vector3f.YP.rotationDegrees(rota.x));
        params.matrixStack.mulPose(Vector3f.ZP.rotationDegrees(rota.y));
        params.matrixStack.mulPose(Vector3f.YP.rotationDegrees(45));
        params.matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
        params.matrixStack.scale(scale, scale, scale);
        RenderHelper.renderStaticParticle(BufferContext.create(params.matrixStack, params.buffer, PART)
                , new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight));
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE), this::render);
        map.put(new RenderMode(PART), this::renderPart);
        return map;
    }
}
