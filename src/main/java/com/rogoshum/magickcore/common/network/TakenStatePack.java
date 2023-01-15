package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

import java.util.UUID;
import java.util.function.Supplier;

public class TakenStatePack extends EntityPack<ClientNetworkContext<?>>{
    private final UUID uuid;
    private final int time;

    public TakenStatePack(FriendlyByteBuf buffer) {
        super(buffer);
        uuid = buffer.readUUID();
        time = buffer.readInt();
    }

    public TakenStatePack(int id, int time, UUID uuid) {
        super(id);
        this.uuid = uuid;
        this.time = time;
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeUUID(uuid);
        buf.writeInt(time);
    }

    public static void handler(ClientNetworkContext<TakenStatePack> context) {
        TakenStatePack pack = context.packet();
        Entity entity = Minecraft.getInstance().level.getEntity(pack.id);
        if(entity == null || entity.removed)
            return;
        TakenEntityData state = ExtraDataUtil.takenEntityData(entity);
        if(state != null) {
            state.setTime(pack.time);
            state.setOwner(pack.uuid);
        }
    }
}
