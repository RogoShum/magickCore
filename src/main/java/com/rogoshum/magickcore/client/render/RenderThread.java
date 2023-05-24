package com.rogoshum.magickcore.client.render;

import com.google.common.collect.Queues;
import com.mojang.math.Vector4f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.api.render.easyrender.IEasyRender;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.client.particle.LitParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class RenderThread extends Thread {
    private Queue<Consumer<RenderParams>> ORIGIN_GL_FUNCTIONS = Queues.newArrayDeque();
    private HashMap<RenderMode, Queue<Consumer<RenderParams>>> SOLID_GL_FUNCTIONS = new HashMap<>();
    private HashMap<RenderMode, Queue<Consumer<RenderParams>>> GL_FUNCTIONS = new HashMap<>();
    private HashMap<RenderMode, Queue<Consumer<RenderParams>>> SHADER_GL_FUNCTIONS = new HashMap<>();
    private Queue<Vector4f> COLOR_LIGHT = Queues.newArrayDeque();
    private final ConcurrentLinkedQueue<IEasyRender> RENDERERS = new ConcurrentLinkedQueue<>();

    private volatile boolean NEED_UPDATE = false;
    private int LAST_TICK;

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
    public Queue<Consumer<RenderParams>> getOriginalGlFunction() {
        return ORIGIN_GL_FUNCTIONS;
    }
    public HashMap<RenderMode, Queue<Consumer<RenderParams>>> getSolidGlFunction() {
        return SOLID_GL_FUNCTIONS;
    }

    public HashMap<RenderMode, Queue<Consumer<RenderParams>>> getGlFunction() {
        return GL_FUNCTIONS;
    }
    public HashMap<RenderMode, Queue<Consumer<RenderParams>>> getShaderGlFunction() {
        return SHADER_GL_FUNCTIONS;
    }

    public Queue<Vector4f> getLight() {
        return COLOR_LIGHT;
    }

    //renderWorldLastEvent invoke 2
    public void update() {
        NEED_UPDATE = true;
    }

    public void setClippingHelper(Frustum clippingHelper) {
        CLIPPING_HELPER = clippingHelper;
    }

    public static float packVec4(Vector4f vec) {
        int r = (int) (vec.x() * 99);
        int g = (int) (vec.y() * 99);
        int b = (int) (vec.z() * 99);
        int a = (int) vec.w();
        return r * 1000000 + g * 10000 + b * 100 + a;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            executeTask();
        }
    }

    public void executeTask() {
        if(Minecraft.getInstance().level == null) {
            RENDERERS.clear();
        }
        if(NEED_UPDATE && CLIPPING_HELPER != null) {
            Queue<Consumer<RenderParams>> original_function = Queues.newArrayDeque();
            HashMap<RenderMode, Queue<Consumer<RenderParams>>> solid_function = new HashMap<>();
            HashMap<RenderMode, Queue<Consumer<RenderParams>>> function = new HashMap<>();
            HashMap<RenderMode, Queue<Consumer<RenderParams>>> shader_function = new HashMap<>();
            Queue<Vector4f> lights = Queues.newArrayDeque();
            boolean shouldTick = MagickCore.proxy.getRunTick() > LAST_TICK+1;
            if(shouldTick) {
                LAST_TICK = MagickCore.proxy.getRunTick();
            }

            Iterator<IEasyRender> it = RENDERERS.iterator();
            try {
                while (it.hasNext()) {
                    IEasyRender renderer = it.next();
                    if(!shouldTick && !(renderer instanceof LitParticle)) {
                        renderer.updatePosition();
                        continue;
                    }
                    if(!renderer.alive()) {
                        renderer.setShouldRender(false);
                        it.remove();
                        continue;
                    }
                    if(!renderer.forceRender()) {
                        if(RenderHelper.enableColorLighting()) {
                            ILightSourceEntity light = renderer.getLightEntity();
                            if(light != null) {
                                float size = light.getSourceLight()*1.5f;
                                AABB aabb = new AABB(renderer.positionVec(), renderer.positionVec()).inflate(size);
                                if(CLIPPING_HELPER.isVisible(aabb)) {
                                    float colorSize = packVec4(new Vector4f(light.getColor().r(), light.getColor().g(), light.getColor().b(), size));
                                    lights.add(new Vector4f((float) light.positionVec().x, (float) (light.positionVec().y + light.eyeHeight()), (float) light.positionVec().z, colorSize));
                                }
                            }
                        }
                        if(!CLIPPING_HELPER.isVisible(renderer.boundingBox())) {
                            renderer.setShouldRender(false);
                            continue;
                        }
                        Vec3 vec = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                        if(!RenderHelper.isInRangeToRender3d(renderer, vec.x, vec.y, vec.z)) {
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
                    if(render != null)
                        addToQueue(render, original_function, solid_function, function, shader_function);

                    if(RenderHelper.showDebug()) {
                        render = renderer.getDebugFunction();
                        if(render != null)
                            addToQueue(render, original_function, solid_function, function, shader_function);
                    }

                    if(RenderHelper.enableColorLighting()) {
                        render = renderer.getLightFunction();
                        if(render != null)
                            addToQueue(render, original_function, solid_function, function, shader_function);
                    }
                }
            } catch (Exception e) {
                clearFunction();
                GL_FUNCTIONS = function;
                ORIGIN_GL_FUNCTIONS = original_function;
                SHADER_GL_FUNCTIONS = shader_function;
                SOLID_GL_FUNCTIONS = solid_function;
                COLOR_LIGHT = lights;
                NEED_UPDATE = false;
                this.interrupt();
                MagickCore.LOGGER.warn("Something wrong when render the entity!");
                e.printStackTrace();
            }

            if(shouldTick) {
                GL_FUNCTIONS = function;
                ORIGIN_GL_FUNCTIONS = original_function;
                SHADER_GL_FUNCTIONS = shader_function;
                SOLID_GL_FUNCTIONS = solid_function;
                COLOR_LIGHT = lights;
            }
            NEED_UPDATE = false;
        }
    }

    public void addToQueue(HashMap<RenderMode, Consumer<RenderParams>> render, Queue<Consumer<RenderParams>> original_function
            , HashMap<RenderMode, Queue<Consumer<RenderParams>>> solid_function, HashMap<RenderMode, Queue<Consumer<RenderParams>>> function
            , HashMap<RenderMode, Queue<Consumer<RenderParams>>> shader_function) {

        for (RenderMode bufferMode : render.keySet()) {
            Consumer<RenderParams> renderParams = render.get(bufferMode);
            bufferMode.transForQueueRender();
            if(!bufferMode.useShader.isEmpty()) {
                if(!shader_function.containsKey(bufferMode))
                    shader_function.put(bufferMode, Queues.newArrayDeque());
                shader_function.get(bufferMode).add(renderParams);
            } else if(bufferMode.originRender) {
                original_function.add(renderParams);
            } else if(bufferMode.renderType.toString().contains("SolidType")) {
                if(!solid_function.containsKey(bufferMode))
                    solid_function.put(bufferMode, Queues.newArrayDeque());
                solid_function.get(bufferMode).add(renderParams);
            } else {
                if(!function.containsKey(bufferMode))
                    function.put(bufferMode, Queues.newArrayDeque());
                function.get(bufferMode).add(renderParams);
            }
        }
    }

    public void clearFunction() {
        ORIGIN_GL_FUNCTIONS.clear();
        GL_FUNCTIONS.clear();
        SHADER_GL_FUNCTIONS.clear();
        SOLID_GL_FUNCTIONS.clear();
        COLOR_LIGHT.clear();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        RENDERERS.clear();
        clearFunction();
    }
}
