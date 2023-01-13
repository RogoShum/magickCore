package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.pointed.ManaCapacityEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class ManaCapacityRenderer extends EasyRenderer<ManaCapacityEntity> {
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
        scale = entity.getBbWidth() * 0.999f;
        if(entity.tickCount == 0)
            scale *= 0;
        else if(entity.tickCount < 30)
            scale *= 1f - 1f / (float)entity.tickCount;

        lightmap = Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(entity, Minecraft.getInstance().getFrameTime());
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
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
        float scale = entity.manaCapacity().getMana() / entity.manaCapacity().getMaxMana() * this.scale;
        matrixStackIn.scale(scale, scale, scale);
        for (int i = 0; i < 2; ++i) {
            if(entity.getMode())
                RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, renderType_2), new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), lightmap));
            else
                RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, renderType_2), new RenderHelper.RenderContext(0.6f, entity.spellContext().element.color(), lightmap));
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
        }
    }

    @Override
    protected void updateSpellContext() {
        super.updateSpellContext();
        String[] strings = new String[1];
        String mana = "§7Mana: " + entity.manaCapacity().getMana() + " / " + entity.manaCapacity().getMaxMana();
        if(contextLength < mana.length())
            contextLength = mana.length();
        strings[0] = mana;
        if(debugSpellContext != null) {
            strings = new String[debugSpellContext.length+1];
            System.arraycopy(debugSpellContext, 0, strings, 0, debugSpellContext.length);
            strings[debugSpellContext.length] = mana;
        }
        debugSpellContext = strings;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(renderType_1), this::render);
        if(entity.getMode())
            map.put(new RenderMode(renderType_2, RenderMode.ShaderList.OPACITY_SHADER), this::renderCapacity);
        else
            map.put(new RenderMode(renderType_2, RenderMode.ShaderList.SLIME_SHADER), this::renderCapacity);
        return map;
    }
}
