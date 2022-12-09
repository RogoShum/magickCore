package com.rogoshum.magickcore.client.entity.easyrender.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.render.IEasyRender;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public abstract class EasyRenderer<T extends Entity> implements IEasyRender{
    protected static final ResourceLocation sphereOrb = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_bloom.png");
    protected static final ResourceLocation cylinder_bloom = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_bloom.png");
    protected static final ResourceLocation sphere_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_rotate.png");
    protected static final ResourceLocation wind = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/wind.png");
    protected static final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");
    protected static final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected static final ResourceLocation taken = new ResourceLocation("magickcore:textures/entity/takensphere.png");
    protected final T entity;
    protected double x;
    protected double y;
    protected double z;
    protected String[] debugSpellContext;
    protected double debugX;
    protected double debugY;
    protected double debugZ;
    protected int contextLength;

    public EasyRenderer(T entity) {
        this.entity = entity;
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
        return entity.world.isRemote;
    }

    public Vector3d getEntityRenderVector(float partialTicks) {
        double x = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * (double) partialTicks;
        double y = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * (double) partialTicks;
        double z = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * (double) partialTicks;
        return new Vector3d(x, y, z);
    }

    public static Vector3d getEntityRenderVector(Entity entity, float partialTicks) {
        double x = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * (double) partialTicks;
        double y = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * (double) partialTicks;
        double z = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * (double) partialTicks;
        return new Vector3d(x, y, z);
    }

    @Override
    public void update() {
        Vector3d vec = getEntityRenderVector(Minecraft.getInstance().getRenderPartialTicks());
        x = vec.x;
        y = vec.y;
        z = vec.z;
        if(RenderHelper.showDebug() && entity instanceof IManaEntity)
            updateSpellContext();
    }

    public void baseOffset(MatrixStack matrixStackIn) {
        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        matrixStackIn.translate(x - camX, y - camY + entity.getHeight() * 0.5, z - camZ);
    }

    @Override
    public boolean alive() {
        return entity.isAlive() && entity.isAddedToWorld() && entity.world == Minecraft.getInstance().world;
    }

    @Override
    public AxisAlignedBB boundingBox() {
        return entity.getBoundingBox();
    }

    @Override
    public Vector3d positionVec() {
        return entity.getPositionVec();
    }

    protected void renderDebug(RenderParams renderParams) {
        if(entity instanceof IManaEntity) {
            renderSpellContext(renderParams);
        }
    }

    protected void updateSpellContext() {
        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        Vector3d offset = cam.subtract(x, y, z).normalize().scale(entity.getWidth() * 0.5);
        debugX = x - camX + offset.x;
        debugY = y - camY + entity.getHeight() * 0.5 + offset.y;
        debugZ = z - camZ + offset.z;

        String information = ((IManaEntity)entity).spellContext().toString();
        if(information.isEmpty())  {
            debugSpellContext = null;
            return;
        }
        debugSpellContext = information.split("\n");
        contextLength = 0;
        if(debugSpellContext.length < 1) return;
        for (String s : debugSpellContext) {
            if (s.length() > contextLength)
                contextLength = s.length();
        }
    }

    protected void renderSpellContext(RenderParams renderParams) {
        if(debugSpellContext != null) {
            renderParams.matrixStack.push();
            renderParams.matrixStack.translate(debugX, debugY, debugZ);
            renderParams.matrixStack.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
            renderParams.matrixStack.scale(0.015f, 0.015f, 0.015f);
            renderParams.matrixStack.rotate(Vector3f.ZP.rotationDegrees(180));
            renderParams.matrixStack.translate(-contextLength, debugSpellContext.length * -4, 0);
            for (int i = 0; i < debugSpellContext.length; ++i) {
                String tip = debugSpellContext[i];
                if(!tip.isEmpty()) {
                    renderParams.matrixStack.push();
                    Minecraft.getInstance().fontRenderer.drawString(renderParams.matrixStack, tip, 0, i*8, 0);
                    renderParams.matrixStack.pop();
                }
            }
            renderParams.matrixStack.pop();
        }
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getDebugFunction() {
        if(!(entity instanceof IManaEntity)) return null;
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(RenderMode.ORIGIN_RENDER, this::renderDebug);
        return map;
    }
}
