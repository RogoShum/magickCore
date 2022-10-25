package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.client.vertex.VectorHitReaction;
import com.rogoshum.magickcore.entity.pointed.ManaCapacityEntity;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibShaders;
import com.rogoshum.magickcore.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ManaCapacityRenderer extends EasyRenderer<ManaCapacityEntity> {
    private final static RenderType renderType = RenderHelper.getTexedEntity(ModElements.ORIGIN.getRenderer().getParticleSprite());
    private final static RenderType renderType_1 = RenderHelper.getTexedEntityGlint(RenderHelper.blankTex, 1f, 0f);
    private final static RenderType renderType_2 = RenderHelper.getTexedEntityGlint(taken, 1f, 0f);
    float scale;
    int lightmap;

    public ManaCapacityRenderer(ManaCapacityEntity entity) {
        super(entity);
    }

    @Override
    public void update() {
        super.update();
        scale = entity.getWidth() * 0.999f;
        if(entity.ticksExisted == 0)
            scale *= 0;
        else if(entity.ticksExisted < 30)
            scale *= 1f - 1f / (float)entity.ticksExisted;

        lightmap = Minecraft.getInstance().getRenderManager().getPackedLight(entity, Minecraft.getInstance().getRenderPartialTicks());
    }

    public void render(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, renderType_1), new RenderHelper.RenderContext(0.1f, entity.spellContext().element.color(), lightmap));
    }

    public void renderCapacity(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
        float scale = entity.manaCapacity().getMana() / entity.manaCapacity().getMaxMana() * this.scale;
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, renderType_2).useShader(LibShaders.slime), new RenderHelper.RenderContext(0.6f, entity.spellContext().element.color(), lightmap));
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(renderType_1), this::render);
        map.put(new RenderMode(renderType_2, LibShaders.slime), this::renderCapacity);
        return map;
    }
}
