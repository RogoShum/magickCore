package com.rogoshum.magickcore.common.entity.living;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class TimeManagerEntity extends RelicEntity{
    public TimeManagerEntity(EntityType<? extends TimeManagerEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return false;
	}

	@Override
	public void playerTouch(PlayerEntity entityIn) {

	}

	@Override
	public void push(Entity entityIn) {}

	@Override
	public void tick() {
		super.tick();
	}
}
