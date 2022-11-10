package com.rogoshum.magickcore.client.render;

import com.google.common.collect.Queues;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.api.render.IEasyRender;
import com.rogoshum.magickcore.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class RenderThread extends Thread {
    private HashMap<RenderMode, Queue<Consumer<RenderParams>>> glFunction = new HashMap<>();
    private final ConcurrentLinkedQueue<IEasyRender> renderer = new ConcurrentLinkedQueue<>();

    private volatile boolean needUpdate = false;

    private ClippingHelper clippinghelper;

    public RenderThread(String name) {
        super(name);
    }

    public void addRenderer(IEasyRender renderer) {
        this.renderer.add(renderer);
    }


    //renderWorldLastEvent invoke 1
    //render
    public HashMap<RenderMode, Queue<Consumer<RenderParams>>> getGlFunction() {
        return glFunction;
    }

    //renderWorldLastEvent invoke 2
    public void update() {
        needUpdate = true;
    }

    public void setClippingHelper(ClippingHelper clippingHelper) {
        clippinghelper = clippingHelper;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            if(Minecraft.getInstance().world == null) {
                renderer.clear();
            }
            if(needUpdate && clippinghelper != null) {
                HashMap<RenderMode, Queue<Consumer<RenderParams>>> function = new HashMap<>();
                Iterator<IEasyRender> it = renderer.iterator();
                try {
                    while (it.hasNext()) {
                        IEasyRender renderer = it.next();
                        if(!renderer.alive()) {
                            it.remove();
                            continue;
                        }
                        if(!renderer.forceRender()) {
                            if(!clippinghelper.isBoundingBoxInFrustum(renderer.boundingBox())) continue;
                            Vector3d vec = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
                            if(!RenderHelper.isInRangeToRender3d(renderer, vec.x, vec.y, vec.z)) continue;
                            if(!RenderHelper.shouldRender(renderer.boundingBox())) continue;
                        }
                        renderer.update();
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
}
