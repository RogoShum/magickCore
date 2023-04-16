package com.rogoshum.magickcore.client.entity.easyrender.projectile;

import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.projectile.JewelryBagEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.child.ItemContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class JewelryBagRenderer extends EasyRenderer<JewelryBagEntity> {
    private static final RenderType TYPE = RenderHelper.getTexedOrbGlow(ModElements.ORIGIN.getRenderer().getParticleSprite());

    public JewelryBagRenderer(JewelryBagEntity entity) {
        super(entity);
    }

    public void renderItem(RenderParams params) {
        baseOffset(params.matrixStack);
        if(entity.spellContext().containChild(LibContext.ITEM)) {
            ItemContext context = entity.spellContext().getChild(LibContext.ITEM);
            if(context.valid()) {
                float f3 = ((float)entity.tickCount + params.partialTicks) / 20.0F;
                params.matrixStack.mulPose(Vector3f.YP.rotation(f3));
                params.matrixStack.pushPose();
                MultiBufferSource.BufferSource renderTypeBuffer = MultiBufferSource.immediate(params.buffer);
                Minecraft.getInstance().getItemRenderer().renderStatic(context.itemStack, ItemTransforms.TransformType.GROUND, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, params.matrixStack, renderTypeBuffer, 0);
                renderTypeBuffer.endBatch();
                params.matrixStack.popPose();
            }
        }
    }

    @Override
    public void update() {
        super.update();
        entity.renderFrame(Minecraft.getInstance().getFrameTime());
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE), (renderParams) -> {
            baseOffset(renderParams.matrixStack);
            renderParams.matrixStack.scale(entity.getBbWidth() * 0.6f, entity.getBbWidth() * 0.6f, entity.getBbWidth() * 0.6f);
            RenderHelper.renderParticle(BufferContext.create(renderParams.matrixStack, renderParams.buffer, TYPE), new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight));
        });
        map.put(RenderMode.ORIGIN_RENDER, this::renderItem);
        return map;
    }
}
