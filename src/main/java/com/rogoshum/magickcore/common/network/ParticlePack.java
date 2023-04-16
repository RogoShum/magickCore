package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ParticlePack extends EntityPack{
    private final String element;
    private final ParticleType texture;
    private final Vec3 position;
    private final Vec3 motion;
    private final float scaleWidth;
    private final float scaleHeight;
    private final float alpha;
    private final int maxAge;
    private final boolean glow;
    private final boolean limitSize;
    private final int trace;
    private final float gravity;
    private final boolean canCollide;
    private final Color color;
    private final float shake;
    public ParticlePack(FriendlyByteBuf buffer) {
        super(buffer);
        element = buffer.readUtf();
        texture = buffer.readEnum(ParticleType.class);
        position = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        scaleWidth = buffer.readFloat();
        scaleHeight = buffer.readFloat();
        alpha = buffer.readFloat();
        maxAge = buffer.readInt();
        glow = buffer.readBoolean();
        trace = buffer.readInt();
        gravity = buffer.readFloat();
        limitSize = buffer.readBoolean();
        motion = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        canCollide = buffer.readBoolean();
        color = Color.create(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        shake = buffer.readFloat();
    }

    public ParticlePack(int id, ParticleType texture, Vec3 position, float scaleWidth, float scaleHeight, float alpha, int maxAge, String element,
                        boolean glow, int trace, float gravity, boolean limitSize, Vec3 motion, boolean canCollide, Color color, float shake) {
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
        this.limitSize = limitSize;
        this.motion = motion;
        this.canCollide = canCollide;
        this.color = color;
        this.shake = shake;
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeUtf(this.element);
        buf.writeEnum(texture);
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
        buf.writeBoolean(limitSize);
        buf.writeDouble(motion.x);
        buf.writeDouble(motion.y);
        buf.writeDouble(motion.z);
        buf.writeBoolean(canCollide);
        buf.writeFloat(color.r());
        buf.writeFloat(color.g());
        buf.writeFloat(color.b());
        buf.writeFloat(shake);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;
        MagickElement element1 = MagickRegistry.getElement(element);
        ElementRenderer renderer = MagickCore.proxy.getElementRender(element);
        ResourceLocation res = ParticleType.getResourceLocation(texture, element1);

        LitParticle par = new LitParticle(Minecraft.getInstance().level, res, position, scaleWidth, scaleHeight, alpha, maxAge, renderer);
        if(glow)
            par.setGlow();
        if(limitSize)
            par.setLimitScale();
        par.addMotion(motion.x, motion.y, motion.z);
        Entity entity = Minecraft.getInstance().level.getEntity(trace);

        if(entity != null)
            par.setTraceTarget(entity);
        par.setParticleGravity(gravity);
        par.setCanCollide(canCollide);
        par.setColor(color);
        if(shake > 0)
            par.setShakeLimit(shake);
        MagickCore.addMagickParticle(par);
    }
}
