package com.rogoshum.magickcore.client.entity.easyrender.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.render.IEasyRender;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.ElementSoulRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
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
        return entity.level.isClientSide;
    }

    public Vec3 getEntityRenderVector(float partialTicks) {
        double x = entity.xOld + (entity.getX() - entity.xOld) * (double) partialTicks;
        double y = entity.yOld + (entity.getY() - entity.yOld) * (double) partialTicks;
        double z = entity.zOld + (entity.getZ() - entity.zOld) * (double) partialTicks;
        return new Vec3(x, y, z);
    }

    public static Vec3 getEntityRenderVector(Entity entity, float partialTicks) {
        double x = entity.xOld + (entity.getX() - entity.xOld) * (double) partialTicks;
        double y = entity.yOld + (entity.getY() - entity.yOld) * (double) partialTicks;
        double z = entity.zOld + (entity.getZ() - entity.zOld) * (double) partialTicks;
        return new Vec3(x, y, z);
    }

    @Override
    public void update() {
        if(RenderHelper.showDebug() && entity instanceof IManaEntity)
            updateSpellContext();
    }

    @Override
    public void updatePosition() {
        Vec3 vec = getEntityRenderVector(Minecraft.getInstance().getFrameTime());
        x = vec.x;
        y = vec.y;
        z = vec.z;
    }

    public void baseOffset(PoseStack matrixStackIn) {
        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        matrixStackIn.translate(x - camX, y - camY + entity.getBbHeight() * 0.5, z - camZ);
    }

    @Override
    public boolean alive() {
        if(entity instanceof ManaProjectileEntity
            && ((ManaProjectileEntity) entity).spellContext().containChild(LibContext.SELF)
            && !(this instanceof ElementSoulRenderer)) {
            MagickCore.proxy.addRenderer(() -> new ElementSoulRenderer((ManaProjectileEntity) this.entity));
            return false;
        }
        return entity.isAlive() && entity.isAddedToWorld() && entity.level == Minecraft.getInstance().level;
    }

    @Override
    public AABB boundingBox() {
        return entity.getBoundingBox();
    }

    @Override
    public Vec3 positionVec() {
        return entity.position();
    }

    protected void renderDebug(RenderParams renderParams) {
        if(entity instanceof IManaEntity) {
            renderSpellContext(renderParams);
        }
    }

    protected void updateSpellContext() {
        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        Vec3 offset = cam.subtract(x, y, z).normalize().scale(entity.getBbWidth() * 0.5);
        debugX = x - camX + offset.x;
        debugY = y - camY + entity.getBbHeight() * 0.5 + offset.y;
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
            renderParams.matrixStack.pushPose();
            renderParams.matrixStack.translate(debugX, debugY, debugZ);
            renderParams.matrixStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
            renderParams.matrixStack.scale(0.015f, 0.015f, 0.015f);
            renderParams.matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
            renderParams.matrixStack.translate(-contextLength, debugSpellContext.length * -4, 0);
            for (int i = 0; i < debugSpellContext.length; ++i) {
                String tip = debugSpellContext[i];
                if(!tip.isEmpty()) {
                    renderParams.matrixStack.pushPose();
                    Minecraft.getInstance().font.draw(renderParams.matrixStack, tip, 0, i*8, 0);
                    renderParams.matrixStack.popPose();
                }
            }
            renderParams.matrixStack.popPose();
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
