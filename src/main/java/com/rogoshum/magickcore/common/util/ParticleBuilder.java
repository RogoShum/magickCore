package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.network.Networking;
import com.rogoshum.magickcore.common.network.ParticlePack;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class ParticleBuilder {
    private final World world;
    private final ParticleType texture;
    private final Vector3d position;
    private Vector3d motion = Vector3d.ZERO;
    private final float scaleWidth;
    private final float scaleHeight;
    private final float alpha;
    private final int maxAge;
    private final String element;

    private boolean glow;
    private boolean limitSize;
    private Color color;
    private float grav;
    private int trace;
    private float shake;

    private boolean canCollide = true;

    private ParticleBuilder(World world, ParticleType texture, Vector3d position, float scaleWidth, float scaleHeight, float alpha, int maxAge, String element)
    {
        this.texture = texture;
        this.position = position;
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;
        this.alpha = alpha;
        this.maxAge = maxAge;
        this.element = element;
        this.color = MagickRegistry.getElement(element).color();
        this.world = world;
    }

    public ParticleBuilder glow()
    {
        this.glow = true;
        return this;
    }

    public ParticleBuilder limitSize()
    {
        this.limitSize = true;
        return this;
    }

    public ParticleBuilder shake(float shakeLimit)
    {
        this.shake = shakeLimit;
        return this;
    }

    public ParticleBuilder color(Color color)
    {
        this.color = color;
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

    public ParticleBuilder motion(Vector3d motion) {
        this.motion = motion;
        return this;
    }

    public ParticleBuilder canCollide(boolean canCollide) {
        this.canCollide = canCollide;
        return this;
    }

    public static ParticleBuilder create (World world, ParticleType texture, Vector3d position, float scaleWidth, float scaleHeight, float alpha, int maxAge, String element) {
        return new ParticleBuilder(world, texture, position, scaleWidth, scaleHeight, alpha, maxAge, element);
    }

    public void send () {
        ParticlePack pack = new ParticlePack(0, texture, position, scaleWidth, scaleHeight, alpha, maxAge, element, glow, trace, grav, limitSize, motion, canCollide, color, shake);
        Networking.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                position.x, position.y, position.z, 32, world.getDimensionKey()
        )), pack);
    }
}
