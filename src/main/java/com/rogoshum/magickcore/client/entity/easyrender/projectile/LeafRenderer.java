package com.rogoshum.magickcore.client.entity.easyrender.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.projectile.LeafEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Consumer;

public class LeafRenderer extends EasyRenderer<LeafEntity> {
    private static final ResourceLocation LEAF_0 = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/leaf_0.png");
    private static final ResourceLocation LEAF_1 = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/leaf_1.png");
    private static final ResourceLocation LEAF_2 = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/leaf_2.png");
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexedOrbGlow(LEAF_1);
    private static final RenderType RENDER_TYPE_2 = RenderHelper.getTexedOrbGlow(LEAF_2);
    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexedOrbGlow(LEAF_0);

    private RenderType renderType;

    public LeafRenderer(LeafEntity entity) {
        super(entity);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(renderType, RenderMode.ShaderList.BITS_SHADER), (renderParams) -> {
            baseOffset(renderParams.matrixStack);
            renderParams.matrixStack.scale(entity.getBbWidth() * 0.6f, entity.getBbWidth() * 0.6f, entity.getBbWidth() * 0.6f);
            RenderHelper.renderParticle(BufferContext.create(renderParams.matrixStack, renderParams.buffer, renderType), new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight));
        });
        return map;
    }

    @Override
    public void update() {
        super.update();
        int i = entity.getNumber();
        if(i == 1)
            renderType = RENDER_TYPE_1;
        else if(i == 2)
            renderType = RENDER_TYPE_2;
        else
            renderType = RENDER_TYPE_0;
    }
}
