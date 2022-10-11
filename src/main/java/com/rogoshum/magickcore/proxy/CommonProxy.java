package com.rogoshum.magickcore.proxy;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.event.ShaderEvent;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibEntityData;
import com.rogoshum.magickcore.lib.LibRegistry;
import com.rogoshum.magickcore.magick.extradata.EntityExtraData;
import com.rogoshum.magickcore.magick.extradata.ItemExtraData;
import com.rogoshum.magickcore.magick.extradata.entity.ElementToolData;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.magick.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.registry.ObjectRegistry;
import com.rogoshum.magickcore.tool.EntityLightSourceHandler;
import net.minecraftforge.fml.LogicalSide;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

public class CommonProxy implements IProxy
{
	private int preTick;
	private volatile int tick;
	private final List<Runnable> taskList = new Vector<>();
	private Thread magickThread;

	public void init() {}
	
	public void preInit() {}

	@Override
	public void tick(LogicalSide side) {
		if(side == LogicalSide.CLIENT) return;
		int pre = tick;
		tick = 1 + pre;
	}

	@Override
	public int getRunTick() {
		return tick;
	}

	@Override
	public void addTask(Runnable run) {
		taskList.add(run);
	}

	public void registerHandlers() {}

	public void addMagickParticle(LitParticle par) { }

	public ElementRenderer getElementRender(String string) { return null;}

	@Override
	public void createThread() {
		if(magickThread != null) {
			magickThread.interrupt();
		}
		magickThread = new Thread(() -> {
			while (!magickThread.isInterrupted()) {
				try {
					if(tick > preTick) {
						EntityLightSourceHandler.tick(LogicalSide.SERVER);

						for (int i = 0; i < taskList.size(); ++i) {
							taskList.get(i).run();
						}
						taskList.clear();
						preTick = tick;
					}
				} catch (Exception e) {
					MagickCore.LOGGER.info("MagickCore Server Thread Crashed!");
					MagickCore.LOGGER.debug(e);
					taskList.clear();
					createThread();
				}
			}
		}, "MagickCore Server Thread");
		magickThread.start();
	}
}
