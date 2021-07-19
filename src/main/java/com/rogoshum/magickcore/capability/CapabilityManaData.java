package com.rogoshum.magickcore.capability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.EnumManaType;
import com.rogoshum.magickcore.api.EnumTargetType;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.UUID;

public class CapabilityManaData {
	public static final String ELEMENT = "ELEMENT";
	public static final String TRACE_TARGET = "TRACE_TARGET";
	public static final String FORCE = "FORCE";
	public static final String TICK = "TICK";
	public static final String RANGE = "RANGE";
	public static final String TARGET_TYPE = "TARGET_TYPE";
	public static final String MANA_TYPE = "MANA_TYPE";

	public static class Storage<T extends IManaData> implements IStorage<T>
    {
		@Override
		public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
			CompoundNBT tag = new CompoundNBT();
			tag.putString(ELEMENT, instance.getElement().getType());
			tag.putUniqueId(TRACE_TARGET, instance.getTraceTarget());
			tag.putFloat(FORCE, instance.getForce());
			tag.putInt(TICK, instance.getTickTime());
			tag.putFloat(RANGE, instance.getRange());
			tag.putString(TARGET_TYPE, instance.getTargetType().getLabel());
			tag.putString(MANA_TYPE, instance.getManaType().getLabel());
			return tag;
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
			CompoundNBT tag = (CompoundNBT) nbt;
			instance.setElement(ModElements.getElement(tag.getString(ELEMENT)));
			if(tag.hasUniqueId(TRACE_TARGET))
				instance.setTraceTarget(tag.getUniqueId(TRACE_TARGET));
			instance.setForce(tag.getFloat(FORCE));
			instance.setTickTime(tag.getInt(TICK));
			instance.setRange(tag.getFloat(RANGE));

			EnumTargetType target = EnumTargetType.getEnum(tag.getString(TARGET_TYPE));
			if(target != null)
				instance.setTargetType(target);

			EnumManaType mana = EnumManaType.getEnum(tag.getString(MANA_TYPE));
			if(mana != null)
				instance.setManaType(mana);
		}
    }

	 public static class Implementation implements IManaData
	 {
		 private float range;
		 private float force;
		 private EnumTargetType targetType;
		 private EnumManaType manaType;
		 private int tick;
		 private UUID traceTarget;
		 private IManaElement element;

		 public Implementation(IManaElement element) {
			 this.element = element;
			 this.targetType = EnumTargetType.NONE;
			 this.manaType = EnumManaType.NONE;
			 this.traceTarget = MagickCore.emptyUUID;
		 }

		 @Override
		 public IManaElement getElement() {
			 return this.element;
		 }

		 @Override
		 public void setElement(IManaElement element) {
			this.element = element;
		 }

		 @Override
		 public UUID getTraceTarget() {
			 if(traceTarget == null)
				 return MagickCore.emptyUUID;
			 return traceTarget;
		 }

		 @Override
		 public void setTraceTarget(UUID uuid) {
			 traceTarget = uuid;
		 }

		 @Override
		 public float getForce() {
			 return force;
		 }

		 @Override
		 public void setForce(float force) {
			 this.force = force;
		 }

		 @Override
		 public int getTickTime() {
			 return tick;
		 }

		 @Override
		 public void setTickTime(int tick) {
			 this.tick = tick;
		 }

		 @Override
		 public float getRange() {
			 return range;
		 }

		 @Override
		 public void setRange(float range) {
			 this.range = range;
		 }

		 @Override
		 public EnumTargetType getTargetType() {
			 if(targetType == null)
				 return EnumTargetType.NONE;
			 return this.targetType;
		 }

		 @Override
		 public void setTargetType(EnumTargetType type) {
			 this.targetType = type;
		 }

		 @Override
		 public EnumManaType getManaType() {
		 	if(manaType == null)
		 		return EnumManaType.NONE;
			 return manaType;
		 }

		 @Override
		 public void setManaType(EnumManaType type) {
			 manaType = type;
		 }
	 }

	public static class ManaDataProvider implements ICapabilitySerializable<INBT> {
		@CapabilityInject(IManaData.class)
		public static Capability<IManaData> capability = null;
		private LazyOptional<IManaData> instance = LazyOptional.of(capability::getDefaultInstance);


		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {

			return cap == capability ? instance.cast() : LazyOptional.empty();
		}

		@Override
		public INBT serializeNBT() {
			return capability.getStorage().writeNBT(capability, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
		}

		@Override
		public void deserializeNBT(INBT nbt) {
			capability.getStorage().readNBT(capability, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
		}
	}
}
