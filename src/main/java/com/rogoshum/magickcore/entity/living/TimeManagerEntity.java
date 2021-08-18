package com.rogoshum.magickcore.entity.living;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class TimeManagerEntity extends RelicEntity{
    public TimeManagerEntity(EntityType<? extends TimeManagerEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	public boolean canCollide(Entity entity) {
		return false;
	}

	@Override
	public void onCollideWithPlayer(PlayerEntity entityIn) {

	}

	@Override
	public void applyEntityCollision(Entity entityIn) {}

	@Override
	public void tick() {
		super.tick();
	}
}
