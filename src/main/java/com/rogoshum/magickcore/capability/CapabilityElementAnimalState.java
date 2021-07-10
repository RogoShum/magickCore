package com.rogoshum.magickcore.capability;

import com.rogoshum.magickcore.api.EnumManaLimit;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.buff.ManaBuff;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.HashMap;
import java.util.Iterator;

public class CapabilityElementAnimalState {
	public static final String ELEMENT = "ELEMENT";
	public static class Storage<T extends IElementAnimalState> implements Capability.IStorage<T>
    {
		@Override
		public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
			CompoundNBT tag = new CompoundNBT();
			tag.putString(ELEMENT, instance.getElement().getType());
			return tag;
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
			CompoundNBT tag = (CompoundNBT) nbt;
			instance.setElement(ModElements.getElement(tag.getString(ELEMENT)));
		}
    }

	 public static class Implementation implements IElementAnimalState
	 {
		 private IManaElement element;
		 public Implementation(IManaElement element) {
			 this.element = element;
		 }

		 @Override
		 public IManaElement getElement() {
			 return this.element;
		 }

		 @Override
		 public void setElement(IManaElement element) {
			this.element = element;
		 }
	 }

	public static class ElementAnimalStateProvider implements ICapabilitySerializable<INBT> {
		@CapabilityInject(IElementAnimalState.class)
		public static Capability<IElementAnimalState> capability = null;
		private LazyOptional<IElementAnimalState> instance = LazyOptional.of(capability::getDefaultInstance);

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
