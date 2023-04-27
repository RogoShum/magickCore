package com.rogoshum.magickcore.api.extradata.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.extradata.EntityExtraData;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.lib.LibBuff;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public class TakenEntityData extends EntityExtraData {
    private UUID owner = MagickCore.emptyUUID;;
    private int time;
    private int range;

    public void setOwner(UUID entityIn) {
        owner = entityIn;
    }

    public UUID getOwnerUUID() {
        return owner;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public int getRange() {
        return range;
    }

    public void tick(Mob entity) {
        range = (int) entity.getAttributeValue(Attributes.FOLLOW_RANGE);
        if(time > 0)
            this.time--;

        if(time == 0)
            this.owner = MagickCore.emptyUUID;
        if(entity.level instanceof ServerLevel) {
            Entity entity1 = ((ServerLevel) entity.level).getEntity(this.owner);
            if(entity1 instanceof LivingEntity) {
                EntityStateData state = ExtraDataUtil.entityStateData(entity1);
                if(state.getBuffList().containsKey(LibBuff.TAKEN_KING)) {
                    entity.heal(state.getBuffList().get(LibBuff.TAKEN_KING).getForce() * 0.02f);
                }
            }
        }
    }

    @Override
    public boolean isEntitySuitable(Entity entity) {
        return entity instanceof Mob;
    }

    @Override
    public void read(CompoundTag nbt) {
        if(nbt.hasUUID("UUID"))
            this.setOwner(nbt.getUUID("UUID"));
        this.setTime(nbt.getInt("TIME"));
    }

    @Override
    public void write(CompoundTag nbt) {
        nbt.putUUID("UUID", this.getOwnerUUID());
        nbt.putInt("TIME", this.getTime());
    }
}
