package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.superentity.RadianceWellEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.Queue;
import java.util.function.Consumer;

public class RadianceWellRenderer extends EasyRenderer<RadianceWellEntity> {
    int packedLightIn;
    float alphaS;
    float alphaC;
    final RenderType TYPE = RenderHelper.getTexedSphereGlow(blank, 1f, 0f);
    final RenderType INNER_TYPE = RenderHelper.getTexedCylinderGlint(cylinder_bloom, 0.2f, 0f);
    final RenderType OUTER_TYPE = RenderHelper.getTexedCylinderGlint(cylinder_bloom, 0.15f, 0f);
    RenderHelper.RenderContext RENDER_5;
    RenderHelper.RenderContext RENDER_6;
    Queue<Queue<RenderHelper.VertexAttribute>> CYLINDER_INNER;
    Queue<Queue<RenderHelper.VertexAttribute>> CYLINDER_OUTER;

    public RadianceWellRenderer(RadianceWellEntity entity) {
        super(entity);
    }

    public void renderSword(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        matrixStackIn.translate(x - camX, y - camY + entity.getBbHeight() / 2, z - camZ);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(225));
        matrixStackIn.translate(0.4, 0.1, 0);
        matrixStackIn.scale(2.5f, 2.5f, 2.5f);
        ItemStack stack = new ItemStack(Items.GOLDEN_SWORD);
        BakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0);
        MultiBufferSource.BufferSource renderTypeBuffer = MultiBufferSource.immediate(bufferIn);
        Minecraft.getInstance().getItemRenderer().render(stack, ItemTransforms.TransformType.GROUND, false, matrixStackIn, renderTypeBuffer, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
        renderTypeBuffer.endBatch();
    }

    public void renderSphere(RenderParams params) {
        baseOffset(params.matrixStack);
        PoseStack matrixStackIn = params.matrixStack;
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
        PoseStack matrixStackIn = params.matrixStack;
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.translate(0, -entity.getBbHeight() * 2, 0);
        matrixStackIn.scale(entity.getBbWidth() * 2f, entity.getBbHeight() * 2f, entity.getBbWidth() * 2f);
        if(CYLINDER_INNER != null)
            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, INNER_TYPE), CYLINDER_INNER);
    }

    public void renderOuterLight(RenderParams params) {
        baseOffset(params.matrixStack);
        PoseStack matrixStackIn = params.matrixStack;
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.translate(0, -entity.getBbHeight() * 2, 0);
        matrixStackIn.scale(entity.getBbWidth() * 2.001f, entity.getBbHeight() * 2f, entity.getBbWidth() * 2.001f);
        if(CYLINDER_OUTER != null)
            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, OUTER_TYPE), CYLINDER_OUTER);
    }

    @Override
    public void baseOffset(PoseStack matrixStackIn) {
        super.baseOffset(matrixStackIn);
        matrixStackIn.translate(0, entity.getBbHeight() * 0.5, 0);
        matrixStackIn.scale(0.5f, 0.5f, 0.5f);
    }

    @Override
    public void update() {
        super.update();
        packedLightIn = Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(entity, Minecraft.getInstance().getFrameTime());
        alphaS = Math.min(1f, (float)entity.tickCount / 5f);
        alphaC = Math.min(1f, (float) entity.tickCount / 20f);
        RENDER_5 = new RenderHelper.RenderContext(0.5f * alphaS
                , entity.spellContext().element.primaryColor(), packedLightIn);
        RENDER_6 = new RenderHelper.RenderContext(0.6f * alphaS
                , Color.ORIGIN_COLOR, packedLightIn);

        if(entity.initial) {
            RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(0.5f, 0.5f, 1, 2f, 12
                    , 0, 0.8f * alphaC, 1.5f, ModElements.ORIGIN.getRenderer().getSecondaryColor());
            CYLINDER_INNER = RenderHelper.drawCylinder(context, entity.getHitReactions(), 0.2f);

            context = new RenderHelper.CylinderContext(0.5f, 0.5f, 1, 2f, 12
                    , 0, alphaC, 1f, entity.spellContext().element.getRenderer().getPrimaryColor());
            CYLINDER_OUTER = RenderHelper.drawCylinder(context, entity.getHitReactions(), 0.2f);
        }
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(RenderMode.ORIGIN_RENDER, this::renderSword);
        map.put(new RenderMode(TYPE, RenderMode.ShaderList.BITS_SHADER), this::renderSphere);
        if(entity.initial) {
            map.put(new RenderMode(INNER_TYPE, RenderMode.ShaderList.BITS_SHADER), this::renderInnerLight);
            map.put(new RenderMode(OUTER_TYPE, RenderMode.ShaderList.BITS_SHADER), this::renderOuterLight);
        }
        return map;
    }
}
