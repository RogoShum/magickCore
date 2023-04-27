package com.rogoshum.magickcore.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.api.render.easyrender.IEasyRender;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.api.render.ElementRenderer;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class LitParticle implements ILightSourceEntity, IEasyRender {
    public boolean render = true;
    protected double posX;
    protected double posY;
    protected double posZ;
    protected double lPosX;
    protected double lPosY;
    protected double lPosZ;

    protected double renderX;
    protected double renderY;
    protected double renderZ;

    private Color color;
    private final float alpha;
    private int age = 1;
    private final int maxAge;
    private float scaleWidth;
    private float scaleHeight;
    private ResourceLocation texture;
    private boolean isGlow;
    private static final AABB EMPTY_AABB = new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    protected double motionX;
    protected double motionY;
    protected double motionZ;
    private AABB boundingBox = EMPTY_AABB;
    protected boolean onGround;
    protected boolean canCollide = true;
    private boolean collidedY;
    protected float particleGravity;
    private final Level world;
    private final ElementRenderer renderer;
    private float shakeLimit;
    private boolean limitScale;

    private boolean noScale;
    private Entity traceTarget;
    private final RenderMode.ShaderList shader = RenderMode.ShaderList.create();

    private RenderType type;
    private RenderHelper.RenderContext renderContext;

    public LitParticle(Level world, ResourceLocation texture, Vec3 position, float scaleWidth, float scaleHeight, float alpha, int maxAge, ElementRenderer renderer) {
        this.world = world;
        this.texture = texture;
        this.setSize(scaleWidth, scaleHeight);
        this.setPosition(position.x, position.y, position.z);
        this.maxAge = Math.max(maxAge, 2);
        this.alpha = alpha;
        this.color = renderer.getSecondaryColor();
        this.particleGravity = renderer.getParticleGravity();
        this.canCollide = renderer.getParticleCanCollide();
        this.renderer = renderer;
        update();
    }

    @Override
    public boolean hasRenderer() {
        return false;
    }

    @Override
    public void setShouldRender(boolean shouldRender) {
        render = shouldRender;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public LitParticle setColor(Color color) {
        this.color = color;
        return this;
    }

    public LitParticle setColor(float r, float g, float b) {
        this.color = Color.create(r, g, b);
        return this;
    }

    public LitParticle setTexture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    public LitParticle setTraceTarget(Entity traceTarget) {
        this.traceTarget = traceTarget;
        return this;
    }

    public LitParticle setLimitScale() {
        this.limitScale = true;
        return this;
    }

    public LitParticle setNoScale() {
        this.noScale = true;
        return this;
    }

    public LitParticle setShakeLimit(float shakeLimit) {
        this.shakeLimit = shakeLimit*0.1f*Math.min(scaleHeight, scaleWidth);
        return this;
    }

    public LitParticle setParticleGravity(float g) {
        this.particleGravity = g;
        return this;
    }

    public LitParticle useShader(String shader) {
        this.shader.addShader(shader);
        return this;
    }

    protected void setSize(float particleWidth, float particleHeight) {
        if (particleWidth != this.scaleWidth || particleHeight != this.scaleHeight) {
            this.scaleWidth = particleWidth;
            this.scaleHeight = particleHeight;
            AABB axisalignedbb = this.getBoundingBox();
            double d0 = (axisalignedbb.minX + axisalignedbb.maxX - (double) particleWidth) / 2.0D;
            double d1 = (axisalignedbb.minZ + axisalignedbb.maxZ - (double) particleWidth) / 2.0D;
            this.setBoundingBox(new AABB(d0, axisalignedbb.minY, d1, d0 + (double) this.scaleWidth, axisalignedbb.minY + (double) this.scaleHeight, d1 + (double) this.scaleWidth));
        }

    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosition(double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.lPosX = this.posX;
        this.lPosY = this.posY;
        this.lPosZ = this.posZ;
        float f = this.scaleWidth / 2.0F;
        float f1 = this.scaleHeight;
        this.setBoundingBox(new AABB(x - (double) f, y, z - (double) f, x + (double) f, y + (double) f1, z + (double) f));
    }

    public LitParticle addMotion(double x, double y, double z) {
        this.motionX += x;
        this.motionY += y;
        this.motionZ += z;
        return this;
    }

    public float getAlpha(float alpha) {
        if(this.maxAge <= 3)
            return alpha;
        float f = ((float) this.age) / (float) this.maxAge;
        if(f > .7)
            f -= Math.pow(1-f, 1.1);
        f = Math.min(f, 1.0f);
        return alpha * f;
    }

    public float getScale(float scale) {
        if(this.maxAge <= 3 || noScale)
            return scale;
        float f = (float) this.age / (float) this.maxAge;
        if (f <= 0.5f)
            f = 1.0f - f;
        f = 1.0F - f;
        return limitScale && (float) this.age / (float) this.maxAge <= 0.5f ? scale : scale * f * 2f;
    }

    public void tick() {
        try {
            this.lPosX = this.posX;
            this.lPosY = this.posY;
            this.lPosZ = this.posZ;
            this.age++;
            this.motionY -= 0.04D * (double) this.particleGravity;

            if (this.traceTarget != null) {
                Vec3 vec = this.traceTarget.position().add(0, this.traceTarget.getBbHeight() / 2, 0).subtract(this.posX, this.posY, this.posZ).normalize();
                double length = 0.3;

                this.motionX += (vec.x / 2 + MagickCore.getNegativeToOne()) * length;
                this.motionY += (vec.y / 2 + MagickCore.getNegativeToOne()) * length * 0.1;
                this.motionZ += (vec.z / 2 + MagickCore.getNegativeToOne()) * length;
            }

            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double) 0.98F;
            this.motionY *= (double) 0.98F;
            this.motionZ *= (double) 0.98F;
            if (this.onGround) {
                this.motionX *= (double) 0.7F;
                this.motionZ *= (double) 0.7F;
            }

            renderer.tickParticle(this);
        } catch (Exception ignored) {
            this.remove();
        }
    }

    public void easyTick() {
        this.age++;
    }


    public void move(double x, double y, double z) {
        if (!this.collidedY) {
            double d0 = x;
            double d1 = y;
            double d2 = z;
            if (this.canCollide && (x != 0.0D || y != 0.0D || z != 0.0D)) {
                Vec3 vector3d = Entity.collideBoundingBox((Entity) null, new Vec3(x, y, z), this.getBoundingBox(), this.world, List.of());
                x = vector3d.x;
                y = vector3d.y;
                z = vector3d.z;
            }

            if (x != 0.0D || y != 0.0D || z != 0.0D) {
                this.setBoundingBox(this.getBoundingBox().move(x, y, z));
                this.resetPositionToBB();
            }

            if (Math.abs(d1) >= (double) 1.0E-5F && Math.abs(y) < (double) 1.0E-5F) {
                this.collidedY = true;
            }

            this.onGround = d1 != y && d1 < 0.0D;
            if (d0 != x) {
                this.motionX = 0.0D;
            }

            if (d2 != z) {
                this.motionZ = 0.0D;
            }

        }
    }

    protected void resetPositionToBB() {
        AABB axisalignedbb = this.getBoundingBox();
        this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
        this.posY = axisalignedbb.minY;
        this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;
    }

    public AABB getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(AABB bb) {
        this.boundingBox = bb;
    }

    public LitParticle setGlow() {
        this.isGlow = true;
        return this;
    }

    public boolean getGlow() {
        return this.isGlow;
    }

    public boolean isDead() {
        return age >= maxAge;
    }

    public void add() {
        MagickCore.addMagickParticle(this);
    }

    public LitParticle setCanCollide(boolean canCollide) {
        this.canCollide = canCollide;
        return this;
    }

    public boolean shouldRender(Frustum camera) {
        return camera.isVisible(this.boundingBox);
    }

    @Override
    public float getSourceLight() {
        return getScale(scaleHeight) * 10;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(type, shader), this::render);
        return map;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getLightFunction() {
        return null;
    }

    public RenderMode getRenderMode() {
        type = isGlow ? RenderHelper.getTexedParticleGlow(texture, shakeLimit) : RenderHelper.getTexedParticle(texture, shakeLimit);
        return new RenderMode(type, shader);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getDebugFunction() {
        return null;
    }

    public void render(RenderParams renderParams) {
        PoseStack matrixStackIn = renderParams.matrixStack;
        matrixStackIn.pushPose();
        matrixStackIn.translate(renderX, renderY, renderZ);
        matrixStackIn.scale(getScale(scaleWidth), getScale(scaleHeight), getScale(scaleWidth));
        matrixStackIn.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        RenderHelper.callParticleVertex(BufferContext.create(matrixStackIn, renderParams.buffer, type).useShader(shader), renderContext);
        matrixStackIn.popPose();
    }

    @Override
    public void update() {
        if (this.texture == null) return;
        float renderAlpha = getAlpha(this.alpha);
        renderContext = new RenderHelper.RenderContext(renderAlpha, color, RenderHelper.renderLight);
    }

    @Override
    public void updatePosition() {
        float partialTicks = Minecraft.getInstance().getFrameTime();
        if(Minecraft.getInstance().isPaused()) {
            partialTicks = 0;
        }
        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        double x = this.lPosX + (this.posX - this.lPosX) * partialTicks;
        double y = this.lPosY + (this.posY - this.lPosY) * partialTicks;
        double z = this.lPosZ + (this.posZ - this.lPosZ) * partialTicks;
        renderX = x - camX;
        renderY = y - camY;
        renderZ = z - camZ;
    }

    @Override
    public boolean alive() {
        return !isDead() && world == Minecraft.getInstance().level;
    }

    public void remove() {
        this.age = this.maxAge + 10;
    }

    @Override
    public Vec3 positionVec() {
        return new Vec3(getPosX(), getPosY(), getPosZ());
    }

    public Vec3 centerVec() {
        return new Vec3(getPosX(), getPosY() + scaleHeight * 0.5, getPosZ());
    }

    @Override
    public AABB boundingBox() {
        return getBoundingBox();
    }

    @Override
    public Level world() {
        return this.world;
    }

    @Override
    public float eyeHeight() {
        return getScale(scaleHeight) * 0.5f;
    }

    @Override
    public Color getColor() {
        return renderer.getPrimaryColor();
    }

    @Override
    public boolean spawnGlowBlock() {
        return false;
    }
}
