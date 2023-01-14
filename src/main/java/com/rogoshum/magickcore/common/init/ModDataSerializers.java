package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.magick.ManaCapacity;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.registry.DeferredRegister;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModDataSerializers {
    public static final DeferredRegister<DataSerializerEntry> DATA_SERIALIZERS = DeferredRegister.create(ForgeRegistries.DATA_SERIALIZERS, MagickCore.MOD_ID);
    public static final IDataSerializer<ManaCapacity> MANA_CAPACITY = new IDataSerializer<ManaCapacity>() {
        @Override
        public void write(PacketBuffer buf, ManaCapacity value) {
            CompoundNBT tag = new CompoundNBT();
            value.serialize(tag);
            buf.writeNbt(tag);
        }

        @Override
        public ManaCapacity read(PacketBuffer buf) {
            CompoundNBT tag = buf.readNbt();
            ManaCapacity capacity = ManaCapacity.create(tag);
            if(capacity == null)
                return new ManaCapacity(0);
            return capacity;
        }

        @Override
        public ManaCapacity copy(ManaCapacity value) {
            CompoundNBT tag = new CompoundNBT();
            value.serialize(tag);
            ManaCapacity capacity = ManaCapacity.create(tag);
            if(capacity == null)
                return new ManaCapacity(0);
            return capacity;
        }
    };

    public static final IDataSerializer<SpellContext> SPELL_CONTEXT = new IDataSerializer<SpellContext>() {
        @Override
        public void write(PacketBuffer buf, SpellContext value) {
            CompoundNBT tag = new CompoundNBT();
            value.serialize(tag);
            buf.writeNbt(tag);
        }

        @Override
        public SpellContext read(PacketBuffer buf) {
            CompoundNBT tag = buf.readNbt();
            return SpellContext.create(tag);
        }

        @Override
        public SpellContext copy(SpellContext value) {
            CompoundNBT tag = new CompoundNBT();
            value.serialize(tag);
            return SpellContext.create(tag);
        }
    };

    public static final IDataSerializer<Vector3d> VECTOR3D = new IDataSerializer<Vector3d>() {
        @Override
        public void write(PacketBuffer buf, Vector3d value) {
            buf.writeDouble(value.x);
            buf.writeDouble(value.y);
            buf.writeDouble(value.z);
        }

        @Override
        public Vector3d read(PacketBuffer buf) {
            return new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }

        @Override
        public Vector3d copy(Vector3d value) {
            return new Vector3d(value.x, value.y, value.z);
        }
    };

    private static final RegistryObject<DataSerializerEntry> MANA_CAPACITY_REGISTRY = DATA_SERIALIZERS.register("mana_capacity", () -> new DataSerializerEntry(MANA_CAPACITY));
    private static final RegistryObject<DataSerializerEntry> SPELL_CONTEXT_REGISTRY = DATA_SERIALIZERS.register("spell_context", () -> new DataSerializerEntry(SPELL_CONTEXT));
    private static final RegistryObject<DataSerializerEntry> VECTOR3D_REGISTRY = DATA_SERIALIZERS.register("vector3d", () -> new DataSerializerEntry(VECTOR3D));
}
