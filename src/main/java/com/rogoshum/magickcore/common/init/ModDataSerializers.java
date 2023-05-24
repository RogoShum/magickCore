package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.magick.ManaCapacity;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModDataSerializers {
    public static final DeferredRegister<DataSerializerEntry> DATA_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.DATA_SERIALIZERS, MagickCore.MOD_ID);
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

    public static final EntityDataSerializer<Vec3> VECTOR3D = new EntityDataSerializer<>() {
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

    private static final RegistryObject<DataSerializerEntry> MANA_CAPACITY_REGISTRY = DATA_SERIALIZERS.register("mana_capacity", () -> new DataSerializerEntry(MANA_CAPACITY));
    private static final RegistryObject<DataSerializerEntry> SPELL_CONTEXT_REGISTRY = DATA_SERIALIZERS.register("spell_context", () -> new DataSerializerEntry(SPELL_CONTEXT));
    private static final RegistryObject<DataSerializerEntry> VECTOR3D_REGISTRY = DATA_SERIALIZERS.register("vector3d", () -> new DataSerializerEntry(VECTOR3D));
}
