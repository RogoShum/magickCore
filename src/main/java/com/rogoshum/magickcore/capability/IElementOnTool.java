package com.rogoshum.magickcore.capability;

import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.buff.ManaBuff;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.HashMap;

public interface IElementOnTool {
	public void tick(LivingEntity entity);

	public void setAdditionDamage(int level);
	public float applyAdditionDamage(float amount);

	public void consumeElementOnTool(LivingEntity entity, String element);
}
