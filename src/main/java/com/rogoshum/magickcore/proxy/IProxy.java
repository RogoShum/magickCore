package com.rogoshum.magickcore.proxy;

import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.event.ShaderEvent;
import net.minecraftforge.fml.LogicalSide;

public interface IProxy {
    public void init();

    public void preInit();

    public void tick(LogicalSide side);
    public int getRunTick();

    public void addTask(Runnable run);

    public void registerHandlers();

    public void addMagickParticle(LitParticle par);

    public ElementRenderer getElementRender(String string);

    public void createThread();
}
