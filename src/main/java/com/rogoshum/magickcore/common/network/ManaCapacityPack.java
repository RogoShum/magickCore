package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.common.magick.ManaCapacity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ManaCapacityPack extends EntityPack{
    private final CompoundNBT nbt;

    public ManaCapacityPack(PacketBuffer buffer) {
        super(buffer);
        nbt = buffer.readNbt();
    }

    public ManaCapacityPack(int id, ManaCapacity manaCapacity) {
        super(id);
        CompoundNBT nbt = new CompoundNBT();
        manaCapacity.serialize(nbt);
        this.nbt = nbt;
    }

    public void toBytes(PacketBuffer buf) {
        super.toBytes(buf);
        buf.writeNbt(nbt);
    }

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;

        Entity entity = Minecraft.getInstance().level.getEntity(this.id);
        if(entity == null || entity.removed || !(entity instanceof IManaCapacity))
            return;

        ManaCapacity data = ((IManaCapacity) entity).manaCapacity();
        if(data != null) {
            data.deserialize(nbt);
        }
    }
}
