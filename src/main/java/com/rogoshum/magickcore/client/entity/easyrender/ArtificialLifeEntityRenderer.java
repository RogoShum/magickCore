package com.rogoshum.magickcore.client.entity.easyrender;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.living.ArtificialLifeEntity;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

public class ArtificialLifeEntityRenderer extends EasyRenderer<ArtificialLifeEntity> {
    protected Color color;
    protected final HashMap<Vec3, VoxelShape> shapes = new HashMap<>();
    protected final HashSet<Vec3> posSet = new HashSet<>();
    protected static final RenderType BLOCK_TYPE = RenderHelper.getLineStripPC(5);
    protected static final ResourceLocation LASER_TOP = new ResourceLocation(MagickCore.MOD_ID,  "textures/element/base/wave/wave_0.png");
    Queue<Vec3> DIRECTION;
    private static final ResourceLocation LASER_MID = new ResourceLocation(MagickCore.MOD_ID,  "textures/element/base/wave/wave_0.png");
    private final static RenderType SIDE = RenderHelper.getTexturedEntityGlint(RenderHelper.SPHERE_ROTATE, 1f, 0f);
    final RenderType TYPE = RenderHelper.getTexturedLaserGlint(LASER_MID, -1f);
    public ArtificialLifeEntityRenderer(ArtificialLifeEntity entity) {
        super(entity);
    }

    @Override
    public void update() {
        super.update();
        if(Minecraft.getInstance().crosshairPickEntity != entity) return;
        color = entity.spellContext().element().primaryColor();
        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        shapes.clear();
        posSet.clear();
        if(entity.getVectorSet().isEmpty()) {
            addPos(entity.getTracePos(), cam);
        } else {
            for(Vec3 vec : entity.getVectorSet()) {
                addPos(vec, cam);
            }
        }
        DIRECTION = Queues.newArrayDeque();
        List<Entity> livings = entity.level.getEntities(entity, entity.getBoundingBox().inflate(8), entity -> entity instanceof IManaCapacity);
        for (Entity entity : livings) {
            Vec3 me = getEntityRenderVector(Minecraft.getInstance().getFrameTime()).add(0, this.entity.getBbHeight() * 0.5, 0);
            Vec3 it = getEntityRenderVector(entity, Minecraft.getInstance().getFrameTime()).add(0, entity.getBbHeight() * 0.5, 0);
            Vec3 dirc = me.subtract(it);
            float distance = (float) dirc.length();
            dirc = dirc.normalize();
            Vec2 rota = getRotationFromVector(dirc);
            DIRECTION.add(new Vec3(rota.x, rota.y, distance));
        }
    }

    public void addPos(Vec3 vec, Vec3 cam) {
        BlockPos pos = new BlockPos(vec);
        BlockState state = entity.level.getBlockState(pos);
        VoxelShape blockShape = state.getShape(entity.level, pos);
        VoxelShape shape = Shapes.block();
        if(!blockShape.isEmpty())
            shape = blockShape;
        Vec3 offsetPos = new Vec3(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
        posSet.add(offsetPos);
        shapes.put(offsetPos, shape);
    }

    @Override
    public boolean forceRender() {
        return Minecraft.getInstance().crosshairPickEntity == entity && entity.isFocus();
    }

    public void renderSide(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.scale(10.9f, 10.9f, 10.9f);
        RenderHelper.renderCubeCache(BufferContext.create(matrixStackIn, bufferIn, SIDE), new RenderHelper.RenderContext(0.2f, entity.spellContext().element().primaryColor(), RenderHelper.renderLight));
    }

    public void renderBlock(RenderParams params) {
        for (Vec3 pos : posSet) {
            if(shapes.containsKey(pos)) {
                VoxelShape shape = shapes.get(pos);
                RenderHelper.drawShape(params.matrixStack, params.buffer, shape, pos.x, pos.y, pos.z, color.r(), color.g(), color.b(), 1.0F);
            }
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
            matrixStackIn.scale(1, (float) (vector3d.z * 10), 1);
            RenderHelper.renderLaserParticle(
                    BufferContext.create(matrixStackIn, params.buffer, TYPE)
                    , new RenderHelper.RenderContext(0.8f, this.entity.spellContext().element().primaryColor()));
            matrixStackIn.popPose();
        }
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        if(DIRECTION != null)
            map.put(new RenderMode(TYPE, RenderMode.ShaderList.BITS_SMALL_SHADER), this::renderLaser);
        if(Minecraft.getInstance().crosshairPickEntity == entity)
            map.put(new RenderMode(SIDE), this::renderSide);
        if(Minecraft.getInstance().crosshairPickEntity != entity || !entity.isFocus() || posSet.isEmpty() || shapes.isEmpty())
            return map;
        map.put(new RenderMode(BLOCK_TYPE), this::renderBlock);

        return map;
    }
}
