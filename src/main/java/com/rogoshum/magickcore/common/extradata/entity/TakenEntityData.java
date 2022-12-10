package com.rogoshum.magickcore.common.extradata.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.extradata.EntityExtraData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.lib.LibBuff;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

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

    public void tick(MobEntity entity) {
        range = (int) entity.getAttributeValue(Attributes.FOLLOW_RANGE);
        if(time > 0)
            this.time--;

        if(time == 0)
            this.owner = MagickCore.emptyUUID;
        if(entity.world instanceof ServerWorld) {
            Entity entity1 = ((ServerWorld) entity.world).getEntityByUuid(this.owner);
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
        return entity instanceof MobEntity;
    }

    @Override
    public void read(CompoundNBT nbt) {
        if(nbt.hasUniqueId("UUID"))
            this.setOwner(nbt.getUniqueId("UUID"));
        this.setTime(nbt.getInt("TIME"));
    }

    @Override
    public void write(CompoundNBT nbt) {
        nbt.putUniqueId("UUID", this.getOwnerUUID());
        nbt.putInt("TIME", this.getTime());
    }
}
