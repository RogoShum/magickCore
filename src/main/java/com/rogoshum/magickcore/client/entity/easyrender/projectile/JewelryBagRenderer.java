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
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.vector.Vector3f;

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
                float f3 = ((float)entity.ticksExisted + params.partialTicks) / 20.0F;
                params.matrixStack.rotate(Vector3f.YP.rotation(f3));
                params.matrixStack.push();
                IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(params.buffer);
                Minecraft.getInstance().getItemRenderer().renderItem(context.itemStack, ItemCameraTransforms.TransformType.GROUND, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, params.matrixStack, renderTypeBuffer);
                renderTypeBuffer.finish();
                params.matrixStack.pop();
            }
        }
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE), (renderParams) -> {
            baseOffset(renderParams.matrixStack);
            renderParams.matrixStack.scale(entity.getWidth() * 0.6f, entity.getWidth() * 0.6f, entity.getWidth() * 0.6f);
            RenderHelper.renderParticle(BufferContext.create(renderParams.matrixStack, renderParams.buffer, TYPE), new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight));
        });
        map.put(RenderMode.ORIGIN_RENDER, this::renderItem);
        return map;
    }
}
