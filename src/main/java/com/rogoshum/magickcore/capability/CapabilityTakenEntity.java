package com.rogoshum.magickcore.capability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.UUID;

public class CapabilityTakenEntity {
	public static final String UUID = "UUID";
	public static class Storage<T extends ITakenState> implements Capability.IStorage<T>
    {
		@Override
		public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
			CompoundNBT tag = new CompoundNBT();
			tag.putUniqueId(UUID, instance.getOwnerUUID());
			tag.putInt("TIME", instance.getTime());
			return tag;
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
			CompoundNBT tag = (CompoundNBT) nbt;
			if(tag.hasUniqueId(UUID))
				instance.setOwner(tag.getUniqueId(UUID));
			instance.setTime(tag.getInt("TIME"));
		}
    }

	 public static class Implementation implements ITakenState
	 {
		 private UUID owner;
		 private int time;
		 private int range;
		 public Implementation() { owner = MagickCore.emptyUUID; }

		 @Override
		 public void setOwner(UUID entityIn) {
			 owner = entityIn;
		 }

		 @Override
		 public UUID getOwnerUUID() {
			 return owner;
		 }

		 @Override
		 public void setTime(int time) {
			this.time = time;
		 }

		 @Override
		 public int getTime() {
			 return time;
		 }

		 @Override
		 public int getRange() {
			 return range;
		 }

		 @Override
		 public void tick(MobEntity entity) {
			 range = (int) entity.getAttributeValue(Attributes.FOLLOW_RANGE);
			 if(time > 0)
				 this.time--;

			 if(time == 0)
			 	this.owner = MagickCore.emptyUUID;
		 }
	 }

	public static class TakenEntityProvider implements ICapabilitySerializable<INBT> {
		@CapabilityInject(ITakenState.class)
		public static Capability<ITakenState> capability = null;
		private LazyOptional<ITakenState> instance = LazyOptional.of(capability::getDefaultInstance);

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
