package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.network.Networking;
import com.rogoshum.magickcore.common.network.ParticlePack;
import com.rogoshum.magickcore.common.network.SimpleChannel;
import com.rogoshum.magickcore.common.registry.MagickRegistry;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleBuilder {
    private final Level world;
    private final ParticleType texture;
    private final Vec3 position;
    private Vec3 motion = Vec3.ZERO;
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

    private ParticleBuilder(Level world, ParticleType texture, Vec3 position, float scaleWidth, float scaleHeight, float alpha, int maxAge, String element)
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

    public ParticleBuilder motion(Vec3 motion) {
        this.motion = motion;
        return this;
    }

    public ParticleBuilder canCollide(boolean canCollide) {
        this.canCollide = canCollide;
        return this;
    }

    public static ParticleBuilder create (Level world, ParticleType texture, Vec3 position, float scaleWidth, float scaleHeight, float alpha, int maxAge, String element) {
        return new ParticleBuilder(world, texture, position, scaleWidth, scaleHeight, alpha, maxAge, element);
    }

    public void send () {
        ParticlePack pack = new ParticlePack(0, texture, position, scaleWidth, scaleHeight, alpha, maxAge, element, glow, trace, grav, limitSize, motion, canCollide, color, shake);
        Networking.INSTANCE.send(SimpleChannel.SendType.server(PlayerLookup.tracking((ServerLevel) world, new BlockPos(position))), pack);
    }
}
