package com.rogoshum.magickcore.capability;

import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.buff.ManaBuff;
import com.rogoshum.magickcore.init.ModBuff;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface IEntityState {
	public void setElemented();
	public boolean allowElement();

	public IManaElement getElement();
	public void setElement(IManaElement element);

	public float getManaValue();
	public void setManaValue(float mana);

	public void hitElementShield();
	public int getElementCooldown();
	public void releaseMagick();
	public int getManaCooldown();

	public float getElementShieldMana();
	public void setElementShieldMana(float mana);

	public float getMaxManaValue();
	public void setMaxManaValue(float mana);

	public float getFinalMaxElementShield();
	public void setFinalMaxElementShield(float mana);
	public float getMaxElementShieldMana();
	public void setMaxElementShieldMana(float mana);

	public boolean applyBuff(ManaBuff buff);
	public boolean setBuff(String type, int tick, int force);
	public HashMap<String, ManaBuff> getBuffList();
	public void removeBuff(String type);
	public void tick(Entity entity);

	public boolean getIsDeprived();
	public void setDeprived();
}
