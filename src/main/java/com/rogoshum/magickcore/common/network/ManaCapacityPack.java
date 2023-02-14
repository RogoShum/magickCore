package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.common.magick.ManaCapacity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ManaCapacityPack extends EntityPack{
    private final CompoundTag nbt;

    public ManaCapacityPack(FriendlyByteBuf buffer) {
        super(buffer);
        nbt = buffer.readNbt();
    }

    public ManaCapacityPack(int id, ManaCapacity manaCapacity) {
        super(id);
        CompoundTag nbt = new CompoundTag();
        manaCapacity.serialize(nbt);
        this.nbt = nbt;
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeNbt(nbt);
    }

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;

        Entity entity = Minecraft.getInstance().level.getEntity(this.id);
        if(entity == null || entity.isRemoved() || !(entity instanceof IManaCapacity))
            return;

        ManaCapacity data = ((IManaCapacity) entity).manaCapacity();
        if(data != null) {
            data.deserialize(nbt);
        }
    }
}
