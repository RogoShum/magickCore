package com.rogoshum.magickcore.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class EntityCompoundTagPack extends EntityPack{
    private final CompoundNBT nbt;

    public EntityCompoundTagPack(PacketBuffer buffer) {
        super(buffer);
        nbt = buffer.readCompoundTag();
    }

    public EntityCompoundTagPack(int id, Entity entity) {
        super(id);
        CompoundNBT nbt = new CompoundNBT();
        entity.writeUnlessRemoved(nbt);
        this.nbt = nbt;
    }

    public void toBytes(PacketBuffer buf) {
        super.toBytes(buf);
        buf.writeCompoundTag(nbt);
    }

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;

        Entity entity = Minecraft.getInstance().world.getEntityByID(this.id);
        if(entity == null || entity.removed)
            return;

        entity.read(nbt);
    }
}
