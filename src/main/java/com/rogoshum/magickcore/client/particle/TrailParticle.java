package com.rogoshum.magickcore.client.particle;

import com.rogoshum.magickcore.api.ISpellContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class TrailParticle{
	private final Entity owner;
	private final Vector3d position;
	private final int spacing;
	private int tick;

	private final Vector3d[] trailPosition;
	
	public TrailParticle(Entity owner, Vector3d position, int amount, int spacing) {
		this.owner = owner;
		this.position = position;
		this.trailPosition = new Vector3d[amount];
		for(int c = 0; c < amount; c++ ) {
			trailPosition[c] = owner.getPositionVec().add(position);
		}
		this.spacing = spacing;
	}

	public void tick() {
		++tick;
		if(tick % spacing != 0) return;
		for(int i = trailPosition.length - 1; i >= 1; -- i) {
			trailPosition[i] = trailPosition[i - 1];
		}
		double x = owner.lastTickPosX + (owner.getPosX() - owner.lastTickPosX) * (double) Minecraft.getInstance().getRenderPartialTicks();
		double y = owner.lastTickPosY + (owner.getPosY() - owner.lastTickPosY) * (double) Minecraft.getInstance().getRenderPartialTicks();
		double z = owner.lastTickPosZ + (owner.getPosZ() - owner.lastTickPosZ) * (double) Minecraft.getInstance().getRenderPartialTicks();
		trailPosition[0] = new Vector3d(x, y, z).add(position);
	}

	public Vector3d[] getTrailPoint() {return this.trailPosition;}
}
