package com.rogoshum.magickcore.capability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.EnumManaType;
import com.rogoshum.magickcore.api.EnumTargetType;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.helper.RoguelikeHelper;
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

public class CapabilityManaItemData {
	public static final String ELEMENT = "ELEMENT";
	public static final String MANA = "MANA";
	public static final String FORCE = "FORCE";
	public static final String TICK = "TICK";
	public static final String RANGE = "RANGE";
	public static final String TRACE = "TRACE";
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
			return tag;
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
			CompoundNBT tag = (CompoundNBT) nbt;
			instance.setElement(ModElements.getElement(tag.getString(ELEMENT)));
			instance.setForce(tag.getFloat(FORCE));
			instance.setTickTime(tag.getInt(TICK));
			instance.setRange(tag.getFloat(RANGE));
			EnumManaType mana = EnumManaType.getEnum(tag.getString(MANA_TYPE));
			if(mana != null)
				instance.setManaType(mana);
			instance.setMana(tag.getFloat(MANA));
			instance.setTrace(tag.getBoolean(TRACE));
		}
    }

	 public static class Implementation implements IManaItemData
	 {
		 private final float maxMana = RoguelikeHelper.MAX_MANA - 1;
		 private float range;
		 private float force;
		 private float mana;
		 private EnumManaType manaType;
		 private int tick;
		 private IManaElement element;
		 private boolean trace;

		 public Implementation(IManaElement element) {
			 this.element = element;
			 this.manaType = EnumManaType.NONE;
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

			 if(this.mana + mana <= this.maxMana)
			 	this.mana += mana;
			 else
			 {
				 extra = this.mana + mana - this.maxMana;
				 this.mana = this.maxMana;
			 }

			 return extra;
		 }

		 @Override
		 public float getMaxMana() {
			 return maxMana;
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
			 return manaType;
		 }

		 @Override
		 public void setManaType(EnumManaType type) {
			 manaType = type;
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
