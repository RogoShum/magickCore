package com.rogoshum.magickcore.common.magick.lifestate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;

public class EntityLifeState extends LifeState<Entity>{
    @Override
    public INBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        boolean removed = this.value.removed;
        this.value.removed = false;
        this.value.writeUnlessRemoved(tag);
        this.value.removed = removed;
        return tag;
    }

    @Override
    public void deserialize(INBT value, @Nonnull World world) {
        CompoundNBT tag = (CompoundNBT) value;
        Optional<EntityType<?>> optional = EntityType.readEntityType(tag);
        optional.ifPresent(type -> {
            this.value = type.create(world);
            if(this.value != null)
                this.value.read(tag);
        });
    }
}
