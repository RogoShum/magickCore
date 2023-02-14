package com.rogoshum.magickcore.client.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.vertex.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.magick.Color;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.resources.ResourceLocation;

public abstract class ElementRenderer {
    private ResourceLocation orbTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/magick_orb.png");
    private ResourceLocation starTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/star.png");
    private ResourceLocation laserTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/laser.png");
    private ResourceLocation cycleTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/cycle.png");
    private ResourceLocation mistTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/mist.png");
    private ResourceLocation trailTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/trail.png");
    private ResourceLocation particleSprite = new ResourceLocation(MagickCore.MOD_ID + ":textures/particle/magick_particle.png");
    private ResourceLocation particleTex = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/magick_particle.png");

    private ResourceLocation ring_0 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/ring/ring_0.png");
    private ResourceLocation ring_1 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/ring/ring_1.png");
    private ResourceLocation ring_2 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/ring/ring_2.png");

    private ResourceLocation elc_0 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/elc/elc_0.png");
    private ResourceLocation elc_1 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/elc/elc_1.png");
    private ResourceLocation elc_2 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/elc/elc_2.png");
    private ResourceLocation elc_3 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/elc/elc_3.png");

    private ResourceLocation wave_0 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/wave/wave_0.png");
    private ResourceLocation wave_1 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/wave/wave_1.png");
    private ResourceLocation wave_2 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/wave/wave_2.png");

    private ResourceLocation laser_0 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/laser/laser_0.png");
    private ResourceLocation laser_1 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/laser/laser_1.png");

    private ResourceLocation wind_0 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/stasis/wind_0.png");
    private ResourceLocation wind_1 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/stasis/wind_1.png");
    private ResourceLocation wind_2 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/stasis/wind_2.png");
    private ResourceLocation wind_3 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/stasis/wind_3.png");

    private ResourceLocation[] ring = new ResourceLocation[3];
    private ResourceLocation[] elc = new ResourceLocation[4];
    private ResourceLocation[] wave = new ResourceLocation[3];
    private ResourceLocation[] laser = new ResourceLocation[2];
    private ResourceLocation[] wind = new ResourceLocation[4];
    protected Color color = Color.create(1, 1, 1);

    public ElementRenderer(Color color) {
        this.color = color;
        ring[0] = ring_0;
        ring[1] = ring_1;
        ring[2] = ring_2;

        elc[0] = elc_0;
        elc[1] = elc_1;
        elc[2] = elc_2;
        elc[3] = elc_3;

        wave[0] = wave_0;
        wave[1] = wave_1;
        wave[2] = wave_2;

        laser[0] = laser_0;
        laser[1] = laser_1;

        wind[0] = wind_0;
        wind[1] = wind_1;
        wind[2] = wind_2;
        wind[3] = wind_3;
    }

    public void renderLaserParticle(PoseStack matrix, BufferBuilder bufferIn, ResourceLocation res, float alpha, float length, float laserScale){
        RenderHelper.renderLaserParticle(BufferContext.create(matrix, bufferIn, RenderHelper.getTexedLaserGlint(res, laserScale)), new RenderHelper.RenderContext(alpha, this.color), RenderHelper.EmptyVertexContext, length);
    }

    public ResourceLocation getRingTexture() {
        return ring[MagickCore.rand.nextInt(ring.length)];
    }
    public ResourceLocation getLaserBeamTexture() {
        return laser[MagickCore.rand.nextInt(laser.length)];
    }

    public ResourceLocation getWaveTexture(int i) {
        return wave[i];
    }

    public ResourceLocation getWaveTexture() {
        return wave[MagickCore.rand.nextInt(wave.length)];
    }
    public ResourceLocation getElcTexture() {
        return elc[MagickCore.rand.nextInt(elc.length)];
    }

    public ResourceLocation getElcTexture(int i) {
        return elc[i];
    }

    public ResourceLocation getWindTexture(int  i) {
        return wind[i];
    }

    public ResourceLocation getStarTexture() { return this.starTex; }
    public ResourceLocation getParticleTexture() { return this.particleTex; }
    public ResourceLocation getParticleSprite() { return this.particleSprite; }
    public ResourceLocation getOrbTexture() { return this.orbTex; }
    public ResourceLocation getLaserTexture() { return this.laserTex; }
    public ResourceLocation getCycleTexture() { return this.cycleTex; }
    public ResourceLocation getMistTexture() { return this.mistTex; }
    public ResourceLocation getTrailTexture() { return this.particleTex; }

    public void tickParticle(LitParticle particle) {}

    public Color getColor() {
        return this.color;
    }

    public float getParticleGravity()
    {
        return 0;
    }

    public int getParticleRenderTick()
    {
        return 60;
    }

    public boolean getParticleCanCollide()
    {
        return true;
    }
}