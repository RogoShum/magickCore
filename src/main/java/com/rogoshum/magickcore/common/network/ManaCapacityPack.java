package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.magick.ManaCapacity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class ManaCapacityPack extends EntityPack<ClientNetworkContext<?>>{
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

    public static void handler(ClientNetworkContext<ManaCapacityPack> context) {
        ManaCapacityPack pack = context.packet();
        Entity entity = Minecraft.getInstance().level.getEntity(pack.id);
        if(entity == null || entity.removed || !(entity instanceof IManaCapacity))
            return;

        ManaCapacity data = ((IManaCapacity) entity).manaCapacity();
        if(data != null) {
            data.deserialize(pack.nbt);
        }
    }
}
