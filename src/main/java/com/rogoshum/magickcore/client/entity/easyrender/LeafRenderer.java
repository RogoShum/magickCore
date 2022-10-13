package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.projectile.BubbleEntity;
import com.rogoshum.magickcore.entity.projectile.LampEntity;
import com.rogoshum.magickcore.entity.projectile.LeafEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

public class LeafRenderer extends EasyRenderer<LeafEntity>{
    private static final ResourceLocation LEAF_0 = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/leaf_0.png");
    private static final ResourceLocation LEAF_1 = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/leaf_1.png");
    private static final ResourceLocation LEAF_2 = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/leaf_2.png");
    @Override
    public void render(LeafEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        matrixStackIn.scale(entityIn.getWidth() * 0.6f, entityIn.getWidth() * 0.6f, entityIn.getWidth() * 0.6f);
        int i = entityIn.getNumber();
        if(i == 1)
            RenderHelper.renderParticle(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedOrbGlow(LEAF_1)), 1.0f, entityIn.spellContext().element.color());
        else if(i == 2)
            RenderHelper.renderParticle(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedOrbGlow(LEAF_2)), 1.0f, entityIn.spellContext().element.color());
        else
            RenderHelper.renderParticle(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedOrbGlow(LEAF_0)), 1.0f, entityIn.spellContext().element.color());
    }
}
