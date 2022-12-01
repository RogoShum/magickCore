package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.common.network.Networking;
import com.rogoshum.magickcore.common.network.ParticlePack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class ParticleBuilder {
    private World world;
    private ParticleType texture;
    private Vector3d position;
    private float scaleWidth;
    private float scaleHeight;
    private float alpha;
    private int maxAge;
    private String element;

    private boolean glow;
    private float grav;
    private int trace;

    private ParticleBuilder(World world, ParticleType texture, Vector3d position, float scaleWidth, float scaleHeight, float alpha, int maxAge, String element)
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

    public ParticleBuilder glow()
    {
        this.glow = true;
        return this;
    }

    public ParticleBuilder grav(float grav)
    {
        this.grav = grav;
        return this;
    }

    public ParticleBuilder trace(int uuid)
    {
        this.trace = uuid;
        return this;
    }

    public static ParticleBuilder create (World world, ParticleType texture, Vector3d position, float scaleWidth, float scaleHeight, float alpha, int maxAge, String element)
    {
        return new ParticleBuilder(world, texture, position, scaleWidth, scaleHeight, alpha, maxAge, element);
    }

    public void send ()
    {
        ParticlePack pack = new ParticlePack(0, texture, position, scaleWidth, scaleHeight, alpha, maxAge, element, glow, trace, grav);
        Networking.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                position.x, position.y, position.z, 32, world.getDimensionKey()
        )), pack);
    }
}
