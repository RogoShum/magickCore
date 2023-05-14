package com.rogoshum.magickcore.api.render.easyrender;

import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.api.entity.IPositionEntity;
import com.rogoshum.magickcore.client.render.RenderParams;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Consumer;

public interface IEasyRender extends IPositionEntity {
    HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction();
    @Nullable
    ILightSourceEntity getLightEntity();
    @Nullable
    HashMap<RenderMode, Consumer<RenderParams>> getLightFunction();
    HashMap<RenderMode, Consumer<RenderParams>> getDebugFunction();
    void update();
    void updatePosition();
    boolean alive();

    default boolean forceRender() {
        return false;
    }
    default boolean hasRenderer() {return true; }

    default void setShouldRender(boolean should) {}
}
