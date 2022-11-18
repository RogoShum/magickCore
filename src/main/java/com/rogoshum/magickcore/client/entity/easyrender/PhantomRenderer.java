package com.rogoshum.magickcore.client.entity.easyrender;

import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.client.render.SingleBuffer;
import com.rogoshum.magickcore.common.entity.projectile.PhantomEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.function.Consumer;

public class PhantomRenderer extends EasyRenderer<PhantomEntity> {
    SingleBuffer renderTypeBuffer = new SingleBuffer(RenderHelper::getTexedEntityGlow, RenderHelper.EMPTY_TEXTURE);
    public PhantomRenderer(PhantomEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        params.matrixStack.translate(0, -entity.getHeight() * 0.5, 0);
        float f = MathHelper.lerp(params.partialTicks, entity.getEntity().prevRotationYaw, entity.getEntity().rotationYaw);
        Minecraft.getInstance().getRenderManager().renderEntityStatic(entity.getEntity(), 0, 0, 0
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
