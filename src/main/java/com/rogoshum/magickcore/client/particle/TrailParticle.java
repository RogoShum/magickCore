package com.rogoshum.magickcore.client.particle;

import com.rogoshum.magickcore.api.IMagickElementObject;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

public class TrailParticle{
	private IMagickElementObject owner;
	private Vector3d position;
	private double spacing;
	private Vector3d motion;

	private Vector3d[] trailPosition= new Vector3d[0];
	
	public TrailParticle(IMagickElementObject owner, Vector3d position, int amount, double spacing)
	{
		this.owner = owner;
		this.position = position;
		this.trailPosition = new Vector3d[amount];
		for(int c = 0; c < amount; c++ )
		{
			if(owner instanceof Entity)
				trailPosition[c] = ((Entity)owner).getPositionVec().add(position);
		}
		this.spacing = spacing;
		this.motion = new Vector3d(0, 0, 0);
	}

	public void setMotion(Vector3d motion) {this.motion = motion;}

	public void setPosition(Vector3d position) { this.position = position; }
	
	public Vector3d getPosition() { return this.position; }

	public double getSpacing() { return this.spacing; }

	public void tick()
	{
		Vector3d trailVec = new Vector3d(0, 0, 0);

		if(owner instanceof Entity)
			trailVec = ((Entity)owner).getPositionVec().add(this.position);

		for(int c = 0; c < trailPosition.length; c++ )
		{
			if(c > 0)
			{
				//
				if(owner instanceof Entity)
					trailPosition[c] = trailPosition[c].subtract(((Entity)owner).getMotion());

				trailPosition[c] = trailPosition[c].subtract(motion.scale(0.5d));

				Vector3d point = trailPosition[c];
				Vector3d lastPoint = trailPosition[c - 1];

				Vector3d dirct = point.subtract(lastPoint);

				double distance = dirct.length();

				if(distance > spacing)
					trailPosition[c] = dirct.normalize().scale(spacing).add(lastPoint);
			}
			else
				trailPosition[0] = trailVec;
		}
	}

	public Vector3d[] getTrailPoint() {return this.trailPosition;}
}
