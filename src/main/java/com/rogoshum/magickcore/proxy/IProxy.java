package com.rogoshum.magickcore.proxy;

import com.rogoshum.magickcore.api.render.IEasyRender;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IProxy {
    public void init();

    public void preInit();

    public void tick(LogicalSide side);
    public int getRunTick();

    public void addTask(Runnable run);
    public void addAdditionTask(Runnable tryTask, Runnable catchTask);

    public void registerHandlers();

    public void addMagickParticle(LitParticle par);

    public ElementRenderer getElementRender(String string);
    public void addRenderer(Supplier<IEasyRender> renderSupplier);
    public HashMap<RenderMode, Queue<Consumer<RenderParams>>> getGlFunction();
    void updateRenderer();
    public void setClippingHelper(Frustum clippingHelper);

    public void initBlockRenderer();
}
