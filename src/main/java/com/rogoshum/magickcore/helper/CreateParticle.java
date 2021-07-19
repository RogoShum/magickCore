package com.rogoshum.magickcore.helper;

import com.rogoshum.magickcore.api.EnumParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class CreateParticle {
    private World world;
    private EnumParticleType texture;
    private Vector3d position;
    private float scaleWidth;
    private float scaleHeight;
    private float alpha;
    private int maxAge;
    private String element;

    private CreateParticle (World world, EnumParticleType texture, Vector3d position, float scaleWidth, float scaleHeight, float alpha, int maxAge, String element)
    {
        this.world = world;
        this.texture = texture;
        this.position = position;
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;
        this.alpha = alpha;
        this.maxAge = maxAge;
        this.element = element;
    }

    public CreateParticle create (World world, EnumParticleType texture, Vector3d position, float scaleWidth, float scaleHeight, float alpha, int maxAge, String element)
    {
        return new CreateParticle(world, texture, position, scaleWidth, scaleHeight, alpha, maxAge, element);
    }

    public void send ()
    {

    }
}
