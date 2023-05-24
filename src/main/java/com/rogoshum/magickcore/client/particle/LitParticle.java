package com.rogoshum.magickcore.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.api.render.easyrender.IEasyRender;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.client.render.instanced.ParticleInstanceRenderer;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.api.render.ElementRenderer;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.nio.FloatBuffer;
import java.util.*;
import java.util.function.Consumer;

public class LitParticle implements ILightSourceEntity, IEasyRender {
    public static final ResourceLocation TEXTURE = MagickCore.fromId("lit_particle_atlas.png");
    public static final TextureAtlas TEXTURE_ATLAS = new TextureAtlas(TEXTURE);
    private static final HashMap<ResourceLocation, ResourceLocation> TEXTURE_MAP = new HashMap<>();
    public static final ParticleInstanceRenderer PARTICLE_INSTANCE_RENDERER = new ParticleInstanceRenderer();
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
    private final ResourceLocation texture;
    private final TextureAtlasSprite sprite;
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
        if(!TEXTURE_MAP.containsKey(texture)) {
            this.texture = new ResourceLocation(texture.getNamespace(), texture.getPath().replace("textures/", "").replace(".png", ""));
            TEXTURE_MAP.put(texture, this.texture);
            RenderSystem.recordRenderCall(() -> {
                TextureAtlas.Preparations preparations = TEXTURE_ATLAS.prepareToStitch(Minecraft.getInstance().getResourceManager(), TEXTURE_MAP.values().stream(), InactiveProfiler.INSTANCE, 0);
                TEXTURE_ATLAS.reload(preparations);
            });
        } else
            this.texture = TEXTURE_MAP.get(texture);

        this.sprite = TEXTURE_ATLAS.getSprite(this.texture);
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

    public static void addTexture(Collection<ResourceLocation> textures) {
        HashSet<ResourceLocation> res = new HashSet<>();
        for(ResourceLocation tex : textures) {
            ResourceLocation atlas = new ResourceLocation(tex.getNamespace(), tex.getPath().replace("textures/", "").replace(".png", ""));
            res.add(atlas);
            TEXTURE_MAP.put(tex, atlas);
        }

        RenderSystem.recordRenderCall(() -> {
            TextureAtlas.Preparations preparations = TEXTURE_ATLAS.prepareToStitch(Minecraft.getInstance().getResourceManager(), TEXTURE_MAP.values().stream(), InactiveProfiler.INSTANCE, 0);
            TEXTURE_ATLAS.reload(preparations);
        });
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
        this.shakeLimit = Math.min(1, Float.parseFloat(String.format("%.1f", shakeLimit*0.2f*Math.min(scaleHeight, scaleWidth))));
        return this;
    }

    public float shakeLimit() {
        return this.shakeLimit;
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
    public ILightSourceEntity getLightEntity() {
        return null;
    }

    @Nullable
    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getLightFunction() {
        return null;
    }

    public RenderMode getRenderMode() {
        type = isGlow ? RenderHelper.getTexturedParticleGlow(TEXTURE, RenderHelper.gl33() ? 0 : shakeLimit) : RenderHelper.getTexturedParticle(TEXTURE, RenderHelper.gl33() ? 0 : shakeLimit);
        return new RenderMode(type, shader);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getDebugFunction() {
        return null;
    }

    public void addVertex(FloatBuffer buffer) {
        buffer.put(shakeLimit);

        buffer.put(getScale(scaleWidth));
        buffer.put(getScale(scaleHeight));

        buffer.put(this.sprite.getU0());
        buffer.put(this.sprite.getU1());
        buffer.put(this.sprite.getV0());
        buffer.put(this.sprite.getV1());

        buffer.put(getColor().r());
        buffer.put(getColor().g());
        buffer.put(getColor().b());
        buffer.put(alpha);

        buffer.put((float) this.posX);
        buffer.put((float) this.posY);
        buffer.put((float) this.posZ);
    }

    public void render(RenderParams renderParams) {
        if(this.sprite == null) return;
        if(RenderHelper.gl33()) {
            PARTICLE_INSTANCE_RENDERER.addInstanceAttrib(this::addVertex);
            return;
        }
        PoseStack matrixStackIn = renderParams.matrixStack;
        BufferBuilder buffer = renderParams.buffer;
        matrixStackIn.pushPose();
        Matrix4f matrix4f = renderParams.matrixStack.last().pose();
        int lightmap = renderContext.packedLightIn;
        updatePosition();
        matrixStackIn.translate(renderX, renderY, renderZ);
        matrixStackIn.scale(getScale(scaleWidth), getScale(scaleHeight), getScale(scaleWidth));
        matrixStackIn.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        float coner = alpha * 0.8f;
        float u0 = this.sprite.getU0();
        float u1 = this.sprite.getU1();
        float v0 = this.sprite.getV0();
        float v1 = this.sprite.getV1();
        float u05 = (u0+u1) * 0.5f;
        float v05 = (v0+v1) * 0.5f;
        Vec3[] quad = RenderHelper.FAN_PARTICLE;
        buffer.vertex(matrix4f, (float) quad[0].x, (float) quad[0].y, (float) quad[0].z).color(color.r(), color.g(), color.b(), alpha).uv(u05, v05)
                .uv2(lightmap).endVertex();
        buffer.vertex(matrix4f, (float) quad[1].x, (float) quad[1].y, (float) quad[1].z).color(color.r(), color.g(), color.b(), coner).uv(u1, v1)
                .uv2(lightmap).endVertex();
        buffer.vertex(matrix4f, (float) quad[2].x, (float) quad[2].y, (float) quad[2].z).color(color.r(), color.g(), color.b(), coner).uv(u1, v0)
                .uv2(lightmap).endVertex();
        buffer.vertex(matrix4f, (float) quad[0].x, (float) quad[0].y, (float) quad[0].z).color(color.r(), color.g(), color.b(), alpha).uv(u05, v05)
                .uv2(lightmap).endVertex();
        buffer.vertex(matrix4f, (float) quad[2].x, (float) quad[2].y, (float) quad[2].z).color(color.r(), color.g(), color.b(), coner).uv(u1, v0)
                .uv2(lightmap).endVertex();
        buffer.vertex(matrix4f, (float) quad[3].x, (float) quad[3].y, (float) quad[3].z).color(color.r(), color.g(), color.b(), coner).uv(u0, v0)
                .uv2(lightmap).endVertex();
        buffer.vertex(matrix4f, (float) quad[0].x, (float) quad[0].y, (float) quad[0].z).color(color.r(), color.g(), color.b(), alpha).uv(u05, v05)
                .uv2(lightmap).endVertex();
        buffer.vertex(matrix4f, (float) quad[3].x, (float) quad[3].y, (float) quad[3].z).color(color.r(), color.g(), color.b(), coner).uv(u0, v0)
                .uv2(lightmap).endVertex();
        buffer.vertex(matrix4f, (float) quad[4].x, (float) quad[4].y, (float) quad[4].z).color(color.r(), color.g(), color.b(), coner).uv(u0, v1)
                .uv2(lightmap).endVertex();
        buffer.vertex(matrix4f, (float) quad[0].x, (float) quad[0].y, (float) quad[0].z).color(color.r(), color.g(), color.b(), alpha).uv(u05, v05)
                .uv2(lightmap).endVertex();
        buffer.vertex(matrix4f, (float) quad[4].x, (float) quad[4].y, (float) quad[4].z).color(color.r(), color.g(), color.b(), coner).uv(u0, v1)
                .uv2(lightmap).endVertex();
        buffer.vertex(matrix4f, (float) quad[1].x, (float) quad[1].y, (float) quad[1].z).color(color.r(), color.g(), color.b(), coner).uv(u1, v1)
                .uv2(lightmap).endVertex();
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
        return color;
    }

    @Override
    public boolean spawnGlowBlock() {
        return false;
    }
}
