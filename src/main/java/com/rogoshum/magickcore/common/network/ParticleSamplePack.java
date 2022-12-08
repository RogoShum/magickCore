package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ParticleSamplePack extends EntityPack{
    private final String element;
    private final ParticleType particle;
    private final Vector3d position;
    private final Vector3d motion;
    private final byte type;

    private final float force;
    public ParticleSamplePack(PacketBuffer buffer) {
        super(buffer);
        element = buffer.readString();
        particle = buffer.readEnumValue(ParticleType.class);
        position = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        type = buffer.readByte();
        force = buffer.readFloat();
        motion = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public ParticleSamplePack(int id, ParticleType texture, Vector3d position, float force, byte type, String element, Vector3d motion) {
        super(id);
        this.element = element;
        this.particle = texture;
        this.position = position;
        this.force = force;
        this.type = type;
        this.motion = motion;
    }

    public void toBytes(PacketBuffer buf) {
        super.toBytes(buf);
        buf.writeString(this.element);
        buf.writeEnumValue(particle);
        buf.writeDouble(position.x);
        buf.writeDouble(position.y);
        buf.writeDouble(position.z);
        buf.writeByte(type);
        buf.writeFloat(force);
        buf.writeDouble(motion.x);
        buf.writeDouble(motion.y);
        buf.writeDouble(motion.z);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;
        MagickElement element1 = MagickRegistry.getElement(element);
        if(Minecraft.getInstance().world != null) {
            if(type == 0)
                ParticleUtil.spawnBlastParticle(Minecraft.getInstance().world, position, force, element1, particle);
            else
                ParticleUtil.spawnImpactParticle(Minecraft.getInstance().world, position, force, motion, element1, particle);
        }
    }
}
