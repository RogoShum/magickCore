package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.api.enums.ParticleType;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
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

public class ParticlePack extends EntityPack{
    private final String element;
    private final ParticleType texture;
    private final Vector3d position;
    private final float scaleWidth;
    private final float scaleHeight;
    private final float alpha;
    private final int maxAge;
    private final boolean glow;
    private final int trace;
    private final float gravity;
    public ParticlePack(PacketBuffer buffer) {
        super(buffer);
        element = buffer.readString();
        texture = buffer.readEnumValue(ParticleType.class);
        position = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        scaleWidth = buffer.readFloat();
        scaleHeight = buffer.readFloat();
        alpha = buffer.readFloat();
        maxAge = buffer.readInt();
        glow = buffer.readBoolean();
        trace = buffer.readInt();
        gravity = buffer.readFloat();
    }

    public ParticlePack(int id, ParticleType texture, Vector3d position, float scaleWidth, float scaleHeight, float alpha, int maxAge, String element,
                        boolean glow, int trace, float gravity) {
        super(id);
        this.element = element;
        this.texture = texture;
        this.position = position;
        this.scaleHeight = scaleHeight;
        this.scaleWidth = scaleWidth;
        this.alpha = alpha;
        this.maxAge = maxAge;
        this.glow = glow;
        this.trace = trace;
        this.gravity = gravity;
    }

    public void toBytes(PacketBuffer buf) {
        super.toBytes(buf);
        buf.writeString(this.element);
        buf.writeEnumValue(texture);
        buf.writeDouble(position.x);
        buf.writeDouble(position.y);
        buf.writeDouble(position.z);
        buf.writeFloat(scaleWidth);
        buf.writeFloat(scaleHeight);
        buf.writeFloat(alpha);
        buf.writeInt(maxAge);
        buf.writeBoolean(glow);
        buf.writeInt(trace);
        buf.writeFloat(gravity);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;
        ElementRenderer renderer = MagickCore.proxy.getElementRender(element);
        ResourceLocation res = RenderHelper.blankTex;
        if(texture == ParticleType.MIST)
            res = renderer.getMistTexture();
        else if(texture == ParticleType.PARTICLE)
            res = renderer.getParticleTexture();
        else if(texture == ParticleType.ORB)
            res = renderer.getOrbTexture();
        else if(texture == ParticleType.TRAIL)
            res = renderer.getTrailTexture();

        LitParticle par = new LitParticle(Minecraft.getInstance().world, res, position, scaleWidth, scaleHeight, alpha, maxAge, renderer);
        if(glow)
            par.setGlow();
        Entity entity = Minecraft.getInstance().world.getEntityByID(trace);

        if(entity != null)
            par.setTraceTarget(entity);
        par.setParticleGravity(gravity);

        MagickCore.addMagickParticle(par);
    }
}
