package com.rogoshum.magickcore.client.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.init.ModElements;
import net.minecraft.resources.ResourceLocation;

public class BotaniaRenderer extends ElementRenderer {
    private final ResourceLocation mist = new ResourceLocation("botania:textures/particle/wisp.png");
    private final ResourceLocation[] particle = new ResourceLocation[4];

    public BotaniaRenderer() {
        super(ModElements.BOTANIA);
        ResourceLocation sparkle_0 = new ResourceLocation("botania:textures/particle/sparkle_0.png");
        particle[0] = sparkle_0;
        ResourceLocation sparkle_1 = new ResourceLocation("botania:textures/particle/sparkle_1.png");
        particle[1] = sparkle_1;
        ResourceLocation sparkle_2 = new ResourceLocation("botania:textures/particle/sparkle_2.png");
        particle[2] = sparkle_2;
        ResourceLocation sparkle_3 = new ResourceLocation("botania:textures/particle/sparkle_3.png");
        particle[3] = sparkle_3;
    }

    @Override
    public ResourceLocation getParticleTexture() {
        return particle[MagickCore.rand.nextInt(4)];
    }

    @Override
    public ResourceLocation getMistTexture() {
        return mist;
    }

    @Override
    public float getParticleGravity() {
        return 0.1f;
    }
}
