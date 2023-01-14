package com.rogoshum.magickcore.client.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.init.ModElements;
import net.minecraft.util.ResourceLocation;

public class WitherRenderer extends ElementRenderer {
    private ResourceLocation mist_0 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/wither/mist/mist_0.png");
    private ResourceLocation mist_1 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/wither/mist/mist_1.png");
    private ResourceLocation mist_2 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/wither/mist/mist_2.png");

    private ResourceLocation[] mist = new ResourceLocation[3];

    private ResourceLocation throns_0 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/wither/cycle/throns_0.png");
    private ResourceLocation throns_1 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/wither/cycle/throns_1.png");
    private ResourceLocation throns_2 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/wither/cycle/throns_2.png");

    private ResourceLocation[] throns = new ResourceLocation[3];

    private ResourceLocation particle_0 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/wither/particle_0.png");
    private ResourceLocation particle_1 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/wither/particle_1.png");
    private ResourceLocation particle_2 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/wither/particle_2.png");

    private ResourceLocation[] particle = new ResourceLocation[3];

    public WitherRenderer() {
        super(ModElements.WITHER_COLOR);
        mist[0] = mist_0;
        mist[1] = mist_1;
        mist[2] = mist_2;

        throns[0] = throns_0;
        throns[1] = throns_1;
        throns[2] = throns_2;

        particle[0] = particle_0;
        particle[1] = particle_1;
        particle[2] = particle_2;
    }

    @Override
    public ResourceLocation getParticleTexture() {
        return particle[MagickCore.rand.nextInt(3)];
    }

    @Override
    public ResourceLocation getMistTexture() {
        return mist[MagickCore.rand.nextInt(3)];
    }

    @Override
    public ResourceLocation getCycleTexture() {
        return throns[MagickCore.rand.nextInt(3)];
    }

    @Override
    public float getParticleGravity() {
        return 0.1f;
    }
}
