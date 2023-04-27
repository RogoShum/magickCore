package com.rogoshum.magickcore.client.render;

import com.google.common.collect.Queues;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.easyrender.IEasyRender;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.api.render.RenderHelper;
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
    private HashMap<RenderMode, Queue<Consumer<RenderParams>>> GL_FUNCTIONS = new HashMap<>();
    private final ConcurrentLinkedQueue<IEasyRender> RENDERERS = new ConcurrentLinkedQueue<>();

    private volatile boolean NEED_UPDATE = false;
    private static int LAST_TICK;

    private Frustum CLIPPING_HELPER;

    public RenderThread(String name) {
        super(name);
    }

    public void addRenderer(IEasyRender renderer) {
        this.RENDERERS.add(renderer);
    }

    public ConcurrentLinkedQueue<IEasyRender> getRenderer() {
        return RENDERERS;
    }


    //renderWorldLastEvent invoke 1
    //render
    public HashMap<RenderMode, Queue<Consumer<RenderParams>>> getGlFunction() {
        return GL_FUNCTIONS;
    }

    //renderWorldLastEvent invoke 2
    public void update() {
        NEED_UPDATE = true;
    }

    public void setClippingHelper(Frustum clippingHelper) {
        CLIPPING_HELPER = clippingHelper;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            if(Minecraft.getInstance().level == null) {
                RENDERERS.clear();
            }
            if(NEED_UPDATE && CLIPPING_HELPER != null) {
                HashMap<RenderMode, Queue<Consumer<RenderParams>>> function = new HashMap<>();
                boolean shouldTick = MagickCore.proxy.getRunTick() > LAST_TICK+1;
                if(shouldTick) {
                    LAST_TICK = MagickCore.proxy.getRunTick();
                }

                Iterator<IEasyRender> it = RENDERERS.iterator();
                double scale = Math.max((RENDERERS.size() * 0.002d), 1d);
                try {
                    while (it.hasNext()) {
                        IEasyRender renderer = it.next();
                        if(!shouldTick) {
                            renderer.updatePosition();
                            continue;
                        }
                        if(!renderer.alive()) {
                            renderer.setShouldRender(false);
                            it.remove();
                            continue;
                        }
                        if(!renderer.forceRender()) {
                            if(!CLIPPING_HELPER.isVisible(renderer.boundingBox())) {
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

                        if(RenderHelper.enableColorLighting()) {
                            render = renderer.getLightFunction();
                            if(render != null)
                                for (RenderMode bufferMode : render.keySet()) {
                                    if(!function.containsKey(bufferMode))
                                        function.put(bufferMode, Queues.newArrayDeque());
                                    function.get(bufferMode).add(render.get(bufferMode));
                                }
                        }
                    }
                } catch (Exception e) {
                    clearFunction();
                    GL_FUNCTIONS = function;
                    NEED_UPDATE = false;
                    this.interrupt();
                    MagickCore.LOGGER.warn("Something wrong when render the entity!");
                    e.printStackTrace();
                }

                if(shouldTick)
                    GL_FUNCTIONS = function;
                NEED_UPDATE = false;
            }
        }
    }

    public void clearFunction() {
        GL_FUNCTIONS.keySet().forEach(renderMode -> GL_FUNCTIONS.put(renderMode, Queues.newArrayDeque()));
    }

    @Override
    public void interrupt() {
        super.interrupt();
        RENDERERS.clear();
        clearFunction();
    }
}
