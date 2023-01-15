package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class ParticleSamplePack extends EntityPack<ClientNetworkContext<?>>{
    private final String element;
    private final ParticleType particle;
    private final Vec3 position;
    private final Vec3 motion;
    private final byte type;

    private final float force;
    public ParticleSamplePack(FriendlyByteBuf buffer) {
        super(buffer);
        element = buffer.readUtf();
        particle = buffer.readEnum(ParticleType.class);
        position = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        type = buffer.readByte();
        force = buffer.readFloat();
        motion = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public ParticleSamplePack(int id, ParticleType texture, Vec3 position, float force, byte type, String element, Vec3 motion) {
        super(id);
        this.element = element;
        this.particle = texture;
        this.position = position;
        this.force = force;
        this.type = type;
        this.motion = motion;
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeUtf(this.element);
        buf.writeEnum(particle);
        buf.writeDouble(position.x);
        buf.writeDouble(position.y);
        buf.writeDouble(position.z);
        buf.writeByte(type);
        buf.writeFloat(force);
        buf.writeDouble(motion.x);
        buf.writeDouble(motion.y);
        buf.writeDouble(motion.z);
    }

    public static void handler(ClientNetworkContext<ParticleSamplePack> context) {
        ParticleSamplePack pack = context.packet();
        MagickElement element1 = MagickRegistry.getElement(pack.element);
        if(Minecraft.getInstance().level != null) {
            if(pack.type == 0)
                ParticleUtil.spawnBlastParticle(Minecraft.getInstance().level, pack.position, pack.force, element1, pack.particle);
            else if(pack.type == 1)
                ParticleUtil.spawnImpactParticle(Minecraft.getInstance().level, pack.position, pack.force, pack.motion, element1, pack.particle);
            else if(pack.type == 2)
                ParticleUtil.spawnRaiseParticle(Minecraft.getInstance().level, pack.position, pack.force, element1, pack.particle);
        }
    }
}
