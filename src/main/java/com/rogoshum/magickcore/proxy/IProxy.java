package com.rogoshum.magickcore.proxy;

import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;

public interface IProxy {
    public void init();

    public void preInit();

    public void registerHandlers();
    public void addMagickParticle(LitParticle par);

    public ElementRenderer getElementRender(String string);
}
