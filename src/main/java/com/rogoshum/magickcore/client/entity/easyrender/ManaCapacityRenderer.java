package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.entity.pointed.ManaCapacityEntity;
import com.rogoshum.magickcore.lib.LibShaders;
import com.rogoshum.magickcore.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Vector3f;

public class ManaCapacityRenderer extends EasyRenderer<ManaCapacityEntity>{
    private final Color color = Color.create(0.6f, 0.8f, 1.0f);

    @Override
    public void render(ManaCapacityEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        if(entityIn.spellContext().element != null && entityIn.spellContext().element.getRenderer() != null) {
            float scale = entityIn.getWidth() * 0.999f;
            if(entityIn.ticksExisted == 0)
                scale *= 0;
            else if(entityIn.ticksExisted < 30)
                scale *= 1f - 1f / (float)entityIn.ticksExisted;

            int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);
            matrixStackIn.scale(scale, scale, scale);
            VectorHitReaction[] test = {};
            RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedEntityGlint(RenderHelper.blankTex, 1f, 0f)), entityIn.spellContext().element.color(), packedLightIn, 0.1f);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
            scale = entityIn.manaCapacity().getMana() / entityIn.manaCapacity().getMaxMana();
            matrixStackIn.scale(scale, scale, scale);
            RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedEntityGlint(taken, 1f, 0f)).useShader(LibShaders.slime), entityIn.spellContext().element.color(), packedLightIn, 0.6f);
        }
    }
}
