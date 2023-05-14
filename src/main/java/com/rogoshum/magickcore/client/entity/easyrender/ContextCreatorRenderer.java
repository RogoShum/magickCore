package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.pointed.ContextCreatorEntity;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ContextCreatorRenderer extends EasyRenderer<ContextCreatorEntity> {
    private static final ResourceLocation TAKEN = new ResourceLocation("magickcore:textures/entity/takensphere.png");
    private static final RenderType RENDER_TYPE = RenderHelper.getTexturedEntityGlint(TAKEN, 1f, 45f);
    private static final RenderType LINE = RenderHelper.getTexturedSegmentLaser(MagickRegistry.getElement(LibElements.ORIGIN).getRenderer().getWaveTexture(1), 1f);
    float scale;

    public ContextCreatorRenderer(ContextCreatorEntity entity) {
        super(entity);
    }

    public void renderItems(RenderParams params) {
        baseOffset(params.matrixStack);
        PoseStack matrixStackIn = params.matrixStack;
        float partialTicks = params.partialTicks;
        if(entity.getEntityType() != null) {
            matrixStackIn.pushPose();
            float f3 = entity.tickCount % 360;
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f3));
            MultiBufferSource.BufferSource renderTypeBuffer = MultiBufferSource.immediate(params.buffer);
            ManaEnergyRenderer.renderEntity(entity.getEntityType(), matrixStackIn, renderTypeBuffer, RenderHelper.renderLight);
            renderTypeBuffer.endBatch();
            matrixStackIn.popPose();
        }

        List<ContextCreatorEntity.PosItem> stacks = entity.getStacks();
        for(int i = 0; i < stacks.size(); i++) {
            ContextCreatorEntity.PosItem item = stacks.get(i);
            matrixStackIn.pushPose();
            double x = item.prePos.x + (item.pos.x - item.prePos.x) * (double) partialTicks;
            double y = item.prePos.y + (item.pos.y - item.prePos.y) * (double) partialTicks;
            double z = item.prePos.z + (item.pos.z - item.prePos.z) * (double) partialTicks;
            matrixStackIn.translate(x, y, z);
            float f3 = ((float)item.age + partialTicks) / 20.0F + item.hoverStart;
            matrixStackIn.mulPose(Vector3f.YP.rotation(f3));
            MultiBufferSource.BufferSource renderTypeBuffer = MultiBufferSource.immediate(params.buffer);
            Minecraft.getInstance().getItemRenderer().renderStatic(item.getItemStack(), ItemTransforms.TransformType.GROUND, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, matrixStackIn, renderTypeBuffer, 0);
            renderTypeBuffer.endBatch();
            matrixStackIn.popPose();
        }

        matrixStackIn.pushPose();
        ItemStack stack = new ItemStack(entity.getMaterial().getItem());
        float f3 = ((float)entity.tickCount + partialTicks) / 20.0F;
        matrixStackIn.mulPose(Vector3f.YP.rotation(f3));
        MultiBufferSource.BufferSource renderTypeBuffer = MultiBufferSource.immediate(params.buffer);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, matrixStackIn, renderTypeBuffer, 0);
        renderTypeBuffer.endBatch();
        matrixStackIn.popPose();
    }

    public void renderSphere(RenderParams params) {
        baseOffset(params.matrixStack);
        PoseStack matrixStackIn = params.matrixStack;
        float partialTicks = params.partialTicks;
        Color color = entity.getInnerManaData().spellContext().element.primaryColor();
        int packedLightIn = Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(entity, partialTicks);

        if(entity.getEntityType() == null)
            matrixStackIn.scale(scale, scale, scale);
        else
            matrixStackIn.scale(1.1f, 1.1f, 1.1f);

        RenderHelper.RenderContext renderContext = new RenderHelper.RenderContext(entity.getEntityType() == null ? 0.4f : 0.5f, color, packedLightIn);
        RenderHelper.renderSphereCache(
                BufferContext.create(matrixStackIn, params.buffer, RENDER_TYPE)
                , renderContext, 0);
    }

    public void renderLaser(RenderParams params) {
        baseOffset(params.matrixStack);
        PoseStack matrixStackIn = params.matrixStack;
        Color color = entity.getInnerManaData().spellContext().element.primaryColor();
        matrixStackIn.scale(0.2f, 0.2f, 0.2f);
        BufferContext bufferContext = BufferContext.create(matrixStackIn, params.buffer, LINE);
        double space = 10;
        for (ContextCreatorEntity.PosItem posItem : entity.getStacks()) {
            double dis = posItem.pos.length();
            Vec3 start = posItem.pos.add(0, 0.2, 0);
            Vec3 end = start.normalize().scale(0.4);
            Vec3 direction = start.subtract(end);
            Vec3 origin = direction;
            double y = -origin.y;
            double x = Math.abs(origin.x);
            double z = Math.abs(origin.z);
            if(x > z)
                origin = new Vec3(x, y, 0);
            else if(z > x)
                origin = new Vec3(0, y, z);
            Vec3 prePos = start;
            for (int i = 0; i < space; ++i) {
                double trailFactor = i / (space - 1.0D);
                Vec3 pos = ParticleUtil.drawParabola(start, end, trailFactor, dis / 4, origin);
                direction = pos.subtract(prePos);
                Vec2 rota = getRotationFromVector(direction);

                prePos = pos;
                matrixStackIn.pushPose();
                matrixStackIn.translate(pos.x*5, pos.y*5, pos.z*5);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rota.x));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(rota.y));
                float scale = i / 10f;
                RenderHelper.renderLaserScale(bufferContext, new RenderHelper.RenderContext(0.7f, color), (float) (5f * direction.length()), scale, scale + .1f);
                matrixStackIn.popPose();
            }
        }
    }

    @Override
    public void update() {
        super.update();
        scale = entity.getBbWidth() * 1.1f;
        if(entity.tickCount == 0)
            scale *= 0;
        else if(entity.tickCount < 30)
            scale *= 1f - 1f / (float)entity.tickCount;
    }

    @Override
    protected void updateSpellContext() {
        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        Vec3 offset = cam.subtract(x, y, z).normalize().scale(entity.getBbWidth() * 0.5);
        debugX = x - camX + offset.x;
        debugY = y - camY + entity.getBbHeight() * 0.5 + offset.y;
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

        if(entity.getEntityType() != null) {
            //map.put(new RenderMode(LINE, RenderMode.ShaderList.DISTORTION_SMALL_SHADER), this::renderLaser);
            map.put(new RenderMode(LINE, RenderMode.ShaderList.SLIME_SHADER), this::renderLaser);
        }
        map.put(new RenderMode(RENDER_TYPE, RenderMode.ShaderList.BITS_SHADER), this::renderSphere);
        return map;
    }
}
