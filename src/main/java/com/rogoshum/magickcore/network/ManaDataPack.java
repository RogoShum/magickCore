package com.rogoshum.magickcore.network;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.enums.EnumTargetType;
import com.rogoshum.magickcore.capability.IManaData;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ManaDataPack extends EntityPack{
    private float range;
    private float force;
    private String targetType;
    private String manaType;
    private int tick;
    private UUID traceTarget;
    private String element;

    public ManaDataPack(PacketBuffer buffer) {
        super(buffer);
        element = buffer.readString();
        targetType = buffer.readString();
        manaType = buffer.readString();
        range = buffer.readFloat();
        force = buffer.readFloat();
        tick = buffer.readInt();
        traceTarget = buffer.readUniqueId();
    }

    public ManaDataPack(int id, String element, String targetType, String manaType, float range, float force, UUID traceTarget, int tick) {
        super(id);
        this.element = element;
        this.targetType = targetType;
        this.manaType = manaType;
        this.range = range;
        this.force = force;
        this.traceTarget = traceTarget;
        this.tick = tick;
    }

    public void toBytes(PacketBuffer buf) {
        super.toBytes(buf);
        buf.writeString(this.element);
        buf.writeString(this.targetType);
        buf.writeString(this.manaType);
        buf.writeFloat(this.range);
        buf.writeFloat(this.force);
        buf.writeInt(this.tick);
        buf.writeUniqueId(this.traceTarget);
    }

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;

        Entity entity = Minecraft.getInstance().world.getEntityByID(this.id);
        if(entity == null || entity.removed)
            return;
        IManaData data = entity.getCapability(MagickCore.manaData).orElse(null);
        if(data != null) {
            data.setElement(ModElements.getElement(this.element));
            data.setTraceTarget(this.traceTarget);
            data.setManaType(EnumManaType.getEnum(this.manaType));
            data.setTickTime(this.tick);
            data.setForce(this.force);
            data.setRange(this.range);
            data.setTargetType(EnumTargetType.getEnum(this.targetType));
        }
    }
}
