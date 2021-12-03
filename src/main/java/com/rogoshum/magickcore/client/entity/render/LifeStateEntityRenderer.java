package com.rogoshum.magickcore.client.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.LifeStateEntity;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.lifestate.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LifeStateEntityRenderer extends EntityRenderer<LifeStateEntity> {
    private static final ElementRenderer origin = MagickCore.proxy.getElementRender(LibElements.ORIGIN);
    public static final HashMap<String, Consumer<RenderPackage>> renderState = new HashMap<>();

    public LifeStateEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        renderState.put(LifeState.ENTITY, this::renderEntity);
        renderState.put(LifeState.ITEM, this::renderItem);
        renderState.put(LifeState.POTION, this::renderPotion);
    }

    @Override
    public void render(LifeStateEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        //super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

        //if(entityIn.ticksExisted % 2 != 0) return;
        renderParticle(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    public void renderEntity(RenderPackage pack) {
        EntityLifeState entity = (EntityLifeState) pack.lifeState;
        float scale = 0.1f;
        float scaleX = (float) (scale / entity.getValue().getRenderBoundingBox().getXSize());
        float scaleY = entity.getValue().getRenderBoundingBox().getXSize() >= 1.0f ?
                (float) (scaleX / entity.getValue().getRenderBoundingBox().getXSize() / entity.getValue().getRenderBoundingBox().getYSize())
                : (float) (scaleX * entity.getValue().getRenderBoundingBox().getXSize() / entity.getValue().getRenderBoundingBox().getYSize());
        float scaleZ = (float) (scale / entity.getValue().getRenderBoundingBox().getZSize());

        //MagickCore.LOGGER.debug(entity.getValue().getRenderBoundingBox().getXSize());

        pack.matrixStack.push();
        pack.matrixStack.rotate(Vector3f.YP.rotationDegrees(pack.entity.rotationYaw));
        pack.matrixStack.rotate(Vector3f.XP.rotationDegrees(pack.entity.rotationPitch));
        pack.matrixStack.scale(scaleX, scaleY, scaleZ);
        Minecraft.getInstance().getRenderManager().renderEntityStatic(entity.getValue()
                , 0, -(entity.getValue().getRenderBoundingBox().getYSize() - pack.entity.getRenderBoundingBox().getYSize() / scaleY) / 2, 0,
                0, 0, pack.matrixStack, pack.buffer, RenderHelper.renderLight);
        pack.matrixStack.pop();
    }

    public void renderPotion(RenderPackage pack) {
        if (pack.partialTicks > .3) return;
        PotionLifeState item = (PotionLifeState) pack.lifeState;
        int j = PotionUtils.getColor(item.getValue());
        int k = j >> 16 & 255;
        int l = j >> 8 & 255;
        int i1 = j & 255;
        pack.entity.world.addOptionalParticle(ParticleTypes.ENTITY_EFFECT
                , pack.entity.getPosX() + MagickCore.rand.nextDouble() * 0.3
                , pack.entity.getPosY() + MagickCore.rand.nextDouble() * 0.3
                , pack.entity.getPosZ() + MagickCore.rand.nextDouble() * 0.3
                , (double)((float)k / 255.0F), (double)((float)l / 255.0F), (double)((float)i1 / 255.0F));
    }

    public void renderItem(RenderPackage pack) {
        ItemStackLifeState item = (ItemStackLifeState) pack.lifeState;
        pack.matrixStack.push();
        pack.matrixStack.rotate(Vector3f.YP.rotationDegrees(pack.entity.rotationYaw));
        pack.matrixStack.rotate(Vector3f.XP.rotationDegrees(pack.entity.rotationPitch));
        pack.matrixStack.scale(0.5f, 0.5f, 0.5f);
        Minecraft.getInstance().getItemRenderer().renderItem(item.getValue(), ItemCameraTransforms.TransformType.GROUND, RenderHelper.renderLight
                , OverlayTexture.NO_OVERLAY, pack.matrixStack, pack.buffer);
        pack.matrixStack.pop();
    }

    public void renderParticle(LifeStateEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        ElementRenderer renderer = entityIn.getElementData().getElement().getRenderer();

        LitParticle par = new LitParticle(entityIn.world, renderer.getParticleTexture()
                , new Vector3d(entityIn.getPosX()
                , entityIn.getPosY() + entityIn.getHeight() / 2
                , entityIn.getPosZ())
                , 0.1f, 0.1f, MagickCore.getNegativeToOne(), 20, renderer);

        par.setParticleGravity(0)
                .setGlow()
                .setLimitScale()
                .addMotion(MagickCore.getNegativeToOne() * 0.015, MagickCore.getNegativeToOne() * 0.015, MagickCore.getNegativeToOne() * 0.015)
                .setCanCollide(false);

        for (String name : entityIn.getCarrier().getLifeStates().keySet()) {
            if (renderState.containsKey(name)) {
                renderState.get(name).accept(new RenderPackage()
                        .lifeState(entityIn.getCarrier().getState(name))
                        .entity(entityIn)
                        .particle(par)
                        .buffer(bufferIn)
                        .matrixStack(matrixStackIn)
                        .packedLight(packedLightIn)
                        .partialTicks(partialTicks));
            }
        }
        if (partialTicks > .3) return;
        par.add();
    }

    @Override
    public ResourceLocation getEntityTexture(LifeStateEntity entity) {
        return origin.getParticleTexture();
    }

    public static class RenderPackage {
        public LifeStateEntity entity;
        public LitParticle particle;
        public LifeState<?> lifeState;
        public MatrixStack matrixStack;
        public IRenderTypeBuffer buffer;
        public float partialTicks;
        public int packedLightIn;

        public RenderPackage entity(LifeStateEntity entity) {
            this.entity = entity;
            return this;
        }

        public RenderPackage particle(LitParticle particle) {
            this.particle = particle;
            return this;
        }

        public RenderPackage lifeState(LifeState<?> lifeState) {
            this.lifeState = lifeState;
            return this;
        }

        public RenderPackage matrixStack(MatrixStack matrixStack) {
            this.matrixStack = matrixStack;
            return this;
        }

        public RenderPackage buffer(IRenderTypeBuffer buffer) {
            this.buffer = buffer;
            return this;
        }

        public RenderPackage partialTicks(float partialTicks) {
            this.partialTicks = partialTicks;
            return this;
        }

        public RenderPackage packedLight(int packedLightIn) {
            this.packedLightIn = packedLightIn;
            return this;
        }
    }
}
