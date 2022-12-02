package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.pointed.ContextCreatorEntity;
import com.rogoshum.magickcore.common.lib.LibShaders;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ContextCreatorRenderer extends EasyRenderer<ContextCreatorEntity> {
    private static final ResourceLocation TAKEN = new ResourceLocation("magickcore:textures/entity/takensphere.png");
    private static final RenderType RENDER_TYPE = RenderHelper.getTexedSphereGlow(TAKEN, 1f, 0f);
    float scale;

    public ContextCreatorRenderer(ContextCreatorEntity entity) {
        super(entity);
    }

    public void renderItems(RenderParams params) {
        baseOffset(params.matrixStack);
        MatrixStack matrixStackIn = params.matrixStack;
        float partialTicks = params.partialTicks;
        List<ContextCreatorEntity.PosItem> stacks = entity.getStacks();
        for(int i = 0; i < stacks.size(); i++) {
            ContextCreatorEntity.PosItem item = stacks.get(i);
            matrixStackIn.push();
            double x = item.prePos.x + (item.pos.x - item.prePos.x) * (double) partialTicks;
            double y = item.prePos.y + (item.pos.y - item.prePos.y) * (double) partialTicks;
            double z = item.prePos.z + (item.pos.z - item.prePos.z) * (double) partialTicks;
            matrixStackIn.translate(x, y, z);
            float f3 = ((float)item.age + partialTicks) / 20.0F + item.hoverStart;
            matrixStackIn.rotate(Vector3f.YP.rotation(f3));
            IBakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(item.getItemStack(), null, null);
            IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(params.buffer);
            Minecraft.getInstance().getItemRenderer().renderItem(item.getItemStack(), ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, renderTypeBuffer, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
            renderTypeBuffer.finish();
            matrixStackIn.pop();
        }

        matrixStackIn.push();
        ItemStack stack = new ItemStack(entity.getMaterial().getItem());
        float f3 = ((float)entity.ticksExisted + partialTicks) / 20.0F;
        matrixStackIn.rotate(Vector3f.YP.rotation(f3));
        IBakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, null, null);
        IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(params.buffer);
        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, renderTypeBuffer, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
        renderTypeBuffer.finish();
        matrixStackIn.pop();
    }

    public void renderSphere(RenderParams params) {
        baseOffset(params.matrixStack);
        MatrixStack matrixStackIn = params.matrixStack;
        float partialTicks = params.partialTicks;
        Color color = entity.getInnerManaData().spellContext().element.color();
        int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entity, partialTicks);
        matrixStackIn.scale(scale, scale, scale);

        RenderHelper.RenderContext renderContext = new RenderHelper.RenderContext(0.5f, color, packedLightIn);
        RenderHelper.renderSphere(
                BufferContext.create(matrixStackIn, params.buffer, RENDER_TYPE)
                , renderContext, 24);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
        RenderHelper.renderSphere(
                BufferContext.create(matrixStackIn, params.buffer, RENDER_TYPE)
                , renderContext, 24);
    }

    @Override
    public void update() {
        super.update();
        scale = entity.getWidth() * 1.2f;
        if(entity.ticksExisted == 0)
            scale *= 0;
        else if(entity.ticksExisted < 30)
            scale *= 1f - 1f / (float)entity.ticksExisted;
    }

    @Override
    protected void updateSpellContext() {
        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        Vector3d offset = cam.subtract(x, y, z).normalize().scale(entity.getWidth() * 0.5);
        debugX = x - camX + offset.x;
        debugY = y - camY + entity.getHeight() * 0.5 + offset.y;
        debugZ = z - camZ + offset.z;

        String information = entity.getInnerManaData().spellContext().toString();
        debugSpellContext = information.split("\n");
        contextLength = 0;
        for (String s : debugSpellContext) {
            if (s.length() > contextLength)
                contextLength = s.length();
        }
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(RenderMode.ORIGIN_RENDER, this::renderItems);
        map.put(new RenderMode(RENDER_TYPE, RenderMode.ShaderList.SLIME_SHADER), this::renderSphere);
        return map;
    }
}
