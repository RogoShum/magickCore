package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.magick.ManaCapacity;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.registry.DeferredRegister;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.phys.Vec3;

public class ModDataSerializers {
    public static final ModDataSerializers DATA_SERIALIZERS = new ModDataSerializers();

    public void register() {
        EntityDataSerializers.registerSerializer(MANA_CAPACITY);
        EntityDataSerializers.registerSerializer(SPELL_CONTEXT);
        EntityDataSerializers.registerSerializer(VECTOR3D);
    }
    public static final EntityDataSerializer<ManaCapacity> MANA_CAPACITY = new EntityDataSerializer<ManaCapacity>() {
        @Override
        public void write(FriendlyByteBuf buf, ManaCapacity value) {
            CompoundTag tag = new CompoundTag();
            value.serialize(tag);
            buf.writeNbt(tag);
        }

        @Override
        public ManaCapacity read(FriendlyByteBuf buf) {
            CompoundTag tag = buf.readNbt();
            ManaCapacity capacity = ManaCapacity.create(tag);
            if(capacity == null)
                return new ManaCapacity(0);
            return capacity;
        }

        @Override
        public ManaCapacity copy(ManaCapacity value) {
            CompoundTag tag = new CompoundTag();
            value.serialize(tag);
            ManaCapacity capacity = ManaCapacity.create(tag);
            if(capacity == null)
                return new ManaCapacity(0);
            return capacity;
        }
    };

    public static final EntityDataSerializer<SpellContext> SPELL_CONTEXT = new EntityDataSerializer<SpellContext>() {
        @Override
        public void write(FriendlyByteBuf buf, SpellContext value) {
            CompoundTag tag = new CompoundTag();
            value.serialize(tag);
            buf.writeNbt(tag);
        }

        @Override
        public SpellContext read(FriendlyByteBuf buf) {
            CompoundTag tag = buf.readNbt();
            return SpellContext.create(tag);
        }

        @Override
        public SpellContext copy(SpellContext value) {
            CompoundTag tag = new CompoundTag();
            value.serialize(tag);
            return SpellContext.create(tag);
        }
    };

    public static final EntityDataSerializer<Vec3> VECTOR3D = new EntityDataSerializer<Vec3>() {
        @Override
        public void write(FriendlyByteBuf buf, Vec3 value) {
            buf.writeDouble(value.x);
            buf.writeDouble(value.y);
            buf.writeDouble(value.z);
        }

        @Override
        public Vec3 read(FriendlyByteBuf buf) {
            return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }

        @Override
        public Vec3 copy(Vec3 value) {
            return new Vec3(value.x, value.y, value.z);
        }
    };
}
