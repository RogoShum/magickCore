package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ManaItemDataPack extends EntityPack{
    private final CompoundNBT nbt;
    private final int slot;

    public ManaItemDataPack(PacketBuffer buffer) {
        super(buffer);
        nbt = buffer.readNbt();
        slot = buffer.readInt();
    }

    public ManaItemDataPack(int id, int slot, SpellContext spellContext) {
        super(id);
        CompoundNBT nbt = new CompoundNBT();
        spellContext.serialize(nbt);
        this.nbt = nbt;
        this.slot = slot;
    }

    public void toBytes(PacketBuffer buf) {
        super.toBytes(buf);
        buf.writeNbt(nbt);
        buf.writeInt(this.slot);
    }

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;

        Entity entity = Minecraft.getInstance().level.getEntity(this.id);
        if(entity == null || entity.removed)
            return;
        ItemStack stack = ItemStack.EMPTY;

        if(entity instanceof PlayerEntity)
            stack = ((PlayerEntity)entity).inventory.getItem(this.slot);

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
