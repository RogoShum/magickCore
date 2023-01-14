package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.common.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class TakenStatePack extends EntityPack{
    private final UUID uuid;
    private final int time;

    public TakenStatePack(PacketBuffer buffer) {
        super(buffer);
        uuid = buffer.readUUID();
        time = buffer.readInt();
    }

    public TakenStatePack(int id, int time, UUID uuid) {
        super(id);
        this.uuid = uuid;
        this.time = time;
    }

    public void toBytes(PacketBuffer buf) {
        super.toBytes(buf);
        buf.writeUUID(uuid);
        buf.writeInt(time);
    }

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;
        Entity entity = Minecraft.getInstance().level.getEntity(this.id);
        if(entity == null || entity.removed)
            return;
        TakenEntityData state = ExtraDataUtil.takenEntityData(entity);
        if(state != null) {
            state.setTime(this.time);
            state.setOwner(this.uuid);
        }
    }
}
