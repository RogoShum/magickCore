package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.client.render.SingleBuffer;
import com.rogoshum.magickcore.common.entity.projectile.PhantomEntity;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.function.Consumer;

public class PhantomRenderer extends EasyRenderer<PhantomEntity> {
    SingleBuffer renderTypeBuffer = new SingleBuffer(RenderHelper::getTexedEntityGlow, RenderHelper.EMPTY_TEXTURE);
    public PhantomRenderer(PhantomEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        params.matrixStack.translate(0, -entity.getBbHeight() * 0.5, 0);
        float f = Mth.lerp(params.partialTicks, entity.getEntity().yRotO, entity.getEntity().getYRot());
        Color color = entity.getColor();
        RenderSystem.setShaderColor(color.r(), color.g(), color.b(), 0.5f);
        Minecraft.getInstance().getEntityRenderDispatcher().render(entity.getEntity(), 0, 0, 0
                , f, params.partialTicks, params.matrixStack, renderTypeBuffer, RenderHelper.halfLight);
        renderTypeBuffer.finish();
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        if(entity.getEntity() == null)
            return map;
        map.put(RenderMode.ORIGIN_RENDER, this::render);
        return map;
    }
}
