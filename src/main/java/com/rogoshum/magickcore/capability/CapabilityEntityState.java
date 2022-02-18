package com.rogoshum.magickcore.capability;

import com.rogoshum.magickcore.enums.EnumManaLimit;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.buff.ManaBuff;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibBuff;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import java.util.HashMap;
import java.util.Iterator;

public class CapabilityEntityState{
	public static final String MANA_VALUE = "MANA_VALUE";
	public static final String SHIELD_VALUE = "SHIELD_VALUE";
	public static final String MAX_MANA_VALUE = "MAX_MANA_VALUE";
	public static final String MAX_SHIELD_VALUE = "MAX_SHIELD_VALUE";
	public static final String FINAL_MAX_SHIELD_VALUE = "FINAL_MAX_SHIELD_VALUE";
	public static final String ELEMENT = "ELEMENT";
	public static final String EFFECT_TICK = "EFFECT_TICK";
	public static final String EFFECT_FORCE = "EFFECT_FORCE";
	public static final String ALLOW_ELEMENT = "ALLOW_ELEMENT";

	public static class Storage<T extends IEntityState> implements Capability.IStorage<T>
    {
		@Override
		public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
			CompoundNBT tag = new CompoundNBT();
			tag.putFloat(MANA_VALUE, instance.getManaValue());
			tag.putFloat(SHIELD_VALUE, instance.getElementShieldMana());
			tag.putFloat(MAX_MANA_VALUE, instance.getMaxManaValue());
			tag.putFloat(MAX_SHIELD_VALUE, instance.getMaxElementShieldMana());
			tag.putString(ELEMENT, instance.getElement().getType());
			tag.putBoolean(ALLOW_ELEMENT, !instance.allowElement());
			tag.putFloat(FINAL_MAX_SHIELD_VALUE, instance.getFinalMaxElementShield());
			CompoundNBT effect_tick = new CompoundNBT();
			Iterator<String> i = instance.getBuffList().keySet().iterator();
			while(i.hasNext()) {
				ManaBuff buff = instance.getBuffList().get(i.next());
				effect_tick.putInt(buff.getType(), buff.getTick());
			}
			tag.put(EFFECT_TICK, effect_tick);

			CompoundNBT effect_force = new CompoundNBT();
			Iterator<String> a = instance.getBuffList().keySet().iterator();
			while(a.hasNext()) {
				ManaBuff buff = instance.getBuffList().get(a.next());
				effect_force.putFloat(buff.getType(), buff.getForce());
			}
			tag.put(EFFECT_FORCE, effect_force);

			return tag;
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
			CompoundNBT tag = (CompoundNBT) nbt;
			instance.setMaxManaValue(tag.getFloat(MAX_MANA_VALUE));
			instance.setMaxElementShieldMana(tag.getFloat(MAX_SHIELD_VALUE));
			instance.setManaValue(tag.getFloat(MANA_VALUE));
			instance.setElementShieldMana(tag.getFloat(SHIELD_VALUE));
			instance.setElement(ModElements.getElement(tag.getString(ELEMENT)));
			if(tag.getBoolean(ALLOW_ELEMENT))
				instance.setElemented();
			CompoundNBT effect_tick = tag.getCompound(EFFECT_TICK);
			CompoundNBT effect_force = tag.getCompound(EFFECT_FORCE);
			instance.setFinalMaxElementShield(tag.getFloat(FINAL_MAX_SHIELD_VALUE));
			Iterator<String> tick = effect_tick.keySet().iterator();
			while(tick.hasNext()) {
				String type = tick.next();
				if(effect_force.contains(type))
				{
					instance.applyBuff(ModBuff.getBuff(type).setTick(effect_tick.getInt(type)).setForce(effect_force.getFloat(type)));
				}
			}
		}
    }

	 public static class Implementation implements IEntityState
	 {
		 private float maxManaValue;
		 private float maxShieldValue;
		 private float finalMaxShieldValue;
		 private float manaValue;
		 private float shieldValue;
		 private IManaElement element;
		 private HashMap<String, ManaBuff> buffList = new HashMap<>();
		 public Implementation(IManaElement element) {
			 this.element = element;
		 }

		 private int shieldCooldown;
		 private int manaCooldown;
		 private boolean IsDeprived;
		 private boolean allowElement;

		 @Override
		 public void setElemented() {
			 allowElement = true;
		 }

		 @Override
		 public boolean allowElement() {
			 return !allowElement;
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
		 public float getManaValue() {
			 return this.manaValue;
		 }

		 @Override
		 public void setManaValue(float mana) {
		 	this.manaValue = Math.min(Math.max(0, mana), this.getMaxManaValue());
		 }

		 @Override
		 public void hitElementShield() { shieldCooldown = 120; }

		 @Override
		 public int getElementCooldown() { return shieldCooldown; }

		 @Override
		 public void releaseMagick() { manaCooldown = 30; }

		 @Override
		 public int getManaCooldown() { return manaCooldown; }

		 @Override
		 public float getElementShieldMana() {
			 return this.shieldValue;
		 }

		 @Override
		 public void setElementShieldMana(float mana) {
			 this.shieldValue = mana;
		 }

		 @Override
		 public float getMaxManaValue() {
			 return maxManaValue;
		 }

		 @Override
		 public void setMaxManaValue(float mana) {
			this.maxManaValue = EnumManaLimit.MAX_MANA.limit(mana);
		 }

		 @Override
		 public float getFinalMaxElementShield() {
			 return finalMaxShieldValue;
		 }

		 @Override
		 public void setFinalMaxElementShield(float mana) {
			 finalMaxShieldValue = mana;
		 }

		 @Override
		 public float getMaxElementShieldMana() {
			 return maxShieldValue;
		 }

		 @Override
		 public void setMaxElementShieldMana(float mana) {
			 this.maxShieldValue = mana;
		 }

		 @Override
		 public boolean applyBuff(ManaBuff buff) {
		 	if(!buffList.containsKey(buff.getType())) {
				buffList.put(buff.getType(), buff);
				return true;
			}
		 	else if(buff.canRefreshBuff()) {
				buffList.put(buff.getType(), buff);
				return true;
			}
			 return false;
		 }

		 @Override
		 public boolean setBuff(String type, int tick, int force) {
			 if(buffList.containsKey(type)) {
				 buffList.get(type).setTick(tick).setForce(force);
				 return true;
			 }
			 return false;
		 }

		 @Override
		 public HashMap<String, ManaBuff> getBuffList() {
			 return buffList;
		 }

		 @Override
		 public void removeBuff(String type) {
			 if(buffList.containsKey(type))
				 buffList.remove(type);
		 }

		 @Override
		 public void tick(Entity entity) {

			 EntityEvents.StateCooldownEvent cEvent = new EntityEvents.StateCooldownEvent((LivingEntity) entity, shieldCooldown, false, true);
			 MinecraftForge.EVENT_BUS.post(cEvent);
			 if(shieldCooldown > cEvent.getCooldown())
				 shieldCooldown = cEvent.getCooldown();

			 EntityEvents.StateCooldownEvent cEvent_m = new EntityEvents.StateCooldownEvent((LivingEntity) entity, manaCooldown, true, false);
			 MinecraftForge.EVENT_BUS.post(cEvent_m);
			 if(manaCooldown > cEvent_m.getCooldown())
				 manaCooldown = cEvent_m.getCooldown();

			 EntityEvents.ShieldCapacityEvent shield_value = new EntityEvents.ShieldCapacityEvent((LivingEntity) entity);
			 MinecraftForge.EVENT_BUS.post(shield_value);
			 this.setMaxElementShieldMana(shield_value.getCapacity() + finalMaxShieldValue);

		 	if(shieldCooldown > 0)
				shieldCooldown--;

		 	int shieldRegen = 1;
			 EntityEvents.ShieldRegenerationEvent event = new EntityEvents.ShieldRegenerationEvent((LivingEntity) entity, shieldRegen);
			 MinecraftForge.EVENT_BUS.post(event);
			 shieldRegen = event.getAmount();

		 	if(this.getElementShieldMana() < this.getMaxElementShieldMana() && shieldCooldown <= 0 && shieldRegen > 0 && this.getManaValue() > 1) {
				this.setElementShieldMana(Math.min(this.getMaxElementShieldMana(), this.getElementShieldMana() + shieldRegen));
				this.setManaValue(this.getManaValue() - 1);
			}

			 if(manaCooldown > 0)
				 manaCooldown--;

			 float manaRegen = 1;
			 EntityEvents.ManaRegenerationEvent eventMana = new EntityEvents.ManaRegenerationEvent((LivingEntity) entity, manaRegen);
			 MinecraftForge.EVENT_BUS.post(eventMana);
			 manaRegen = eventMana.getMana();

			 if(manaCooldown <= 0)
				 this.setManaValue(Math.min(this.getMaxManaValue(), this.getManaValue() + manaRegen));

			 Iterator<String> i = buffList.keySet().iterator();
			 while(i.hasNext())
			 {
				 ManaBuff buff = buffList.get(i.next());
				 buff.setTick(buff.getTick() - 1).effectEntity(entity);
				 if(buff.getTick() < 1) {
					 i.remove();
					 if(i.equals(LibBuff.FREEZE))
						 event.getEntityLiving().setSilent(false);
				 }
			 }
		 }

		 @Override
		 public boolean getIsDeprived() {
			 return IsDeprived;
		 }

		 @Override
		 public void setDeprived() {
			 IsDeprived = true;
		 }
	 }

	public static class EntityStateProvider implements ICapabilitySerializable<INBT> {
		@CapabilityInject(IEntityState.class)
		public static Capability<IEntityState> capability = null;
		private LazyOptional<IEntityState> instance = LazyOptional.of(capability::getDefaultInstance);


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
