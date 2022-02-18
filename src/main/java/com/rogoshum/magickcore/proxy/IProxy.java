package com.rogoshum.magickcore.proxy;

import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.event.ShaderEvent;

public interface IProxy {
    public void init();

    public void preInit();

    public int getTick();

    public void registerHandlers();
    public void addMagickParticle(LitParticle par);

    public ElementRenderer getElementRender(String string);
}
