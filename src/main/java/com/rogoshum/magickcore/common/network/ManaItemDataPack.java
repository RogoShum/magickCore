package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ManaItemDataPack extends EntityPack{
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

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;

        Entity entity = Minecraft.getInstance().level.getEntity(this.id);
        if(entity == null || entity.isRemoved())
            return;
        ItemStack stack = ItemStack.EMPTY;

        if(entity instanceof Player)
            stack = ((Player)entity).getInventory().getItem(this.slot);

        if(entity instanceof ItemEntity)
            stack = ((ItemEntity)entity).getItem();

        if(stack == ItemStack.EMPTY)
            return;

        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        if(data != null) {
            data.spellContext().deserialize(nbt);
        }
    }
}
