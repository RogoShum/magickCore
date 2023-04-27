package com.rogoshum.magickcore.api.enums;

import com.rogoshum.magickcore.api.render.ElementRenderer;
import com.rogoshum.magickcore.api.magick.MagickElement;
import net.minecraft.resources.ResourceLocation;

public enum ParticleType {
    PARTICLE("particle"),
    MIST("mist"),
    TRAIL("trail"),
    ORB("orb");

    private String type;

    private ParticleType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static ResourceLocation getResourceLocation(ParticleType type, MagickElement element) {
        ElementRenderer renderer = element.getRenderer();
        ResourceLocation res = renderer.getParticleTexture();

        if(type == ParticleType.MIST)
            res = renderer.getMistTexture();
        else if(type == ParticleType.PARTICLE)
            res = renderer.getParticleTexture();
        else if(type == ParticleType.ORB)
            res = renderer.getOrbTexture();
        else if(type == ParticleType.TRAIL)
            res = renderer.getTrailTexture();

        return res;
    }
}
