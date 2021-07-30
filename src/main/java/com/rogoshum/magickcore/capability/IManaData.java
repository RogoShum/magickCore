package com.rogoshum.magickcore.capability;

import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.enums.EnumTargetType;
import com.rogoshum.magickcore.api.IManaElement;

import java.util.UUID;

public interface IManaData {
	
	public IManaElement getElement();
	public void setElement(IManaElement element);

	public UUID getTraceTarget();
	public void setTraceTarget(UUID uuid);

	public float getForce();
	public void setForce(float force);

	public int getTickTime();
	public void setTickTime(int tick);

	public float getRange();
	public void setRange(float range);

	public EnumTargetType getTargetType();
	public void setTargetType(EnumTargetType type);

	public EnumManaType getManaType();
	public void setManaType(EnumManaType type);
}
