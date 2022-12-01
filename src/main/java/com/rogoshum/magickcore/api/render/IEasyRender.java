package com.rogoshum.magickcore.api.render;

import com.rogoshum.magickcore.api.entity.IPositionEntity;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;

import java.util.HashMap;
import java.util.function.Consumer;

public interface IEasyRender extends IPositionEntity {
    HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction();
    HashMap<RenderMode, Consumer<RenderParams>> getDebugFunction();
    void update();
    boolean alive();

    default boolean forceRender() {
        return false;
    }
}
