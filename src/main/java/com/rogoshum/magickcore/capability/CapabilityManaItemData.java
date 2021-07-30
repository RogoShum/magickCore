package com.rogoshum.magickcore.capability;

import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.api.IManaLimit;
import com.rogoshum.magickcore.init.ManaMaterials;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibMaterial;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityManaItemData {
	public static final String ELEMENT = "ELEMENT";
	public static final String MANA = "MANA";
	public static final String FORCE = "FORCE";
	public static final String TICK = "TICK";
	public static final String RANGE = "RANGE";
	public static final String TRACE = "TRACE";
	public static final String MATERIAL = "MATERIAL";
	public static final String MANA_TYPE = "MANA_TYPE";

	public static class Storage<T extends IManaItemData> implements IStorage<T>
    {
		@Override
		public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
			CompoundNBT tag = new CompoundNBT();
			tag.putString(ELEMENT, instance.getElement().getType());
			tag.putFloat(FORCE, instance.getForce());
			tag.putInt(TICK, instance.getTickTime());
			tag.putFloat(RANGE, instance.getRange());
			tag.putString(MANA_TYPE, instance.getManaType().getLabel());
			tag.putFloat(MANA, instance.getMana());
			tag.putBoolean(TRACE, instance.getTrace());
			tag.putString(MATERIAL, instance.getMaterial().getName());
			return tag;
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
			CompoundNBT tag = (CompoundNBT) nbt;
				if(tag.contains(MATERIAL))
					instance.setMaterial(ManaMaterials.getMaterial(tag.getString(MATERIAL)));
			if(tag.contains(ELEMENT))
				instance.setElement(ModElements.getElement(tag.getString(ELEMENT)));
			if(tag.contains(FORCE))
				instance.setForce(tag.getFloat(FORCE));
			if(tag.contains(TICK))
				instance.setTickTime(tag.getInt(TICK));
			if(tag.contains(RANGE))
				instance.setRange(tag.getFloat(RANGE));
			EnumManaType mana = EnumManaType.getEnum(tag.getString(MANA_TYPE));
			if(mana != null)
				instance.setManaType(mana);
			if(tag.contains(MANA))
				instance.setMana(tag.getFloat(MANA));
			if(tag.contains(TRACE))
				instance.setTrace(tag.getBoolean(TRACE));
		}
    }

	 public static class Implementation implements IManaItemData
	 {
		 private float range;
		 private float force;
		 private float mana;
		 private EnumManaType manaType;
		 private int tick;
		 private IManaElement element;
		 private boolean trace;
		 private IManaLimit material;

		 public Implementation(IManaElement element) {
			 this.element = element;
			 this.manaType = EnumManaType.NONE;
			 this.material = ManaMaterials.getMaterial(LibMaterial.ORIGIN);
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
		 public float getForce() {
			 return force;
		 }

		 @Override
		 public void setForce(float force) {
			 this.force = force;
		 }

		 @Override
		 public float getMana() { return mana; }

		 @Override
		 public void setMana(float mana) { this.mana = mana; }

		 @Override
		 public float receiveMana(float mana) {
		 	 float extra = 0;

			 if(this.mana + mana <= this.material.getMana())
			 	this.mana += mana;
			 else
			 {
				 extra = this.mana + mana - this.material.getMana();
				 this.mana = this.material.getMana();
			 }

			 return extra;
		 }

		 @Override
		 public float getMaxMana() {
			 return material.getMana();
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
		 public boolean getTrace() {
			 return this.trace;
		 }

		 @Override
		 public void setTrace(boolean trace) {
			this.trace = trace;
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
			 if(manaType == null)
				 manaType = EnumManaType.NONE;
		 }

		 @Override
		 public IManaLimit getMaterial() {
			 return material;
		 }

		 @Override
		 public void setMaterial(IManaLimit limit) {
			 this.material = limit;
			 this.force = Math.min(Math.max(this.force - 3f, 0), limit.getForce());
			 this.tick = Math.min(Math.max(this.tick - 50, 0), limit.getTick());
			 this.range = Math.min(Math.max(this.range - 4f, 0), limit.getRange());
			 this.mana = 0;
		 }
	 }

	public static class ManaItemDataProvider implements ICapabilitySerializable<INBT> {
		@CapabilityInject(IManaItemData.class)
		public static Capability<IManaItemData> capability = null;
		private LazyOptional<IManaItemData> instance = LazyOptional.of(capability::getDefaultInstance);


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
