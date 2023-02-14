package com.rogoshum.magickcore.common.entity.living;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TimeManagerEntity extends RelicEntity{
    public TimeManagerEntity(EntityType<? extends TimeManagerEntity> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return false;
	}

	@Override
	public void playerTouch(Player entityIn) {

	}

	@Override
	public void push(Entity entityIn) {}

	@Override
	public void tick() {
		super.tick();
	}
}
