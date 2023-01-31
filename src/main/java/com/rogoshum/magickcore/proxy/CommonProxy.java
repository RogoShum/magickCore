package com.rogoshum.magickcore.proxy;

import com.rogoshum.magickcore.api.render.IEasyRender;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import net.minecraft.client.renderer.culling.Frustum;
import net.fabricmc.api.EnvType;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CommonProxy implements IProxy {
	private TaskThread magickThread;

	public void init() {}
	
	public void preInit() {}

	@Override
	public void tick(EnvType side) {
		checkThread();
		magickThread.tick();
	}

	@Override
	public int getRunTick() {
		checkThread();
		return magickThread.getTick();
	}

	@Override
	public void addTask(Runnable run) {
		checkThread();
		magickThread.addTask(run);
	}

	@Override
	public void addAdditionTask(Runnable tryTask, Runnable catchTask) {
		checkThread();
		magickThread.setAdditionTask(tryTask);
		magickThread.setAdditionCatch(catchTask);
	}

	public void checkThread() {
		if(magickThread == null || magickThread.isInterrupted() || !magickThread.isAlive())
			createThread();
	}

	public void registerHandlers() {}

	public void addMagickParticle(LitParticle par) { }

	public ElementRenderer getElementRender(String string) { return null;}

	@Override
	public void addRenderer(Supplier<IEasyRender> renderSupplier) {

	}

	@Override
	public HashMap<RenderMode, Queue<Consumer<RenderParams>>> getGlFunction() {
		return null;
	}

	@Override
	public void updateRenderer() {

	}

	@Override
	public void setClippingHelper(Frustum clippingHelper) {

	}

	@Override
	public void initBlockRenderer() {}

	public void createThread() {
		if (magickThread != null) {
			magickThread.interrupt();
		}
		magickThread = new TaskThread("MagickCore ServerThread");
		magickThread.start();
	}
}
