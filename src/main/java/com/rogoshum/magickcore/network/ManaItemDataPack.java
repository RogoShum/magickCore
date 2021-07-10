package com.rogoshum.magickcore.network;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.EnumManaType;
import com.rogoshum.magickcore.api.EnumTargetType;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.capability.IManaData;
import com.rogoshum.magickcore.capability.IManaItemData;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ManaItemDataPack extends EntityPack{
    private float range;
    private float force;
    private float mana;
    private String manaType;
    private int tick;
    private int slot;
    private boolean trace;
    private String element;

    public ManaItemDataPack(PacketBuffer buffer) {
        super(buffer);
        element = buffer.readString();
        manaType = buffer.readString();
        range = buffer.readFloat();
        force = buffer.readFloat();
        tick = buffer.readInt();
        mana = buffer.readFloat();
        trace = buffer.readBoolean();
        slot = buffer.readInt();
    }

    public ManaItemDataPack(int id, int slot, String element, boolean trace, String manaType, float range, float force, float mana, int tick) {
        super(id);
        this.element = element;
        this.manaType = manaType;
        this.range = range;
        this.force = force;
        this.tick = tick;
        this.slot = slot;
        this.trace = trace;
        this.mana = mana;
    }

    public void toBytes(PacketBuffer buf) {
        super.toBytes(buf);
        buf.writeString(this.element);
        buf.writeString(this.manaType);
        buf.writeFloat(this.range);
        buf.writeFloat(this.force);
        buf.writeInt(this.tick);
        buf.writeFloat(this.mana);
        buf.writeBoolean(this.trace);
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

        if(stack == ItemStack.EMPTY || !(stack.getItem() instanceof IManaItem))
            return;

        IManaItem data = (IManaItem) stack.getItem();
        if(data != null) {
            data.setElement(stack, ModElements.getElement(this.element));
            data.setTrace(stack, this.trace);
            data.setManaType(stack, EnumManaType.getEnum(this.manaType));
            data.setTickTime(stack, this.tick);
            data.setForce(stack, this.force);
            data.setRange(stack, this.range);
            data.setMana(stack, this.mana);
        }
    }
}
