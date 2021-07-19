package com.rogoshum.magickcore.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class LitParticle {
    protected double posX;
    protected double posY;
    protected double posZ;
    protected double lPosX;
    protected double lPosY;
    protected double lPosZ;
    private float[] color;
    private float alpha;
    private int age;
    private int maxAge;
    private float scaleWidth;
    private float scaleHeight;
    private ResourceLocation texture;
    private boolean isGlow;
    private static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    protected double motionX;
    protected double motionY;
    protected double motionZ;
    private AxisAlignedBB boundingBox = EMPTY_AABB;
    protected boolean onGround;
    protected boolean canCollide = true;
    private boolean collidedY;
    protected float particleGravity;
    private World world;
    private ElementRenderer renderer;
    private float shakeLimit;
    private boolean limitScale;
    private Entity traceTarget;
    public LitParticle(World world, ResourceLocation texture, Vector3d position, float scaleWidth, float scaleHeight, float alpha, int maxAge, ElementRenderer renderer)
    {
        this.world = world;
        this.texture = texture;
        this.setSize(scaleWidth, scaleHeight);
        this.setPosition(position.x, position.y, position.z);
        this.maxAge = maxAge;
        this.alpha = alpha;
        this.color = renderer.getColor();
        this.particleGravity = renderer.getParticleGravity();
        this.canCollide = renderer.getParticleCanCollide();
        this.renderer = renderer;
    }

    public LitParticle setTraceTarget(Entity traceTarget){this.traceTarget = traceTarget;return this;}
    public LitParticle setLimitScale(){this.limitScale = true;return this;}
    public LitParticle setShakeLimit(float shakeLimit)
    {
        this.shakeLimit = shakeLimit;return this;
    }
    public LitParticle setParticleGravity(float g)
    {
        this.particleGravity = g;return this;
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer.Impl buffer)
    {
        if(this.texture == null) return;
        matrixStackIn.push();
        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        double x = this.lPosX + (this.posX - this.lPosX) * (double) Minecraft.getInstance().getRenderPartialTicks();
        double y = this.lPosY + (this.posY - this.lPosY) * (double) Minecraft.getInstance().getRenderPartialTicks();
        double z = this.lPosZ + (this.posZ - this.lPosZ) * (double) Minecraft.getInstance().getRenderPartialTicks();
        matrixStackIn.translate(x - camX, y - camY, z - camZ);
        matrixStackIn.scale(getScale(scaleWidth), getScale(scaleHeight), getScale(scaleWidth));

        if(shakeLimit > 0.0f)
        {
            if(isGlow)
                RenderHelper.renderParticle(matrixStackIn, buffer.getBuffer(RenderHelper.getTexedOrbGlow(texture)), getAlpha(alpha), color, true, this.toString(), shakeLimit);
            else
                RenderHelper.renderParticle(matrixStackIn, buffer.getBuffer(RenderHelper.getTexedOrb(texture)), getAlpha(alpha), color, true, this.toString(), shakeLimit);
        }
        else
        {
            if(isGlow)
                RenderHelper.renderParticle(matrixStackIn, buffer.getBuffer(RenderHelper.getTexedOrbGlow(texture)), getAlpha(alpha), color);
            else
                RenderHelper.renderParticle(matrixStackIn, buffer.getBuffer(RenderHelper.getTexedOrb(texture)), getAlpha(alpha), color);
        }
        matrixStackIn.pop();
        buffer.finish();
    }

    protected void setSize(float particleWidth, float particleHeight) {
        if (particleWidth != this.scaleWidth || particleHeight != this.scaleHeight) {
            this.scaleWidth = particleWidth;
            this.scaleHeight = particleHeight;
            AxisAlignedBB axisalignedbb = this.getBoundingBox();
            double d0 = (axisalignedbb.minX + axisalignedbb.maxX - (double)particleWidth) / 2.0D;
            double d1 = (axisalignedbb.minZ + axisalignedbb.maxZ - (double)particleWidth) / 2.0D;
            this.setBoundingBox(new AxisAlignedBB(d0, axisalignedbb.minY, d1, d0 + (double)this.scaleWidth, axisalignedbb.minY + (double)this.scaleHeight, d1 + (double)this.scaleWidth));
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
        float f = this.scaleWidth / 2.0F;
        float f1 = this.scaleHeight;
        this.setBoundingBox(new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
    }

    public LitParticle addMotion(double x, double y, double z) {
        this.motionX += x;
        this.motionY += y;
        this.motionZ += z;
        return this;
    }

    public float getAlpha(float alpha) {
        float f = ((float)this.age) / (float)this.maxAge;
        f = Math.min(f, 1.0f);
        return alpha * f;
    }

    public float getScale(float scale) {
        float f = (float)this.age / (float)this.maxAge;
        if(f <= 0.5f)
            f  = 1.0f - f;
        f = 1.0F - f;
        return limitScale && (float)this.age / (float)this.maxAge <= 0.5f ? scale : scale * f * 2f;
    }

    public void tick() {
        this.lPosX = this.posX;
        this.lPosY = this.posY;
        this.lPosZ = this.posZ;
        this.age++;
        this.motionY -= 0.04D * (double)this.particleGravity;

        if(this.traceTarget != null)
        {
            Vector3d vec = this.traceTarget.getPositionVec().add(0, this.traceTarget.getHeight() / 2, 0).subtract(this.posX, this.posY, this.posZ).normalize();
            double length = 0.3;

            this.motionX += (vec.x / 2 + MagickCore.getNegativeToOne()) * length;
            this.motionY += (vec.y / 2 + MagickCore.getNegativeToOne()) * length * 0.1;
            this.motionZ += (vec.z / 2 + MagickCore.getNegativeToOne()) * length;
        }

        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= (double)0.98F;
        this.motionY *= (double)0.98F;
        this.motionZ *= (double)0.98F;
        if (this.onGround) {
            this.motionX *= (double)0.7F;
            this.motionZ *= (double)0.7F;
        }

        renderer.tickParticle(this);
    }

    public void easyTick() { this.age++;}


    public void move(double x, double y, double z) {
        if (!this.collidedY) {
            double d0 = x;
            double d1 = y;
            double d2 = z;
            if (this.canCollide && (x != 0.0D || y != 0.0D || z != 0.0D)) {
                Vector3d vector3d = Entity.collideBoundingBoxHeuristically((Entity)null, new Vector3d(x, y, z), this.getBoundingBox(), this.world, ISelectionContext.dummy(), new ReuseableStream<>(Stream.empty()));
                x = vector3d.x;
                y = vector3d.y;
                z = vector3d.z;
            }

            if (x != 0.0D || y != 0.0D || z != 0.0D) {
                this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
                this.resetPositionToBB();
            }

            if (Math.abs(d1) >= (double)1.0E-5F && Math.abs(y) < (double)1.0E-5F) {
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
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
        this.posY = axisalignedbb.minY;
        this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(AxisAlignedBB bb) {
        this.boundingBox = bb;
    }

    public LitParticle setGlow() { this.isGlow = true;
        return this;}
    public boolean getGlow() { return this.isGlow;}

    public boolean isDead() { return age >= maxAge; }

    public LitParticle setCanCollide(boolean canCollide) {this.canCollide = canCollide;return this;}

    public boolean shouldRender(ClippingHelper camera) {
        AxisAlignedBB axisalignedbb = getBoundingBox().grow(0.5D);
        if (axisalignedbb.hasNaN() || axisalignedbb.getAverageEdgeLength() == 0.0D) {
            axisalignedbb = new AxisAlignedBB(this.posX - 2.0D, this.posY - 2.0D, this.posZ - 2.0D, this.posX + 2.0D, this.posY + 2.0D, this.posZ + 2.0D);
        }
        return camera.isBoundingBoxInFrustum(axisalignedbb);
    }
}
