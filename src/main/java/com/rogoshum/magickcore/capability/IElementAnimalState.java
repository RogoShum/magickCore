package com.rogoshum.magickcore.capability;

import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.buff.ManaBuff;
import net.minecraft.entity.Entity;

import java.util.HashMap;

public interface IElementAnimalState {
	public IManaElement getElement();
	public void setElement(IManaElement element);
}
