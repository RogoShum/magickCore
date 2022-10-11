package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.superentity.AscendantRealmEntity;
import com.rogoshum.magickcore.entity.superentity.RadianceWellEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;

import java.util.HashMap;
import java.util.Iterator;

public class AscendantRealmRenderer extends EasyRenderer<AscendantRealmEntity> {

    @Override
    public void render(AscendantRealmEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        //matrixStackIn.translate(0, -entityIn.getHeight() / 2 + 0.005, 0);
        //Matrix4f positionMatrix = matrixStackIn.getLast().getMatrix();
        int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);
        //entityIn.setGlowing(true);
        if(entityIn.spellContext().element != null && entityIn.spellContext().element.getRenderer() != null) {
            EasyRenderer.renderRift(matrixStackIn, bufferIn, RenderHelper.CRUMBLING, entityIn, 7.0f, entityIn.spellContext().element.getRenderer().getColor()
                    , 1.0f, partialTicks, entityIn.world);
        }
    }
}
