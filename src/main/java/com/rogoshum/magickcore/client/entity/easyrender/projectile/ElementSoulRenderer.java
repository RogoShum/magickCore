package com.rogoshum.magickcore.client.entity.easyrender.projectile;

import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.client.vertex.VectorHitReaction;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;

public class ElementSoulRenderer extends EasyRenderer<ManaProjectileEntity> {
    Queue<Queue<RenderHelper.VertexAttribute>> SPHERE;
    private static final RenderType TYPE = RenderHelper.getTexedSphereGlowNoise(sphere_rotate, 1.2f, 0f);
    private RenderType PARTICLE_TYPE;
    protected final HashMap<Integer, VectorHitReaction> hitReactions = new HashMap<>();

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
        if(SPHERE != null) {
            RenderHelper.renderSphere(
                    BufferContext.create(params.matrixStack, params.buffer, TYPE).useShader(RenderMode.ShaderList.SLIME_SMALL_SHADER)
                    , SPHERE);
        }
    }

    @Override
    public void update() {
        super.update();
        hitReactions.values().removeIf((reaction) -> {
            reaction.tick();
            return reaction.isInvalid();
        });
        Vec3 rand = new Vec3(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne());
        hitReactions.put(entity.tickCount, new VectorHitReaction(rand, 0.4F, 0.07F));
        PARTICLE_TYPE = RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/wither/mist/mist_" + (entity.tickCount % 3) + ".png"));
        SPHERE = RenderHelper.drawSphere(10, new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight),
                new RenderHelper.VertexContext(hitReactions.values().stream().filter(Objects::nonNull).toArray(VectorHitReaction[]::new), true, "", 2.10f));
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        if(SPHERE != null)
            map.put(RenderMode.ORIGIN_RENDER, this::render);
        return map;
    }
}
