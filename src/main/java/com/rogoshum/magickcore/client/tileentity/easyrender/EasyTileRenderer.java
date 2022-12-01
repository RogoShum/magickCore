package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.IEasyRender;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import java.util.HashMap;
import java.util.function.Consumer;

public abstract class EasyTileRenderer<T extends TileEntity>implements IEasyRender {
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

    public static Vector2f getRotationFromVector(Vector3d dirc) {
        float yaw = (float) (Math.atan2(dirc.x, dirc.z) * 180 / Math.PI);
        if (yaw < 0)
            yaw += 360;

        float tmp = (float) Math.sqrt (dirc.z * dirc.z + dirc.x * dirc.x);
        float pitch = (float) (Math.atan2(-dirc.y, tmp) * 180 / Math.PI);
        if (pitch < 0)
            pitch += 360;
        return new Vector2f(yaw + 90, pitch - 90);
    }

    public boolean isRemote() {
        return tile.getWorld().isRemote();
    }

    public Vector3d getEntityRenderVector(float partialTicks) {
        return positionVec();
    }

    @Override
    public void update() {
        Vector3d vec = getEntityRenderVector(Minecraft.getInstance().getRenderPartialTicks());
        x = vec.x;
        y = vec.y;
        z = vec.z;
    }

    public void baseOffset(MatrixStack matrixStackIn) {
        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        matrixStackIn.translate(x - camX, y - camY, z - camZ);
    }

    @Override
    public boolean alive() {
        return !tile.isRemoved() && tile.hasWorld() && tile.getWorld() == Minecraft.getInstance().world;
    }

    @Override
    public AxisAlignedBB boundingBox() {
        return new AxisAlignedBB(tile.getPos());
    }

    @Override
    public Vector3d positionVec() {
        return Vector3d.copyCentered(tile.getPos());
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getDebugFunction() {
        return null;
    }
}
