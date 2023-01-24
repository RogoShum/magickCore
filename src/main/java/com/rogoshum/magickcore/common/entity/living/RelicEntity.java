package com.rogoshum.magickcore.common.entity.living;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class RelicEntity extends Mob {

	protected RelicEntity(EntityType<? extends Mob> type, Level worldIn) {
		super(type, worldIn);
		this.getAttributes().getInstance(Attributes.MAX_HEALTH);
	}

}
