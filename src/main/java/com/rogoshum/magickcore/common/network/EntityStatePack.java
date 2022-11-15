package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.init.ModBuff;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Iterator;
import java.util.function.Supplier;

public class EntityStatePack extends EntityPack{
    private final String element;
    private final float shield;
    private final float mana;
    private final float maxShield;
    private final float maxMana;
    private final CompoundNBT effect_tick;
    private final CompoundNBT effect_force;

    public EntityStatePack(PacketBuffer buffer) {
        super(buffer);
        element = buffer.readString();
        shield = buffer.readFloat();
        mana = buffer.readFloat();
        maxShield = buffer.readFloat();
        maxMana = buffer.readFloat();
        effect_tick = buffer.readCompoundTag();
        effect_force = buffer.readCompoundTag();
    }

    public EntityStatePack(int id, String element, float shield, float mana, float maxShield, float maxMana, CompoundNBT effect_tick, CompoundNBT effect_force) {
        super(id);
        this.element = element;
        this.shield = shield;
        this.mana = mana;
        this.maxShield = maxShield;
        this.maxMana = maxMana;
        this.effect_tick = effect_tick;
        this.effect_force = effect_force;
    }

    public void toBytes(PacketBuffer buf) {
        super.toBytes(buf);
        buf.writeString(this.element);
        buf.writeFloat(this.shield);
        buf.writeFloat(this.mana);
        buf.writeFloat(this.maxShield);
        buf.writeFloat(this.maxMana);
        buf.writeCompoundTag(this.effect_tick);
        buf.writeCompoundTag(this.effect_force);
    }

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;
        Entity entity = Minecraft.getInstance().world.getEntityByID(this.id);
        if(entity == null || entity.removed)
            return;
        EntityStateData state = ExtraDataUtil.entityStateData(entity);
        if(state != null) {
            state.setElement(MagickRegistry.getElement(this.element));
            state.setElementShieldMana(this.shield);
            state.setManaValue(this.mana);
            state.setMaxManaValue(this.maxMana);
            state.setMaxElementShieldMana(this.maxShield);

            Iterator<String> tick = effect_tick.keySet().iterator();
            while(tick.hasNext()) {
                String type = tick.next();
                if(effect_force.contains(type))
                {
                    state.applyBuff(ModBuff.getBuff(type).setTick(effect_tick.getInt(type)).setForce(effect_force.getFloat(type)));
                }
            }
        }
    }
}
