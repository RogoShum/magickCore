package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.living.ArtificialLifeEntity;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.tileentity.MaterialJarTileEntity;
import com.rogoshum.magickcore.common.tileentity.RadianceCrystalTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

public class RadianceCrystalRenderer extends EasyTileRenderer<RadianceCrystalTileEntity>{
    private static final RenderType TYPE = RenderHelper.getTexedOrb(RenderHelper.blankTex);
    private int light;
    Queue<Vec3> DIRECTION;
    protected Color color = Color.ORIGIN_COLOR;
    private static final ResourceLocation LASER_MID = new ResourceLocation(MagickCore.MOD_ID,  "textures/element/base/wave/wave_0.png");
    private static final RenderType LASER_TYPE = RenderHelper.getTexedLaserGlint(LASER_MID, -1f);
    private static final RenderType CRYSTAL_TYPE = RenderHelper.getTexedOrbGlint(RenderHelper.SPHERE_ROTATE, 1.0f, 1.0f);
    private final static RenderType SIDE = RenderHelper.getTexedEntityGlint(RenderHelper.SPHERE_ROTATE, 1f, 0f);
    public RadianceCrystalRenderer(RadianceCrystalTileEntity tile) {
        super(tile);
    }

    public void renderSide(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        float scale = RadianceCrystalTileEntity.workRange()*2+.9f;
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, SIDE), new RenderHelper.RenderContext(0.2f, tile.getElement().primaryColor(), RenderHelper.renderLight));
    }

    public void render(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        matrixStackIn.scale(0.65f, 0.65f, 0.65f);
        matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(45));
        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(45));
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, buffer, CRYSTAL_TYPE)
                , new RenderHelper.RenderContext(0.8f, tile.getElement().secondaryColor(), RenderHelper.renderLight));
        matrixStackIn.scale(0.8f, 0.8f, 0.8f);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, buffer, CRYSTAL_TYPE)
                , new RenderHelper.RenderContext(1.0f, tile.getElement().secondaryColor(), RenderHelper.renderLight));
    }

    public void renderItem(RenderParams renderParams) {
        PoseStack matrixStackIn = renderParams.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.translate(0, -0.1, 0);
        MultiBufferSource.BufferSource renderTypeBuffer = MultiBufferSource.immediate(renderParams.buffer);
        Minecraft.getInstance().getItemRenderer().renderStatic(tile.getItemStack(), ItemTransforms.TransformType.GROUND, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, matrixStackIn, renderTypeBuffer, 0);
        renderTypeBuffer.endBatch();
    }

    @Override
    public void update() {
        super.update();
        light = tile.getLevel().getLightEmission(tile.getBlockPos());
        DIRECTION = Queues.newArrayDeque();
        List<Entity> livings = tile.getLevel().getEntities((Entity) null, new AABB(tile.getBlockPos()).inflate(8), (entity) -> entity instanceof IManaCapacity);
        for (Entity entity : livings) {
            Vec3 me = getEntityRenderVector(Minecraft.getInstance().getFrameTime());
            Vec3 it = getEntityRenderVector(entity, Minecraft.getInstance().getFrameTime()).add(0, entity.getBbHeight() * 0.5, 0);
            Vec3 dirc = me.subtract(it);
            float distance = (float) dirc.length();
            dirc = dirc.normalize();
            Vec2 rota = getRotationFromVector(dirc);
            DIRECTION.add(new Vec3(rota.x, rota.y, distance));
        }
    }

    public void renderLaser(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.scale(0.1f, 0.1f, 0.1f);
        Queue<Vec3> direction = DIRECTION;
        for (Vec3 vector3d : direction) {
            matrixStackIn.pushPose();
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((float) vector3d.x));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((float) vector3d.y));
            RenderHelper.renderLaserParticle(
                    BufferContext.create(matrixStackIn, params.buffer, TYPE)
                    , new RenderHelper.RenderContext(0.8f, tile.getElement().primaryColor()), (float) (vector3d.z * 10));
            matrixStackIn.popPose();
        }
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(CRYSTAL_TYPE, RenderMode.ShaderList.BITS_SHADER), this::render);
        if(Minecraft.getInstance().hitResult instanceof BlockHitResult result && result.getBlockPos().equals(tile.getBlockPos())) {
            map.put(new RenderMode(CRYSTAL_TYPE), this::renderSide);
        }
        map.put(RenderMode.ORIGIN_RENDER, this::renderItem);
        if(DIRECTION != null)
            map.put(new RenderMode(LASER_TYPE, RenderMode.ShaderList.BITS_SMALL_SHADER), this::renderLaser);
        return map;
    }
}
