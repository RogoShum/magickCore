package com.rogoshum.magickcore.capability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.EnumManaLimit;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.buff.ManaBuff;
import com.rogoshum.magickcore.helper.NBTTagHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
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

public class CapabilityElementOnTool {
	public static class Storage<T extends IElementOnTool> implements Capability.IStorage<T>
    {
		@Override
		public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
			return new CompoundNBT();
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
		}
    }

	 public static class Implementation implements IElementOnTool
	 {
	 	private float addtionDamage;
		 private int tick;
		 @Override
		 public void tick(LivingEntity entity) {
			 HashMap<String, Integer> map = new HashMap<>();

			 for (ItemStack stack : entity.getEquipmentAndArmor()) {
				 CompoundNBT tag = NBTTagHelper.getToolElementTable(stack);
				 if(tag != null)
				 for(String key : tag.keySet())
				 {
				 	 ModElements.getElement(key).getAbility().applyToolElement(stack, 1);
					 map.put(key, map.containsKey(key) ? map.get(key) + 1 : 1);
				 }
			 }

			 for(String key : map.keySet())
			 {
				 ModElements.getElement(key).getAbility().applyToolElement(entity, map.get(key));
			 }

			 if(tick > 0 && map.containsKey(LibElements.SOLAR)) {
				 addtionDamage = (float) Math.pow(1.1, map.get(LibElements.SOLAR));
			 }
			 else
				 addtionDamage = 1;


			 if(tick > 0)
				 tick--;
		 }

		 @Override
		 public void setAdditionDamage(int level) {
			 tick = level;
		 }

		 @Override
		 public float applyAdditionDamage(float amount) {
			 return amount * addtionDamage;
		 }

		 @Override
		 public void consumeElementOnTool(LivingEntity entity, String element) {
			 entity.getEquipmentAndArmor().forEach((s) -> NBTTagHelper.consumeElementOnTool(s, element));
		 }
	 }

	public static class ElementOnToolProvider implements ICapabilitySerializable<INBT> {
		@CapabilityInject(IElementOnTool.class)
		public static Capability<IElementOnTool> capability = null;
		private LazyOptional<IElementOnTool> instance = LazyOptional.of(capability::getDefaultInstance);


		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			return cap == capability ? instance.cast() : LazyOptional.empty();
		}

		@Override
		public INBT serializeNBT() {
			return new CompoundNBT();
		}

		@Override
		public void deserializeNBT(INBT nbt) {
		}
	}
}
