package com.rogoshum.magickcore.network;

import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.api.IManaCapacity;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.magick.ManaData;
import com.rogoshum.magickcore.magick.context.SpellContext;
import com.rogoshum.magickcore.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.registry.MagickRegistry;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
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
        nbt = buffer.readCompoundTag();
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
        buf.writeCompoundTag(nbt);
        buf.writeInt(this.slot);
    }

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;

        Entity entity = Minecraft.getInstance().world.getEntityByID(this.id);
        if(entity == null || entity.removed)
            return;
        ItemStack stack = ItemStack.EMPTY;

        if(entity instanceof PlayerEntity)
            stack = ((PlayerEntity)entity).inventory.getStackInSlot(this.slot);

        if(entity instanceof ItemEntity)
            stack = ((ItemEntity)entity).getItem();

        if(stack == ItemStack.EMPTY)
            return;

        ItemManaData data = ExtraDataHelper.itemManaData(stack);
        if(data != null) {
            data.spellContext().deserialize(nbt);
        }
    }
}
