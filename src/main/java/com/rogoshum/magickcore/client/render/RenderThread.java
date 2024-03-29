package com.rogoshum.magickcore.client.render;

import com.google.common.collect.Queues;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.IEasyRender;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.particle.LitParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class RenderThread extends Thread {
    private HashMap<RenderMode, Queue<Consumer<RenderParams>>> glFunction = new HashMap<>();
    private final ConcurrentLinkedQueue<IEasyRender> renderer = new ConcurrentLinkedQueue<>();

    private volatile boolean needUpdate = false;

    private Frustum clippinghelper;

    public RenderThread(String name) {
        super(name);
    }

    public void addRenderer(IEasyRender renderer) {
        this.renderer.add(renderer);
    }

    public ConcurrentLinkedQueue<IEasyRender> getRenderer() {
        return renderer;
    }


    //renderLevelLastEvent invoke 1
    //render
    public HashMap<RenderMode, Queue<Consumer<RenderParams>>> getGlFunction() {
        return glFunction;
    }

    //renderLevelLastEvent invoke 2
    public void update() {
        needUpdate = true;
    }

    public void setFrustum(Frustum clippingHelper) {
        clippinghelper = clippingHelper;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            if(Minecraft.getInstance().level == null) {
                renderer.clear();
            }
            if(needUpdate && clippinghelper != null) {
                HashMap<RenderMode, Queue<Consumer<RenderParams>>> function = new HashMap<>();
                Iterator<IEasyRender> it = renderer.iterator();
                double scale = Math.max((renderer.size() * 0.002d), 1d);
                try {
                    while (it.hasNext()) {
                        IEasyRender renderer = it.next();
                        if(!renderer.alive()) {
                            renderer.setShouldRender(false);
                            it.remove();
                            continue;
                        }
                        if(!renderer.forceRender()) {
                            if(!clippinghelper.isVisible(renderer.boundingBox())) {
                                renderer.setShouldRender(false);
                                continue;
                            }
                            Vec3 vec = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                            if(!RenderHelper.isInRangeToRender3d(renderer, vec.x, vec.y, vec.z, scale)) {
                                renderer.setShouldRender(false);
                                continue;
                            }
                            if(renderer instanceof LitParticle) {
                                if(!RenderHelper.shouldRender((LitParticle) renderer)) {
                                    renderer.setShouldRender(false);
                                    continue;
                                }
                            } else if(!RenderHelper.shouldRender(renderer.boundingBox())) {
                                renderer.setShouldRender(false);
                                continue;
                            }
                        }
                        renderer.setShouldRender(true);
                        renderer.update();
                        if(!renderer.hasRenderer()) {
                            continue;
                        }
                        HashMap<RenderMode, Consumer<RenderParams>> render = renderer.getRenderFunction();
                        if(render != null) {
                            for (RenderMode bufferMode : render.keySet()) {
                                if(!function.containsKey(bufferMode))
                                    function.put(bufferMode, Queues.newArrayDeque());
                                function.get(bufferMode).add(render.get(bufferMode));
                            }
                        }

                        if(RenderHelper.showDebug()) {
                            render = renderer.getDebugFunction();
                            if(render != null) {
                                for (RenderMode bufferMode : render.keySet()) {
                                    if(!function.containsKey(bufferMode))
                                        function.put(bufferMode, Queues.newArrayDeque());
                                    function.get(bufferMode).add(render.get(bufferMode));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    clearFunction();
                    glFunction = function;
                    needUpdate = false;
                    this.interrupt();
                    MagickCore.LOGGER.warn("Something wrong when render the entity!");
                    e.printStackTrace();
                }
                glFunction = function;
                needUpdate = false;
            }
        }
    }

    public void clearFunction() {
        glFunction.keySet().forEach(renderMode -> glFunction.put(renderMode, Queues.newArrayDeque()));
    }

    @Override
    public void interrupt() {
        super.interrupt();
        renderer.clear();
        clearFunction();
    }
}
