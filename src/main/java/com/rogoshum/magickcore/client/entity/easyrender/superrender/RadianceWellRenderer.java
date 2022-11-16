package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.superentity.RadianceWellEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibShaders;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.Queue;
import java.util.function.Consumer;

public class RadianceWellRenderer extends EasyRenderer<RadianceWellEntity> {
    int packedLightIn;
    float alphaS;
    float alphaC;
    final RenderType TYPE = RenderHelper.getTexedSphereGlow(blank, 1f, 0f);
    final RenderType INNER_TYPE = RenderHelper.getTexedCylinderGlint(cylinder_bloom, 2f, 0f);
    final RenderType OUTER_TYPE = RenderHelper.getTexedCylinderGlint(cylinder_bloom, 1f, 0f);
    RenderHelper.RenderContext RENDER_5;
    RenderHelper.RenderContext RENDER_6;
    Queue<Queue<RenderHelper.VertexAttribute>> CYLINDER_INNER;
    Queue<Queue<RenderHelper.VertexAttribute>> CYLINDER_OUTER;

    public RadianceWellRenderer(RadianceWellEntity entity) {
        super(entity);
    }

    public void renderSword(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        matrixStackIn.translate(x - camX, y - camY + entity.getHeight() / 2, z - camZ);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(225));
        matrixStackIn.translate(0.4, 0.1, 0);
        matrixStackIn.scale(2.5f, 2.5f, 2.5f);
        ItemStack stack = new ItemStack(Items.GOLDEN_SWORD);
        IBakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, null, null);
        IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(bufferIn);
        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, renderTypeBuffer, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
        renderTypeBuffer.finish();
    }

    public void renderSphere(RenderParams params) {
        baseOffset(params.matrixStack);
        MatrixStack matrixStackIn = params.matrixStack;
        BufferBuilder bufferIn = params.buffer;
        BufferContext context = BufferContext.create(matrixStackIn, bufferIn, TYPE);
        float scale = 1.4f;
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderSphere(context, RENDER_5, 12);
        scale = 0.7f;
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderSphere(context, RENDER_5, 12);
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderSphere(context, RENDER_6, 12);
    }

    public void renderInnerLight(RenderParams params) {
        baseOffset(params.matrixStack);
        MatrixStack matrixStackIn = params.matrixStack;
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.translate(0, -entity.getHeight() * 2, 0);
        matrixStackIn.scale(entity.getWidth() * 0.5f, entity.getHeight() * 1.2f, entity.getWidth() * 0.5f);
        if(CYLINDER_INNER != null)
            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, INNER_TYPE), CYLINDER_INNER);
    }

    public void renderOuterLight(RenderParams params) {
        baseOffset(params.matrixStack);
        MatrixStack matrixStackIn = params.matrixStack;
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.translate(0, -entity.getHeight() * 2, 0);
        matrixStackIn.scale(entity.getWidth() * 0.501f, entity.getHeight() * 1.2f, entity.getWidth() * 0.501f);
        if(CYLINDER_OUTER != null)
            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, OUTER_TYPE), CYLINDER_OUTER);
    }

    @Override
    public void baseOffset(MatrixStack matrixStackIn) {
        super.baseOffset(matrixStackIn);
        matrixStackIn.translate(0, entity.getHeight() * 0.5, 0);
        matrixStackIn.scale(0.5f, 0.5f, 0.5f);
    }

    @Override
    public void update() {
        super.update();
        packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entity, Minecraft.getInstance().getRenderPartialTicks());
        alphaS = Math.min(1f, (float)entity.ticksExisted / 5f);
        alphaC = Math.min(1f, (float) entity.ticksExisted / 20f);
        RENDER_5 = new RenderHelper.RenderContext(0.5f * alphaS, entity.spellContext().element.color(), packedLightIn);
        RENDER_6 = new RenderHelper.RenderContext(0.6f * alphaS, Color.ORIGIN_COLOR, packedLightIn);

        if(entity.initial) {
            RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(2f, 2f, 1, 8.0f, 16
                    , 0, alphaC * 0.8f, 0.4f, ModElements.ORIGIN.getRenderer().getColor());
            CYLINDER_INNER = RenderHelper.drawCylinder(context, entity.getHitReactions(), 0.2f);

            context = new RenderHelper.CylinderContext(2f, 2f, 1, 8.0f, 16
                    , 0, alphaC, 0.4f, entity.spellContext().element.getRenderer().getColor());
            CYLINDER_OUTER = RenderHelper.drawCylinder(context, entity.getHitReactions(), 0.2f);
        }
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(RenderMode.ORIGIN_RENDER, this::renderSword);
        map.put(new RenderMode(TYPE, LibShaders.slime), this::renderSphere);
        if(entity.initial) {
            map.put(new RenderMode(INNER_TYPE, LibShaders.slime), this::renderInnerLight);
            map.put(new RenderMode(OUTER_TYPE, LibShaders.slime), this::renderOuterLight);
        }
        return map;
    }
}
