package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.superentity.RadianceWellEntity;
import com.rogoshum.magickcore.common.init.ModElements;
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
import java.util.function.Consumer;

public class RadianceWellRenderer extends EasyRenderer<RadianceWellEntity> {
    int packedLightIn;
    float alphaS;
    float alphaC;
    final RenderType TYPE = RenderHelper.getTexturedEntityGlint(blank, 1f, 0f);
    final RenderType INNER_TYPE = RenderHelper.getTexturedUniGlint(cylinder_bloom, 1f, 0f, 0.025f, 0.05f);
    final RenderType OUTER_TYPE = RenderHelper.getTexturedUniGlint(cylinder_bloom, 1.1f, 0f, 0.025f, 0.05f);
    RenderHelper.RenderContext RENDER_5;
    RenderHelper.CylinderContext CYLINDER_INNER = new RenderHelper.CylinderContext(0.5f, 0.5f, 1, 3, 2f
            , 0, 0.8f, 1.5f);
    RenderHelper.CylinderContext CYLINDER_OUTER = new RenderHelper.CylinderContext(0.5f, 0.5f, 1, 3, 2f
            , 0, 1, 1f);

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
        RenderHelper.renderSphereCache(context, RENDER_5, 0);
        scale = 0.7f;
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderSphereCache(context, RENDER_5, 0);
    }

    public void renderInnerLight(RenderParams params) {
        baseOffset(params.matrixStack);
        PoseStack matrixStackIn = params.matrixStack;
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.translate(0, -entity.getBbHeight() * 2, 0);
        matrixStackIn.scale(entity.getBbWidth() * 2f, entity.getBbHeight() * 2f, entity.getBbWidth() * 2f);
        RenderHelper.renderCylinderCache(BufferContext.create(matrixStackIn, bufferIn, INNER_TYPE), CYLINDER_INNER
                , new RenderHelper.RenderContext(alphaC, entity.spellContext().element.secondaryColor(), RenderHelper.renderLight, true));
    }

    public void renderOuterLight(RenderParams params) {
        baseOffset(params.matrixStack);
        PoseStack matrixStackIn = params.matrixStack;
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.translate(0, -entity.getBbHeight() * 2, 0);
        matrixStackIn.scale(entity.getBbWidth() * 2.001f, entity.getBbHeight() * 2f, entity.getBbWidth() * 2.001f);
        RenderHelper.renderCylinderCache(BufferContext.create(matrixStackIn, bufferIn, OUTER_TYPE), CYLINDER_OUTER
                , new RenderHelper.RenderContext(alphaC, entity.spellContext().element.primaryColor(), RenderHelper.renderLight, true));
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
                , entity.spellContext().element.secondaryColor(), packedLightIn);
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
