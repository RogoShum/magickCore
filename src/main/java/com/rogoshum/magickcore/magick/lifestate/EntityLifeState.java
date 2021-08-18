package com.rogoshum.magickcore.magick.lifestate;

import com.rogoshum.magickcore.MagickCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
