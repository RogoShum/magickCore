package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

public class ElementAnimalPack extends EntityPack<ClientNetworkContext<?>>{
    private final String element;

    public ElementAnimalPack(FriendlyByteBuf buffer) {
        super(buffer);
        element = buffer.readUtf();
    }

    public ElementAnimalPack(int id, String element) {
        super(id);
        this.element = element;
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeUtf(this.element);
    }

    public static void handler(ClientNetworkContext<ElementAnimalPack> context) {
        ElementAnimalPack pack = context.packet();
        Entity entity = Minecraft.getInstance().level.getEntity(pack.id);
        if(entity == null || entity.removed)
            return;
        EntityStateData state = ExtraDataUtil.entityStateData(entity);
        if(state != null) {
            state.setElement(MagickRegistry.getElement(pack.element));
        }
    }
}
