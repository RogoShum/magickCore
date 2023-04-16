package com.rogoshum.magickcore.common.extradata.entity;

import com.rogoshum.magickcore.common.extradata.EntityExtraData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

public class PlayerTradeUnlock extends EntityExtraData {
    private final Set<EntityType<?>> entityType = new HashSet<>();
    @Override
    public boolean isEntitySuitable(Entity entity) {
        return entity instanceof Player;
    }

    public Set<EntityType<?>> getUnLock() {
        return entityType;
    }

    public boolean isUnLocked(EntityType<?> type) {
        return entityType.contains(type);
    }

    public void addUnlock(EntityType<?> type) {
        entityType.add(type);
    }

    @Override
    public void read(CompoundTag nbt) {
        for (String key : nbt.getAllKeys()) {
            EntityType<?> type = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(key));
            if(type != null)
                addUnlock(type);
        }
    }

    @Override
    public void write(CompoundTag nbt) {
        for (EntityType<?> type : entityType) {
            nbt.putByte(type.getRegistryName().toString(), (byte)1);
        }
    }
}
