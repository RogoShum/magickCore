package com.rogoshum.magickcore.helper;

import com.rogoshum.magickcore.enums.EnumParticleType;
import com.rogoshum.magickcore.network.Networking;
import com.rogoshum.magickcore.network.ParticlePack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class CreateParticle {
    private World world;
    private EnumParticleType texture;
    private Vector3d position;
    private float scaleWidth;
    private float scaleHeight;
    private float alpha;
    private int maxAge;
    private String element;

    private boolean glow;
    private float grav;
    private int trace;

    private CreateParticle (World world, EnumParticleType texture, Vector3d position, float scaleWidth, float scaleHeight, float alpha, int maxAge, String element)
    {
        this.texture = texture;
        this.position = position;
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;
        this.alpha = alpha;
        this.maxAge = maxAge;
        this.element = element;
        this.world = world;
    }

    public CreateParticle glow()
    {
        this.glow = true;
        return this;
    }

    public CreateParticle grav(float grav)
    {
        this.grav = grav;
        return this;
    }

    public CreateParticle trace(int uuid)
    {
        this.trace = uuid;
        return this;
    }

    public CreateParticle create (World world, EnumParticleType texture, Vector3d position, float scaleWidth, float scaleHeight, float alpha, int maxAge, String element)
    {
        return new CreateParticle(world, texture, position, scaleWidth, scaleHeight, alpha, maxAge, element);
    }

    public void send ()
    {
        ParticlePack pack = new ParticlePack(0, texture, position, scaleWidth, scaleHeight, alpha, maxAge, element, glow, trace, grav);
        Networking.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                position.x, position.y, position.z, 32, world.getDimensionKey()
        )), pack);
    }
}
