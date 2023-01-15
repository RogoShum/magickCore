package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.magick.ManaCapacity;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ManaItemDataPack extends EntityPack<ClientNetworkContext<?>>{
    private final CompoundTag nbt;
    private final int slot;

    public ManaItemDataPack(FriendlyByteBuf buffer) {
        super(buffer);
        nbt = buffer.readNbt();
        slot = buffer.readInt();
    }

    public ManaItemDataPack(int id, int slot, SpellContext spellContext) {
        super(id);
        CompoundTag nbt = new CompoundTag();
        spellContext.serialize(nbt);
        this.nbt = nbt;
        this.slot = slot;
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeNbt(nbt);
        buf.writeInt(this.slot);
    }

    public static void handler(ClientNetworkContext<ManaItemDataPack> context) {
        ManaItemDataPack pack = context.packet();
        Entity entity = Minecraft.getInstance().level.getEntity(pack.id);
        if(entity == null || entity.removed)
            return;
        ItemStack stack = ItemStack.EMPTY;

        if(entity instanceof Player)
            stack = ((Player)entity).inventory.getItem(pack.slot);

        if(entity instanceof ItemEntity)
            stack = ((ItemEntity)entity).getItem();

        if(stack == ItemStack.EMPTY)
            return;

        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        data.spellContext().deserialize(pack.nbt);
    }
}
