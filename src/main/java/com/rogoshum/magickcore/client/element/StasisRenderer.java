package com.rogoshum.magickcore.client.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.util.ResourceLocation;

public class StasisRenderer extends ElementRenderer {
    private ResourceLocation mist_0 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/stasis/mist/mist_0.png");
    private ResourceLocation mist_1 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/stasis/mist/mist_1.png");
    private ResourceLocation mist_2 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/stasis/mist/mist_2.png");

    private ResourceLocation[] mist = new ResourceLocation[3];

    private ResourceLocation particle = new ResourceLocation(MagickCore.MOD_ID +":textures/element/stasis/particle.png");
    private ResourceLocation sprite = new ResourceLocation(MagickCore.MOD_ID +":element/stasis/particle");

    private ResourceLocation wind = new ResourceLocation(MagickCore.MOD_ID +":textures/element/stasis/wind_center.png");

    public StasisRenderer() {
        super(ModElements.STASIS_COLOR);
        mist[0] = mist_0;
        mist[1] = mist_1;
        mist[2] = mist_2;
    }

    @Override
    public ResourceLocation getMistTexture() {
        return mist[MagickCore.rand.nextInt(3)];
    }

    @Override
    public ResourceLocation getParticleSprite() {
        return sprite;
    }

    @Override
    public ResourceLocation getParticleTexture() {
        return particle;
    }

    @Override
    public ResourceLocation getCycleTexture() {
        return wind;
    }
}
