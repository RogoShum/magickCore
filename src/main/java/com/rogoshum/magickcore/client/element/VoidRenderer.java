package com.rogoshum.magickcore.client.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.util.ResourceLocation;

public class VoidRenderer extends ElementRenderer {
    private ResourceLocation mist_0 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/void/mist/void_0.png");
    private ResourceLocation mist_1 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/void/mist/void_1.png");
    private ResourceLocation mist_2 = new ResourceLocation(MagickCore.MOD_ID +":textures/element/void/mist/void_2.png");

    private ResourceLocation[] mist = new ResourceLocation[3];

    public VoidRenderer() {
        super(ModElements.VOID_COLOR);

        mist[0] = mist_0;
        mist[1] = mist_1;
        mist[2] = mist_2;
    }

    @Override
    public ResourceLocation getMistTexture() {
        return mist[MagickCore.rand.nextInt(3)];
    }

    @Override
    public boolean getParticleCanCollide() {
        return false;
    }

    @Override
    public float getParticleGravity() {
        return -0.05f;
    }
}
