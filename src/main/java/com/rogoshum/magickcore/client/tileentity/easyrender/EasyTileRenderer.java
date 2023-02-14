package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.IEasyRender;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.function.Consumer;

public abstract class EasyTileRenderer<T extends BlockEntity>implements IEasyRender {
    protected static final ResourceLocation sphereOrb = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_bloom.png");
    protected static final ResourceLocation cylinder_bloom = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_bloom.png");
    protected static final ResourceLocation sphere_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_rotate.png");
    protected static final ResourceLocation wind = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/wind.png");
    protected static final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");
    protected static final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected static final ResourceLocation taken = new ResourceLocation("magickcore:textures/entity/takensphere.png");
    protected final T tile;
    protected double x;
    protected double y;
    protected double z;

    public EasyTileRenderer(T tile) {
        this.tile = tile;
    }

    public static Vec2 getRotationFromVector(Vec3 dirc) {
        float yaw = (float) (Math.atan2(dirc.x, dirc.z) * 180 / Math.PI);
        if (yaw < 0)
            yaw += 360;

        float tmp = (float) Math.sqrt (dirc.z * dirc.z + dirc.x * dirc.x);
        float pitch = (float) (Math.atan2(-dirc.y, tmp) * 180 / Math.PI);
        if (pitch < 0)
            pitch += 360;
        return new Vec2(yaw + 90, pitch - 90);
    }

    public boolean isRemote() {
        return tile.getLevel().isClientSide();
    }

    public Vec3 getEntityRenderVector(float partialTicks) {
        return positionVec();
    }

    @Override
    public void update() {
        Vec3 vec = getEntityRenderVector(Minecraft.getInstance().getFrameTime());
        x = vec.x;
        y = vec.y;
        z = vec.z;
    }

    public void baseOffset(PoseStack matrixStackIn) {
        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        matrixStackIn.translate(x - camX, y - camY, z - camZ);
    }

    @Override
    public boolean alive() {
        return !tile.isRemoved() && tile.hasLevel() && tile.getLevel() == Minecraft.getInstance().level;
    }

    @Override
    public AABB boundingBox() {
        return new AABB(tile.getBlockPos());
    }

    @Override
    public Vec3 positionVec() {
        return Vec3.atCenterOf(tile.getBlockPos());
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getDebugFunction() {
        return null;
    }
}
