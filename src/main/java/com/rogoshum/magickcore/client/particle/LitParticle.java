package com.rogoshum.magickcore.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.common.api.render.IEasyRender;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.client.vertex.VertexShakerHelper;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class LitParticle implements ILightSourceEntity, IEasyRender {
    protected double posX;
    protected double posY;
    protected double posZ;
    protected double lPosX;
    protected double lPosY;
    protected double lPosZ;

    protected double renderX;
    protected double renderY;
    protected double renderZ;

    protected Vector3d[] quad = RenderHelper.QuadVector;
    private Color color;
    private final float alpha;
    private float renderAlpha;
    private int age = 1;
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
    private final World world;
    private final ElementRenderer renderer;
    private float shakeLimit;
    private boolean limitScale;

    private boolean noScale;
    private Entity traceTarget;
    private String shader = "";

    private RenderType type;
    private RenderHelper.RenderContext renderContext;

    public LitParticle(World world, ResourceLocation texture, Vector3d position, float scaleWidth, float scaleHeight, float alpha, int maxAge, ElementRenderer renderer) {
        this.world = world;
        this.texture = texture;
        this.setSize(scaleWidth, scaleHeight);
        this.setPosition(position.x, position.y, position.z);
        this.maxAge = Math.max(maxAge, 2);
        this.alpha = alpha;
        this.color = renderer.getColor();
        this.particleGravity = renderer.getParticleGravity();
        this.canCollide = renderer.getParticleCanCollide();
        this.renderer = renderer;
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
        this.shakeLimit = shakeLimit;
        return this;
    }

    public LitParticle setParticleGravity(float g) {
        this.particleGravity = g;
        return this;
    }

    public LitParticle useShader(String shader) {
        this.shader = shader;
        return this;
    }

    protected void setSize(float particleWidth, float particleHeight) {
        if (particleWidth != this.scaleWidth || particleHeight != this.scaleHeight) {
            this.scaleWidth = particleWidth;
            this.scaleHeight = particleHeight;
            AxisAlignedBB axisalignedbb = this.getBoundingBox();
            double d0 = (axisalignedbb.minX + axisalignedbb.maxX - (double) particleWidth) / 2.0D;
            double d1 = (axisalignedbb.minZ + axisalignedbb.maxZ - (double) particleWidth) / 2.0D;
            this.setBoundingBox(new AxisAlignedBB(d0, axisalignedbb.minY, d1, d0 + (double) this.scaleWidth, axisalignedbb.minY + (double) this.scaleHeight, d1 + (double) this.scaleWidth));
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
        this.setBoundingBox(new AxisAlignedBB(x - (double) f, y, z - (double) f, x + (double) f, y + (double) f1, z + (double) f));
    }

    public LitParticle addMotion(double x, double y, double z) {
        this.motionX += x;
        this.motionY += y;
        this.motionZ += z;
        return this;
    }

    public float getAlpha(float alpha) {
        float f = ((float) this.age) / (float) this.maxAge;
        if(f > .7)
            f -= Math.pow(1-f, 1.1);
        f = Math.min(f, 1.0f);
        return alpha * f;
    }

    public float getScale(float scale) {
        if(noScale)
            return scale;
        float f = (float) this.age / (float) this.maxAge;
        if (f <= 0.5f)
            f = 1.0f - f;
        f = 1.0F - f;
        return limitScale && (float) this.age / (float) this.maxAge <= 0.5f ? scale : scale * f * 2f;
    }

    public void tick() {
        this.lPosX = this.posX;
        this.lPosY = this.posY;
        this.lPosZ = this.posZ;
        this.age++;
        this.motionY -= 0.04D * (double) this.particleGravity;

        if (this.traceTarget != null) {
            Vector3d vec = this.traceTarget.getPositionVec().add(0, this.traceTarget.getHeight() / 2, 0).subtract(this.posX, this.posY, this.posZ).normalize();
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
                Vector3d vector3d = Entity.collideBoundingBoxHeuristically((Entity) null, new Vector3d(x, y, z), this.getBoundingBox(), this.world, ISelectionContext.dummy(), new ReuseableStream<>(Stream.empty()));
                x = vector3d.x;
                y = vector3d.y;
                z = vector3d.z;
            }

            if (x != 0.0D || y != 0.0D || z != 0.0D) {
                this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
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

    public boolean shouldRender(ClippingHelper camera) {
        return camera.isBoundingBoxInFrustum(this.boundingBox);
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
    public HashMap<RenderMode, Consumer<RenderParams>> getDebugFunction() {
        return null;
    }

    public void render(RenderParams renderParams) {
        MatrixStack matrixStackIn = renderParams.matrixStack;
        matrixStackIn.push();
        matrixStackIn.translate(renderX, renderY, renderZ);
        matrixStackIn.scale(getScale(scaleWidth), getScale(scaleHeight), getScale(scaleWidth));
        matrixStackIn.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
        if(shakeLimit <= 0.0f) {
            RenderHelper.callQuadVertex(BufferContext.create(matrixStackIn, renderParams.buffer, type).useShader(shader), renderContext);
        } else {
            Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
            BufferBuilder buffer = renderParams.buffer;
            BufferContext context = BufferContext.create(matrixStackIn, buffer, type);
            RenderHelper.begin(context);
            buffer.pos(matrix4f, (float) quad[0].x, (float) quad[0].y, (float) quad[0].z).color(color.r(), color.g(), color.b(), renderAlpha).tex(1.0f, 1.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal((float) quad[0].x, (float) quad[0].y, (float) quad[0].z).endVertex();
            buffer.pos(matrix4f, (float) quad[1].x, (float) quad[1].y, (float) quad[1].z).color(color.r(), color.g(), color.b(), renderAlpha).tex(1.0f, 0.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal((float) quad[1].x, (float) quad[1].y, (float) quad[1].z).endVertex();
            buffer.pos(matrix4f, (float) quad[2].x, (float) quad[2].y, (float) quad[2].z).color(color.r(), color.g(), color.b(), renderAlpha).tex(0.0f, 0.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal((float) quad[2].x, (float) quad[2].y, (float) quad[2].z).endVertex();
            buffer.pos(matrix4f, (float) quad[3].x, (float) quad[3].y, (float) quad[3].z).color(color.r(), color.g(), color.b(), renderAlpha).tex(0.0f, 1.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal((float) quad[3].x, (float) quad[3].y, (float) quad[3].z).endVertex();
            RenderHelper.finish(context);
        }
        matrixStackIn.pop();
    }

    @Override
    public void update() {
        if (this.texture == null) return;

        renderAlpha = getAlpha(this.alpha);
        Vector3d[] QuadVector = RenderHelper.QuadVector;
        Vector3d V0 = QuadVector[0];
        Vector3d V1 = QuadVector[1];
        Vector3d V2 = QuadVector[2];
        Vector3d V3 = QuadVector[3];

        if (shakeLimit > 0.0f) {
            VertexShakerHelper.VertexGroup group = VertexShakerHelper.getGroup(this.toString());
            group.putVertex(QuadVector[0].getX(), QuadVector[0].getY(), QuadVector[0].getZ(), shakeLimit);
            group.putVertex(QuadVector[1].getX(), QuadVector[1].getY(), QuadVector[1].getZ(), shakeLimit);
            group.putVertex(QuadVector[2].getX(), QuadVector[2].getY(), QuadVector[2].getZ(), shakeLimit);
            group.putVertex(QuadVector[3].getX(), QuadVector[3].getY(), QuadVector[3].getZ(), shakeLimit);

            V0 = group.getVertex(QuadVector[0].getX(), QuadVector[0].getY(), QuadVector[0].getZ()).getPositionVec();
            V1 = group.getVertex(QuadVector[1].getX(), QuadVector[1].getY(), QuadVector[1].getZ()).getPositionVec();
            V2 = group.getVertex(QuadVector[2].getX(), QuadVector[2].getY(), QuadVector[2].getZ()).getPositionVec();
            V3 = group.getVertex(QuadVector[3].getX(), QuadVector[3].getY(), QuadVector[3].getZ()).getPositionVec();
        }

        Vector3d[] Quad = new Vector3d[4];
        Quad[0] = V0; Quad[1] = V1; Quad[2] = V2; Quad[3] = V3;
        quad = Quad;
        type = isGlow ? RenderHelper.getTexedOrbGlow(texture) : RenderHelper.getTexedOrb(texture);
        renderContext = new RenderHelper.RenderContext(renderAlpha, color, RenderHelper.renderLight);

        float partialTicks = Minecraft.getInstance().getRenderPartialTicks();
        if(Minecraft.getInstance().isGamePaused()) {
            partialTicks = 0;
        }
        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
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
        return !isDead();
    }

    @Override
    public Vector3d positionVec() {
        return new Vector3d(getPosX(), getPosY(), getPosZ());
    }

    @Override
    public AxisAlignedBB boundingBox() {
        return getBoundingBox();
    }

    @Override
    public World world() {
        return this.world;
    }

    @Override
    public float eyeHeight() {
        return getScale(scaleHeight) / 2;
    }

    @Override
    public Color getColor() {
        return renderer.getColor();
    }

    @Override
    public boolean spawnGlowBlock() {
        return false;
    }
}
