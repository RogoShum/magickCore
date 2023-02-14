package com.rogoshum.magickcore.client.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.init.ModElements;
import net.minecraft.resources.ResourceLocation;

public class SolarRenderer extends ElementRenderer {
    private ResourceLocation fire_0 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/solar/mist/fire_0.png");
    private ResourceLocation fire_1 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/solar/mist/fire_1.png");
    private ResourceLocation fire_2 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/solar/mist/fire_2.png");
    private ResourceLocation fire_3 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/solar/mist/fire_3.png");
    private ResourceLocation fire_4 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/solar/mist/fire_4.png");
    private ResourceLocation[] fire = new ResourceLocation[5];

    private ResourceLocation particle_0 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/solar/particle/particle_0.png");
    private ResourceLocation particle_1 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/solar/particle/particle_1.png");
    private ResourceLocation particle_2 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/solar/particle/particle_2.png");

    private ResourceLocation particle_0_sprite = new ResourceLocation(MagickCore.MOD_ID +":element/solar/particle/particle_0.png");
    private ResourceLocation particle_1_sprite = new ResourceLocation(MagickCore.MOD_ID +":element/solar/particle/particle_1.png");
    private ResourceLocation particle_2_sprite = new ResourceLocation(MagickCore.MOD_ID +":element/solar/particle/particle_2.png");

    private ResourceLocation[] particle = new ResourceLocation[3];
    private ResourceLocation[] particle_sprite = new ResourceLocation[3];

    public SolarRenderer() {
        super(ModElements.SOLAR_COLOR);
        fire[0] = fire_0;
        fire[1] = fire_1;
        fire[2] = fire_2;
        fire[3] = fire_3;
        fire[4] = fire_4;

        particle[0] = particle_0;
        particle[1] = particle_1;
        particle[2] = particle_2;

        particle_sprite[0] = particle_0_sprite;
        particle_sprite[1] = particle_1_sprite;
        particle_sprite[2] = particle_2_sprite;
    }

    @Override
    public ResourceLocation getMistTexture() {
        return fire[MagickCore.rand.nextInt(5)];
    }

    @Override
    public ResourceLocation getParticleTexture() {
        return particle[MagickCore.rand.nextInt(3)];
    }

    @Override
    public ResourceLocation getParticleSprite() {
        return particle_sprite[MagickCore.rand.nextInt(3)];
    }

    @Override
    public float getParticleGravity() {
        return -0.07f;
    }

    @Override
    public int getParticleRenderTick() {
        return 30;
    }
}
