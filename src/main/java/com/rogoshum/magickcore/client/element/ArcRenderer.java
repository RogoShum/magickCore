package com.rogoshum.magickcore.client.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.util.ResourceLocation;

public class ArcRenderer extends ElementRenderer {
    private ResourceLocation sprite = new ResourceLocation(MagickCore.MOD_ID +":element/base/trail");

    private ResourceLocation elec_0 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/arc/mist/elec_0.png");
    private ResourceLocation elec_1 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/arc/mist/elec_1.png");
    private ResourceLocation elec_2 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/arc/mist/elec_2.png");
    private ResourceLocation elec_3 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/arc/mist/elec_3.png");
    private ResourceLocation elec_4 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/arc/mist/elec_4.png");
    private ResourceLocation elec_5 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/arc/mist/elec_5.png");
    private ResourceLocation elec_6 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/arc/mist/elec_6.png");
    private ResourceLocation elec_7 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/arc/mist/elec_7.png");

    private ResourceLocation trailTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/trail.png");

    private ResourceLocation[] mist = new ResourceLocation[8];

    public ArcRenderer() {
        super(ModElements.ARC_COLOR);
        mist[0] = elec_0;
        mist[1] = elec_1;
        mist[2] = elec_2;
        mist[3] = elec_3;
        mist[4] = elec_4;
        mist[5] = elec_5;
        mist[6] = elec_6;
        mist[7] = elec_7;
    }

    @Override
    public ResourceLocation getParticleTexture() {
        return trailTex;
    }

    @Override
    public ResourceLocation getParticleSprite() {
        return sprite;
    }

    @Override
    public ResourceLocation getMistTexture() {
        return mist[MagickCore.rand.nextInt(8)];
    }

    @Override
    public float getParticleGravity() {
        return 0.07f;
    }

    @Override
    public int getParticleRenderTick() {
        return 15;
    }
}
