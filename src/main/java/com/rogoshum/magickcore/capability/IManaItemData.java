package com.rogoshum.magickcore.capability;

import com.rogoshum.magickcore.api.EnumManaType;
import com.rogoshum.magickcore.api.EnumTargetType;
import com.rogoshum.magickcore.api.IManaElement;

import java.util.UUID;

public interface IManaItemData {
	
	public IManaElement getElement();
	public void setElement(IManaElement element);

	public float getForce();
	public void setForce(float force);

	public float getMana();
	public void setMana(float mana);
	public float receiveMana(float mana);
	public float getMaxMana();

	public int getTickTime();
	public void setTickTime(int tick);

	public float getRange();
	public void setRange(float range);

	public boolean getTrace();
	public void setTrace(boolean trace);

	public EnumManaType getManaType();
	public void setManaType(EnumManaType type);
}
