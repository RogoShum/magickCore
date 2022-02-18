package com.rogoshum.magickcore.proxy;

import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.event.ShaderEvent;

public class CommonProxy implements IProxy
{

	public void init() {}
	
	public void preInit() {}

	@Override
	public int getTick() {
		return 0;
	}

	public void registerHandlers()
	{

	}

	public void addMagickParticle(LitParticle par) { }

	public ElementRenderer getElementRender(String string) { return null;}
}
