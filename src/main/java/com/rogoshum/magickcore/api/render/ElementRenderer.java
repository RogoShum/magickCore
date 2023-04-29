package com.rogoshum.magickcore.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.magick.Color;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.rogoshum.magickcore.api.magick.MagickElement;
import net.minecraft.resources.ResourceLocation;

public abstract class ElementRenderer {
    private final ResourceLocation orbTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/magick_orb.png");
    private final ResourceLocation starTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/star.png");
    private final ResourceLocation laserTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/laser.png");
    private final ResourceLocation cycleTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/cycle.png");
    private final ResourceLocation mistTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/mist.png");
    private final ResourceLocation trailTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/trail.png");
    private final ResourceLocation particleSprite = new ResourceLocation(MagickCore.MOD_ID + ":textures/particle/magick_particle.png");
    private final ResourceLocation particleTex = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/magick_particle.png");
    private final ResourceLocation runeTex = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/rune.png");

    private final ResourceLocation[] ring = new ResourceLocation[3];
    private final ResourceLocation[] elc = new ResourceLocation[4];
    private final ResourceLocation[] wave = new ResourceLocation[3];
    private final ResourceLocation[] laser = new ResourceLocation[2];
    private final ResourceLocation[] wind = new ResourceLocation[4];
    protected Color primaryColor = Color.create(1, 1, 1);
    protected Color secondaryColor = Color.create(1, 1, 1);

    public ElementRenderer(MagickElement element) {
        this.primaryColor = element.primaryColor();
        this.secondaryColor = element.secondaryColor();
        ResourceLocation ring_0 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ring/ring_0.png");
        ring[0] = ring_0;
        ResourceLocation ring_1 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ring/ring_1.png");
        ring[1] = ring_1;
        ResourceLocation ring_2 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ring/ring_2.png");
        ring[2] = ring_2;

        ResourceLocation elc_0 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/elc/elc_0.png");
        elc[0] = elc_0;
        ResourceLocation elc_1 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/elc/elc_1.png");
        elc[1] = elc_1;
        ResourceLocation elc_2 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/elc/elc_2.png");
        elc[2] = elc_2;
        ResourceLocation elc_3 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/elc/elc_3.png");
        elc[3] = elc_3;

        ResourceLocation wave_0 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/wave/wave_0.png");
        wave[0] = wave_0;
        ResourceLocation wave_1 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/wave/wave_1.png");
        wave[1] = wave_1;
        ResourceLocation wave_2 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/wave/wave_2.png");
        wave[2] = wave_2;

        ResourceLocation laser_0 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/laser/laser_0.png");
        laser[0] = laser_0;
        ResourceLocation laser_1 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/laser/laser_1.png");
        laser[1] = laser_1;

        ResourceLocation wind_0 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/stasis/wind_0.png");
        wind[0] = wind_0;
        ResourceLocation wind_1 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/stasis/wind_1.png");
        wind[1] = wind_1;
        ResourceLocation wind_2 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/stasis/wind_2.png");
        wind[2] = wind_2;
        ResourceLocation wind_3 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/stasis/wind_3.png");
        wind[3] = wind_3;
    }

    public void renderLaserParticle(PoseStack matrix, BufferBuilder bufferIn, ResourceLocation res, float alpha, float length, float laserScale){
        RenderHelper.renderLaserParticle(BufferContext.create(matrix, bufferIn, RenderHelper.getTexedLaserGlint(res, laserScale)), new RenderHelper.RenderContext(alpha, this.primaryColor), length);
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
    public ResourceLocation getRuneTexture() {
        return runeTex;
    }

    public void tickParticle(LitParticle particle) {}

    public Color getPrimaryColor() {
        return this.primaryColor;
    }
    public Color getSecondaryColor() {
        return this.secondaryColor;
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